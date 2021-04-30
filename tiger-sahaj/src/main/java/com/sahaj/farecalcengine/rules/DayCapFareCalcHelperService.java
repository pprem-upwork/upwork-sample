package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.FareCapType;
import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.config.BaseFareRuleConfig;
import com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig;
import com.sahaj.farecalcengine.rules.config.WeeklyDailyFareCappingRuleConfig;
import com.sahaj.farecalcengine.services.ConfigReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DayCapFareCalcHelperService {

    @Autowired
    ConfigReadService configReadService;



    @Autowired
    RuleConfigBean ruleConfigBean;

    public TripDetails calcuateTripFareWithDailyCapBenefit(final List<TripDetails> previousTripsOfTheDay, TripDetails currentTravel) {
        if(!isTripDailyCapEligible(previousTripsOfTheDay)){
            return null;
        }
        BigDecimal spentFareOnDate = calculateTotalCostOfDaysTrips(previousTripsOfTheDay);
        return calculateFinalTripFare(spentFareOnDate, currentTravel);
    }

    boolean isTripDailyCapEligible(List<TripDetails> pastTripsOfTheWeek ){

        if(pastTripsOfTheWeek==null || pastTripsOfTheWeek.isEmpty() ){
            return false;
        }
        return true;

    }

    public BigDecimal calculateTotalCostOfDaysTrips(List<TripDetails> tripsOnTheDay) {
        BigDecimal spentFareOnDate = BigDecimal.ZERO;
        for(TripDetails pastTripDetails : tripsOnTheDay){
            BigDecimal fare  = calculateSingleTripFare(spentFareOnDate, pastTripDetails);
            spentFareOnDate = spentFareOnDate.add(fare);
        }
        return spentFareOnDate;
    }

    private BigDecimal calculateSingleTripFare(BigDecimal spentFareOnDate, TripDetails tripDetails) {
        BigDecimal fare = getBaseFareForTrip(tripDetails);
        BigDecimal dailyFareCap = getFareCapForTrip(tripDetails.getFrom(), tripDetails.getTo(), FareCapType.DAILY);
        BigDecimal cashNeedForDailyCap  = dailyFareCap.subtract(spentFareOnDate);
        cashNeedForDailyCap = configReadService.toZeroIfNegative(cashNeedForDailyCap);
        log.debug("Base Fare:: {}, Day Fare Cap:: {}, Cash Need To reach Daily Cap::{}",fare,dailyFareCap,cashNeedForDailyCap);

        if(cashNeedForDailyCap.compareTo(fare)<0){
            fare = cashNeedForDailyCap;
        }

        return fare;
    }

    private TripDetails calculateFinalTripFare(BigDecimal spentFareOnDate, TripDetails tripDetails) {
        BigDecimal fare = getBaseFareForTrip(tripDetails);
        BigDecimal dailyFareCap = getFareCapForTrip(tripDetails.getFrom(), tripDetails.getTo(), FareCapType.DAILY);
        BigDecimal cashNeedForDailyCap  = dailyFareCap.subtract(spentFareOnDate);
        cashNeedForDailyCap = configReadService.toZeroIfNegative(cashNeedForDailyCap);
        log.debug("Base Fare:: {}, Day Fare Cap:: {}, Cash Need To reach Daily Cap::{}",fare,dailyFareCap,cashNeedForDailyCap);

        TripDetails resTrip = null;
        if(cashNeedForDailyCap.compareTo(fare)<0){

            String explanation = String.format("The Daily Cap Reached %d for zone %s-%s. Charged %d instead of %d",dailyFareCap.intValue(),
                    tripDetails.getFrom(),
                    tripDetails.getTo(),
                    cashNeedForDailyCap.intValue(),
                    fare.intValue());
             fare = cashNeedForDailyCap;
             resTrip = tripDetails.toBuilder().calculatedFare(fare).explanation(explanation).build();
        }

        return resTrip;
    }


    public BigDecimal getBaseFareForTrip(TripDetails tripDetails) {
        String fromZone = tripDetails.getFrom();
        String toZone = tripDetails.getTo();
        PeakOffpeakDecisionRuleConfig.HourType hourType = configReadService.getHourType(tripDetails);
        List<BaseFareRuleConfig> baseFareRuleConfigList = ruleConfigBean.getBaseFareRuleConfigs();
        List<BaseFareRuleConfig> filterFare =  baseFareRuleConfigList.stream()
                .filter(c -> fromZone.equals(c.getFromZone()))
                .filter(c -> toZone.equals(c.getToZone()))
                .collect(Collectors.toList());
        return filterFare.get(0).getFareForHourType(hourType);
    }

    public BigDecimal getFareCapForTrip(String fromZone, String toZone, FareCapType fareCapType) {
        List<WeeklyDailyFareCappingRuleConfig> fareCapRuleList =  ruleConfigBean.getWeeklyDailyFareCappingRuleConfigs();
        List<WeeklyDailyFareCappingRuleConfig> filterFareCap =  fareCapRuleList.stream()
                .filter(c -> fromZone.equals(c.getFromZone()))
                .filter(c -> toZone.equals(c.getToZone()))
                .collect(Collectors.toList());
        return fareCapType == FareCapType.DAILY?filterFareCap.get(0).getDailyCap():filterFareCap.get(0).getWeeklyCap();
    }

    public BigDecimal maxDaycapAmountConfigured(){
        WeeklyDailyFareCappingRuleConfig farecap = ruleConfigBean.getWeeklyDailyFareCappingRuleConfigs()
                .stream()
                .max(Comparator.comparing(WeeklyDailyFareCappingRuleConfig::getDailyCap))
                .orElseThrow(NoSuchElementException::new);
        return farecap.getDailyCap();
    }

    public List<TripDetails> extractAllTripsDetailsWithSameDateOfNewTrip(LocalDate dateForNewTrip,
                                                                          Map<LocalDate, List<TripDetails>> dateWiseTravelInfos) {

        List<TripDetails> currentDayTrips = dateWiseTravelInfos.get(dateForNewTrip);
        if(currentDayTrips==null){
            currentDayTrips = Collections.emptyList();
        }
        return currentDayTrips;
    }



    public Map<LocalDate, List<TripDetails>> segregateTripsWithUniqueTripDate(List<TripDetails> pastTrips) {
        Map<LocalDate, List<TripDetails>> tripsForEachDate = new HashMap<>();
        for(TripDetails t: pastTrips){
            if(tripsForEachDate.containsKey(t.getTripStartTime().toLocalDate())){
                List<TripDetails> trips =  tripsForEachDate.get(t.getTripStartTime().toLocalDate());
                trips.add(t);
            }else {
                List<TripDetails> trips = new ArrayList<>();
                trips.add(t);
                tripsForEachDate.put(t.getTripStartTime().toLocalDate(),trips);
            }
        }


        log.debug("All Previous Trips ::{}", pastTrips);
        log.debug("Trips Map With Key As Date ::{}", tripsForEachDate);
        return tripsForEachDate;

    }

    public List<TripDetails> getAllTripsForGivenDate( List<TripDetails> trips,LocalDate date) {
        Map<LocalDate, List<TripDetails>> tripsForEachDate = segregateTripsWithUniqueTripDate(trips);
        return tripsForEachDate.get(date);

    }


}
