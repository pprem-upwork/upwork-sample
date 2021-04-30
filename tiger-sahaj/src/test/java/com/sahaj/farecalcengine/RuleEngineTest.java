package com.sahaj.farecalcengine;

import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.RuleExecutionService;
import com.sahaj.farecalcengine.utils.CSVDataReadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@SpringBootTest
public class RuleEngineTest {

    @Autowired
    RuleExecutionService ruleExecutionService;



    @Test
    public void testCalculateFareWithWeeklyCapping1() throws CsvValidationException, IOException {
        List<TripDetails> tripDetailsList = CSVDataReadUtil.getTravelInfoListFromCSVInClasspath("travelinfo-weeklycaptest1.csv");
        List<TripDetails> previousTrips = new ArrayList<>();
        for(TripDetails tripDetails : tripDetailsList){
            System.out.println("############################################################");

            TripDetails resTripDetails = ruleExecutionService.executeFareCalcRules(previousTrips, tripDetails);
            previousTrips.add(tripDetails);
            log.debug("TEST:: New Trip With Updated Fare: {}, Previous Trips :{} ,  New Trip:{}",resTripDetails, previousTrips,tripDetails);
            Assertions.assertEquals(tripDetails.getCalculatedFare(),resTripDetails.getCalculatedFare());

        }



    }



}
