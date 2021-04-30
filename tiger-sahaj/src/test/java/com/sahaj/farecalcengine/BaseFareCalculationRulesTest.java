package com.sahaj.farecalcengine;

import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.FareCalculationRules;
import com.sahaj.farecalcengine.services.ConfigReadService;
import com.sahaj.farecalcengine.utils.CSVDataReadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sahaj.farecalcengine.rules.FareCalculationRules.*;

@ActiveProfiles("test")
@SpringBootTest
public class BaseFareCalculationRulesTest {

    @Autowired
    @Qualifier("baseFareCalcRules")
    private FareCalculationRules baseFareCalcRulesImpl;

    @Autowired
    @Qualifier("ruleConfigBeanTest")
    RuleConfigBean ruleConfigBean;

    @Autowired
    @Qualifier("configReadServiceTest")
    ConfigReadService configReadService;

    @Value("${ruleconfig.farerules.file}")
    private String fareRateRuleConfig;



    @Test
    public void getBaseFareTest() throws CsvValidationException, IOException {
        Mockito.when(ruleConfigBean.getBaseFareRuleConfigs()).thenReturn(CSVDataReadUtil.getFareRuleListFromCSV(fareRateRuleConfig));
        List<TripDetails> pastTrips = CSVDataReadUtil.getTravelInfoListFromCSVInClasspath("travelinfo-getfaretest.csv");;
        TripDetails newTrip = pastTrips.remove(0);
        Map< FairTypes, BigDecimal > allFares = new HashMap<>();

        TripDetails tripDetails = baseFareCalcRulesImpl.execute(pastTrips,newTrip,allFares);
        Assertions.assertEquals(30, tripDetails.getCalculatedFare().intValue());
        Assertions.assertEquals(tripDetails.getCalculatedFare(), allFares.get(FairTypes.BASEFARE));

    }
}