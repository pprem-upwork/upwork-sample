package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.FareCapType;
import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.FareCalculationRules.FairTypes;
import com.sahaj.farecalcengine.rules.config.WeeklyDailyFareCappingRuleConfig;
import com.sahaj.farecalcengine.services.ConfigReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WeeklyCapFareCalcHelperService {

    @Autowired
    ConfigReadService configReadService;

    @Autowired
    RuleConfigBean ruleConfigBean;

    @Autowired
    DayCapFareCalcHelperService dayCapFareCalcHelperService;

    public TripDetails getFareWithWeeklyCapBenefit(List<TripDetails> pastTripsOfTheWeek, TripDetails newTrip,Map<FairTypes,BigDecimal> allFares) {
        BigDecimal weeklyCap = getWeeklyFareCap(newTrip.getFrom(), newTrip.getTo(), FareCapType.WEEKLY);
        if (!isTripWeeklyCapEligible(pastTripsOfTheWeek,weeklyCap,newTrip)){
            return null;
        }
        List<LocalDate> listLocaDates =  pastTripsOfTheWeek.stream().map(trip->trip.getTripStartTime().toLocalDate()).collect(Collectors.toList());
        log.debug("listLocaDateTimes::{}",listLocaDates);
        Map<LocalDate, List<TripDetails>> dateWiseTravelInfos =
                       dayCapFareCalcHelperService.segregateTripsWithUniqueTripDate(pastTripsOfTheWeek);
        log.debug("dateWiseTravelInfos.keys::{}",dateWiseTravelInfos.keySet());
        log.debug("Trips on same day dateWiseTravelInfos.keys::{}",dateWiseTravelInfos.get(newTrip.getTripStartTime().toLocalDate()));

        BigDecimal effectiveFare = allFares.get(FairTypes.DAILYCAPFARE);
        if(effectiveFare==null){
            effectiveFare =  allFares.get(FairTypes.BASEFARE);
        }



        return calculateFareWithWeeklyCap(
                newTrip,
                dateWiseTravelInfos,
                weeklyCap,
                effectiveFare);

    }

    boolean isTripWeeklyCapEligible(List<TripDetails> pastTripsOfTheWeek,BigDecimal weeklyCap,TripDetails newTrip ){

        int tripsDayOfTheWeek = newTrip.getTripStartTime().getDayOfWeek().getValue();
        int minDaysForWeeklyCap = minDaysRequiredForWeekCapBenefit(weeklyCap);
        if(pastTripsOfTheWeek==null || pastTripsOfTheWeek.isEmpty() || minDaysForWeeklyCap > tripsDayOfTheWeek){
            return false;
        }
        return true;

    }

    public BigDecimal getWeeklyFareCap(String fromZone, String toZone, FareCapType fareCapType) {
        List<WeeklyDailyFareCappingRuleConfig> fareCapRuleList =  ruleConfigBean.getWeeklyDailyFareCappingRuleConfigs();
        List<WeeklyDailyFareCappingRuleConfig> filterFareCap =  fareCapRuleList.stream()
                .filter(c -> fromZone.equals(c.getFromZone()))
                .filter(c -> toZone.equals(c.getToZone()))
                .collect(Collectors.toList());
        return fareCapType == FareCapType.DAILY?filterFareCap.get(0).getDailyCap():filterFareCap.get(0).getWeeklyCap();
    }

    private Map<LocalDate, List<TripDetails>> createTripsMapWithDateAsKey(List<TripDetails> previousTravels) {
        Map<LocalDate,List<TripDetails>> dateWiseTravelInfos =
                previousTravels.stream().collect(Collectors.groupingBy(ti->ti.getTripStartTime().toLocalDate(),
                        Collectors.mapping(ti -> ti,
                                Collectors.collectingAndThen(Collectors.toSet(), ArrayList::new))));
        return dateWiseTravelInfos;
    }


    private List<TripDetails> getSingleDayTripsFromWeekTrips(LocalDate dateForNewTrip,
                                                             Map<LocalDate, List<TripDetails>> dateWiseTravelInfos) {

        List<TripDetails> currentDayTrips = dateWiseTravelInfos.get(dateForNewTrip);
        if(currentDayTrips==null){
            currentDayTrips = Collections.emptyList();
        }
        return currentDayTrips;
    }



    private TripDetails calculateFareWithWeeklyCap(TripDetails tripDetails,
                                                  Map<LocalDate, List<TripDetails>> dateWiseTravelInfos,
                                                  BigDecimal weeklyCap,
                                                  BigDecimal fareWithDayCap) {


            BigDecimal totalSpentOfWeek = calculateTripsCostForWeek(dateWiseTravelInfos);
            log.debug("dateWiseTravelInfos:::{}",dateWiseTravelInfos);
            BigDecimal amountToReachWeeklyCap = weeklyCap.subtract(totalSpentOfWeek);
            amountToReachWeeklyCap = configReadService.toZeroIfNegative(amountToReachWeeklyCap);
            fareWithDayCap = fareWithDayCap==null?BigDecimal.valueOf(Integer.MAX_VALUE):fareWithDayCap;
        BigDecimal finalFare =   amountToReachWeeklyCap.compareTo(fareWithDayCap)>0? fareWithDayCap :amountToReachWeeklyCap;
        TripDetails resTrip = null;
        if(amountToReachWeeklyCap.compareTo(fareWithDayCap)<=0){
            String explanation = String.format("A weekly Cap of %d is reached. Charged %d instead of %d",weeklyCap.intValue(),
                    amountToReachWeeklyCap.intValue(),
                    fareWithDayCap.intValue());
             resTrip = tripDetails.toBuilder().calculatedFare(amountToReachWeeklyCap).explanation(explanation).build();
        }



        log.debug("Final Fare:: {}, Total fare spent this week:{}, Amount Needed To Reach Weekly Cap:: {}, Fare With Day Cap::{}",finalFare,totalSpentOfWeek,amountToReachWeeklyCap,fareWithDayCap);
        return resTrip;

    }

    private BigDecimal calculateTripsCostForWeek(Map<LocalDate, List<TripDetails>> tripsInWeek) {
        BigDecimal totalSpentOfWeek = BigDecimal.ZERO;
        for(Map.Entry<LocalDate,List<TripDetails>> entry : tripsInWeek.entrySet() ){
            BigDecimal dayTotalFare = dayCapFareCalcHelperService.calculateTotalCostOfDaysTrips(entry.getValue());
            totalSpentOfWeek = totalSpentOfWeek.add(dayTotalFare);
        }
        log.debug("Total Spent this week:{}, dates of trips:{}",totalSpentOfWeek,tripsInWeek.keySet() );
        return totalSpentOfWeek;
    }

    private int minDaysRequiredForWeekCapBenefit(BigDecimal weeklyCap) {
        BigDecimal maxDayCap = dayCapFareCalcHelperService.maxDaycapAmountConfigured();
        return weeklyCap.intValue()/maxDayCap.intValue();
    }

}
