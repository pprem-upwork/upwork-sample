package com.sahaj.farecalcengine.rules;

import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.exceptions.DayCapFareCalcException;
import com.sahaj.farecalcengine.rules.config.BaseFareRuleConfig;
import com.sahaj.farecalcengine.services.ConfigReadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig.*;

@Component("baseFareCalcRules")
@Slf4j
public class BaseFareCalcRulesImpl implements FareCalculationRules {

    @Autowired
    RuleConfigBean ruleConfigBean;

    @Autowired
    ConfigReadService configReadService;



    @Override
    public TripDetails execute(List<TripDetails> pastTrips, TripDetails newTrip, Map<FairTypes, BigDecimal> allFares) {
        HourType hourType = configReadService.getHourType(newTrip);
        BigDecimal fare = executeFareRules(newTrip.getFrom(), newTrip.getTo(), hourType);
        log.debug("Base Fare Calculated is ::{},  Trip Details::{}",fare,newTrip);
        allFares.put(FairTypes.BASEFARE,fare);
        TripDetails resTrip = newTrip.toBuilder().calculatedFare(fare).explanation(hourType + "hours Single fare").build();

        return resTrip;
    }



    private BigDecimal executeFareRules(String fromZone, String toZone, HourType hourType) {
        List<BaseFareRuleConfig> baseFareRuleConfigList =  ruleConfigBean.getBaseFareRuleConfigs();
        List<BaseFareRuleConfig> filterFare =  baseFareRuleConfigList.stream()
                .filter(c -> fromZone.equals(c.getFromZone()))
                .filter(c -> toZone.equals(c.getToZone()))
                .collect(Collectors.toList());
        if(filterFare.isEmpty()){
            String errorMessage = String.format("No Matching Fare Rules Found For :: From:%s , To:%s, HourType:%s ",fromZone,toZone,hourType);
            throw new DayCapFareCalcException(errorMessage);
        }
        return filterFare.get(0).getFareForHourType(hourType);
    }



}
