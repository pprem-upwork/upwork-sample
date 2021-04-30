package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.FareCalculationRules.FairTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RuleExecutionService {

    @Autowired
    @Qualifier("baseFareCalcRules")
    private FareCalculationRules baseFareCalcRules;
    @Autowired
    @Qualifier("dailyCapFareCalcRules")
    private FareCalculationRules dailyCapFareCalcRules;
    @Autowired
    @Qualifier("weeklyCapFareCalcRulesImpl")
    private FareCalculationRules weeklyCapFareCalcRulesImpl;



    public TripDetails executeFareCalcRules(List<TripDetails> pastTrips, TripDetails newTrip){



        Map<FairTypes,BigDecimal> fares= new HashMap<>();
        TripDetails tripUpdatedWithBaseFare =  baseFareCalcRules.execute(pastTrips,newTrip,fares);
        TripDetails tripUpdatedWithDayCap =   dailyCapFareCalcRules.execute(pastTrips,newTrip,fares);
        TripDetails tripUpdatedWithWeekCap =  weeklyCapFareCalcRulesImpl.execute(pastTrips,newTrip,fares);



        TripDetails tripWithLeastFear = findLeastFare(tripUpdatedWithBaseFare,tripUpdatedWithDayCap,tripUpdatedWithWeekCap);
        log.debug("Trip with Base Fare: {}, Trip with Daily Cap Fare: {},Trip with Weekly Cap Fare: {}",tripUpdatedWithBaseFare, tripUpdatedWithDayCap,tripUpdatedWithWeekCap);

        return tripWithLeastFear;

    }

    private TripDetails findLeastFare(TripDetails tripUpdatedWithBaseFare, TripDetails tripUpdatedWithDayCap, TripDetails tripUpdatedWithWeekCap) {
        TripDetails tripWithLeastFear = tripUpdatedWithBaseFare;
        if(tripUpdatedWithDayCap!=null){
            tripWithLeastFear = tripWithLeastFear.getCalculatedFare().compareTo(tripUpdatedWithDayCap.getCalculatedFare())<0?tripWithLeastFear:tripUpdatedWithDayCap;
        }
        if(tripUpdatedWithWeekCap!=null){
            tripWithLeastFear = tripWithLeastFear.getCalculatedFare().compareTo(tripUpdatedWithWeekCap.getCalculatedFare())<0?tripWithLeastFear:tripUpdatedWithWeekCap;
        }
        return tripWithLeastFear;

    }


}
