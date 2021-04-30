package com.sahaj.farecalcengine;


import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig;
import com.sahaj.farecalcengine.rules.config.WeeklyDailyFareCappingRuleConfig;
import com.sahaj.farecalcengine.rules.config.BaseFareRuleConfig;
import com.sahaj.farecalcengine.utils.CSVDataReadUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

@SpringBootTest
public class DataParseUtilTest {


    @Test
    public void testReadTravelInformationFromCSV() throws IOException, CsvValidationException {

       List<TripDetails> tripDetailsList =  CSVDataReadUtil.getTravelInfoListFromCSVInClasspath("travelinfo.csv");

        Assertions.assertNotNull(tripDetailsList);
        Assertions.assertNotEquals(0, tripDetailsList.size());
        Assertions.assertEquals(6, tripDetailsList.size());
        Assertions.assertEquals(DayOfWeek.MONDAY, tripDetailsList.get(0).getTripStartTime().getDayOfWeek());
        Assertions.assertEquals(10, tripDetailsList.get(0).getTripStartTime().getHour());
        Assertions.assertEquals(20, tripDetailsList.get(0).getTripStartTime().getMinute());

        Assertions.assertEquals(DayOfWeek.TUESDAY, tripDetailsList.get(4).getTripStartTime().getDayOfWeek());
        Assertions.assertEquals(19, tripDetailsList.get(4).getTripStartTime().getHour());
        Assertions.assertEquals(00, tripDetailsList.get(4).getTripStartTime().getMinute());

    }

    @Test
    public void testReadFareRulesFromCSV() throws IOException, CsvValidationException {
        List<BaseFareRuleConfig> baseFareRuleConfigList =  CSVDataReadUtil.getFareRuleListFromCSV("farerules.csv");
        Assertions.assertNotNull(baseFareRuleConfigList);
        Assertions.assertNotEquals(0, baseFareRuleConfigList.size());
        Assertions.assertEquals(4, baseFareRuleConfigList.size());

        BaseFareRuleConfig testFare1 = new BaseFareRuleConfig(  "1","1", BigDecimal.valueOf(30),BigDecimal.valueOf(25));
        BaseFareRuleConfig testFare2 = new BaseFareRuleConfig(  "1","2", BigDecimal.valueOf(35),BigDecimal.valueOf(30));
        BaseFareRuleConfig testFare3 = new BaseFareRuleConfig(  "2","1", BigDecimal.valueOf(35),BigDecimal.valueOf(30));
        BaseFareRuleConfig testFare4 = new BaseFareRuleConfig(  "2","2", BigDecimal.valueOf(25),BigDecimal.valueOf(20));

        Assertions.assertEquals(testFare1, baseFareRuleConfigList.get(0));
        Assertions.assertEquals(testFare2, baseFareRuleConfigList.get(1));
        Assertions.assertEquals(testFare3, baseFareRuleConfigList.get(2));
        Assertions.assertEquals(testFare4, baseFareRuleConfigList.get(3));

    }

    @Test
    public void testReadFareCapRulesFromCSV() throws IOException, CsvValidationException {
        List<WeeklyDailyFareCappingRuleConfig> weeklyDailyFareCappingRuleConfigList =  CSVDataReadUtil.getFareCappingRuleListFromCSV("farecaprules.csv");
        Assertions.assertNotNull(weeklyDailyFareCappingRuleConfigList);
        Assertions.assertNotEquals(0, weeklyDailyFareCappingRuleConfigList.size());
        Assertions.assertEquals(4, weeklyDailyFareCappingRuleConfigList.size());

        WeeklyDailyFareCappingRuleConfig testFareCap1 = new WeeklyDailyFareCappingRuleConfig(  "1","1", BigDecimal.valueOf(100),BigDecimal.valueOf(500));
        WeeklyDailyFareCappingRuleConfig testFareCap2 = new WeeklyDailyFareCappingRuleConfig(  "1","2", BigDecimal.valueOf(120),BigDecimal.valueOf(600));
        WeeklyDailyFareCappingRuleConfig testFareCap3 = new WeeklyDailyFareCappingRuleConfig(  "2","1", BigDecimal.valueOf(120),BigDecimal.valueOf(600));
        WeeklyDailyFareCappingRuleConfig testFareCap4 = new WeeklyDailyFareCappingRuleConfig(  "2","2", BigDecimal.valueOf(80),BigDecimal.valueOf(400));


        Assertions.assertEquals(testFareCap1, weeklyDailyFareCappingRuleConfigList.get(0));
        Assertions.assertEquals(testFareCap2, weeklyDailyFareCappingRuleConfigList.get(1));
        Assertions.assertEquals(testFareCap3, weeklyDailyFareCappingRuleConfigList.get(2));
        Assertions.assertEquals(testFareCap4, weeklyDailyFareCappingRuleConfigList.get(3));
    }

    @Test
    public void testReadPeakOffpeakConfigFromCSV() throws IOException, CsvValidationException {

        List<PeakOffpeakDecisionRuleConfig> peakOffpeakDecisionRuleConfigs =  CSVDataReadUtil.getPeakOffpeakConfigListFromCSV("peakconfig.csv");
        String expectedData = "[PeakOffpeakDecisionRuleConfig(fromDay=MONDAY, toDay=FRIDAY, fromTime=07:00, toTime=10:30, fromZone=, toZone=, hourType=Peak)," +
                " PeakOffpeakDecisionRuleConfig(fromDay=MONDAY, toDay=FRIDAY, fromTime=17:00, toTime=20:00, fromZone=, toZone=, hourType=Peak), " +
                "PeakOffpeakDecisionRuleConfig(fromDay=SATURDAY, toDay=SUNDAY, fromTime=09:00, toTime=11:00, fromZone=, toZone=, hourType=Peak)," +
                " PeakOffpeakDecisionRuleConfig(fromDay=SATURDAY, toDay=SUNDAY, fromTime=18:00, toTime=22:00, fromZone=, toZone=, hourType=Peak)," +
                " PeakOffpeakDecisionRuleConfig(fromDay=MONDAY, toDay=FRIDAY, fromTime=17:00, toTime=20:00, fromZone=2, toZone=1, hourType=Offpeak)," +
                " PeakOffpeakDecisionRuleConfig(fromDay=SATURDAY, toDay=SUNDAY, fromTime=18:00, toTime=22:00, fromZone=2, toZone=1, hourType=Offpeak)]";
        Assertions.assertEquals(expectedData, peakOffpeakDecisionRuleConfigs.toString());
        System.out.println(peakOffpeakDecisionRuleConfigs);



    }

    }
