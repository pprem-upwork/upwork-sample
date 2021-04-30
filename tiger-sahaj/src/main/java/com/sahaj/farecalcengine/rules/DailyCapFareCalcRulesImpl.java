package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.TripDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("dailyCapFareCalcRules")
public class DailyCapFareCalcRulesImpl implements FareCalculationRules {



    @Autowired
    DayCapFareCalcHelperService dayCapFareCalcHelperService;




    @Override
    public TripDetails execute(List<TripDetails> pastTrips, TripDetails newTrip, Map<FairTypes, BigDecimal> allFares) {
        List<TripDetails> currentDayTrips = dayCapFareCalcHelperService.getAllTripsForGivenDate(pastTrips,newTrip.getTripStartTime().toLocalDate());
        TripDetails tripDetails =  dayCapFareCalcHelperService.calcuateTripFareWithDailyCapBenefit(currentDayTrips,newTrip);
        log.debug("Updated Trip with Fare :: {}, Current Trip Detail:: {}, Past Trips of the Day:: {} ",tripDetails,newTrip, currentDayTrips);
        if(tripDetails!=null){
            allFares.put(FairTypes.DAILYCAPFARE,tripDetails.getCalculatedFare());


        }

        return tripDetails;
    }
}
