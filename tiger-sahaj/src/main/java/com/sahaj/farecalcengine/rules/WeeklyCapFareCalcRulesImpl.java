package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.TripDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component("weeklyCapFareCalcRulesImpl")
public class WeeklyCapFareCalcRulesImpl implements FareCalculationRules {



    @Autowired
    WeeklyCapFareCalcHelperService weeklyCapFareCalcHelperService;




    @Override
    public TripDetails execute(List<TripDetails> pastTrips, TripDetails newTrip, Map<FairTypes, BigDecimal> allFares) {
        TripDetails trip = weeklyCapFareCalcHelperService.getFareWithWeeklyCapBenefit(pastTrips,newTrip,allFares);


        if(trip !=null){
            allFares.put(FairTypes.WEEKLYCAPFARE,trip.getCalculatedFare());
        }

        return trip;

    }
}
