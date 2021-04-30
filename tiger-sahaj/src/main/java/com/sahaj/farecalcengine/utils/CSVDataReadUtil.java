package com.sahaj.farecalcengine.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.config.BaseFareRuleConfig;
import com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig;
import com.sahaj.farecalcengine.rules.config.WeeklyDailyFareCappingRuleConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CSVDataReadUtil {
    public static List<TripDetails> getTravelInfoListFromCSVInClasspath(String filenameInClasspath) throws IOException, CsvValidationException {
        List<List<String>> travelInfoListString =   parseFileInClassPath(filenameInClasspath);
        return fromStringList2TravelInfoList(travelInfoListString);

    }
    public static List<TripDetails> getTravelInfoListFromCSVWithAbsPath(String filenameWithAbsPath) throws IOException, CsvValidationException {
        List<List<String>> travelInfoListString =   parseFileWithAbsPath(filenameWithAbsPath);
        return fromStringList2TravelInfoList(travelInfoListString);

    }

    private static List<TripDetails> fromStringList2TravelInfoList(List<List<String>> travelInfoListString) {

       return travelInfoListString.stream().map(CSVDataReadUtil::stringList2TravelInfo).collect(Collectors.toList());


    }

    private static TripDetails stringList2TravelInfo(List<String> travelInfoFields) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        TripDetails obj = TripDetails.builder().build() ;
        obj.setTripStartTime(LocalDateTime.parse(travelInfoFields.get(0),formatter));
        obj.setFrom(travelInfoFields.get(1));
        obj.setTo(travelInfoFields.get(2));
        String calculatedFare = travelInfoFields.get(3).trim().isEmpty()?"0":travelInfoFields.get(3);
        obj.setCalculatedFare(new BigDecimal(calculatedFare));
        obj.setExplanation(travelInfoFields.get(4));


        return obj;
    }


    private static  List<List<String>> parseFileInClassPath(String filenameInClasspath) throws IOException, CsvValidationException {

        //InputStream is = ParseDataUtil.class.getResourceAsStream(s);
        Resource resource = new ClassPathResource(filenameInClasspath);
        InputStream is = resource.getInputStream();
        System.out.println("Input Stream->"+is);
        return readRecordsFromStream(new InputStreamReader(is));
    }

    private static  List<List<String>> parseFileWithAbsPath(String filenameWithAbsPath) throws IOException, CsvValidationException {
        return readRecordsFromStream(new FileReader(filenameWithAbsPath));
    }

    private static List<List<String>> readRecordsFromStream( Reader reader) throws IOException, CsvValidationException {
        CSVReader csvReader = new CSVReader(reader);
        List<List<String>> records = new ArrayList<List<String>>();
        String[] values = null;
        while ((values = csvReader.readNext()) != null) {
            records.add(Arrays.asList(values));
        }

        return records;
    }

    public static List<BaseFareRuleConfig> getFareRuleListFromCSV(String s) throws CsvValidationException, IOException {
        List<List<String>> fareRuleListString =   parseFileInClassPath(s);
        return fromStringList2FareRuleList(fareRuleListString);
    }

    private static List<BaseFareRuleConfig> fromStringList2FareRuleList(List<List<String>> fareRuleListString) {
        return fareRuleListString.stream().map(CSVDataReadUtil::stringList2FareRule).collect(Collectors.toList());

    }

    private static BaseFareRuleConfig stringList2FareRule(List<String> fareRuleFields) {
        BaseFareRuleConfig obj = new BaseFareRuleConfig();

        obj.setFromZone(fareRuleFields.get(0));
        obj.setToZone(fareRuleFields.get(1));
        obj.setPeakRate(new BigDecimal(fareRuleFields.get(2)));
        obj.setOffPeakRate(new BigDecimal(fareRuleFields.get(3)));
        return obj;
    }

    public static List<WeeklyDailyFareCappingRuleConfig> getFareCappingRuleListFromCSV(String s) throws CsvValidationException, IOException {
        List<List<String>> fareCappingRuleListString =   parseFileInClassPath(s);
        return fromStringList2FareCappingRulesList(fareCappingRuleListString);
    }

    private static List<WeeklyDailyFareCappingRuleConfig> fromStringList2FareCappingRulesList(List<List<String>> fareCappingRuleListString) {
        return fareCappingRuleListString.stream().map(CSVDataReadUtil::stringList2FareCappingRule).collect(Collectors.toList());

    }

    private static WeeklyDailyFareCappingRuleConfig stringList2FareCappingRule(List<String> fareCappingRuleFields) {
        WeeklyDailyFareCappingRuleConfig obj = new WeeklyDailyFareCappingRuleConfig();

        obj.setFromZone(fareCappingRuleFields.get(0));
        obj.setToZone(fareCappingRuleFields.get(1));
        obj.setDailyCap(new BigDecimal(fareCappingRuleFields.get(2)));
        obj.setWeeklyCap(new BigDecimal(fareCappingRuleFields.get(3)));
        return obj;
    }

    public static List<PeakOffpeakDecisionRuleConfig> getPeakOffpeakConfigListFromCSV(String filename) throws CsvValidationException, IOException {
        List<List<String>> peakOffpeakConfigListString =   parseFileInClassPath(filename);
        return fromStringList2PeakOffpeakConfigList(peakOffpeakConfigListString);
    }

    private static List<PeakOffpeakDecisionRuleConfig> fromStringList2PeakOffpeakConfigList(List<List<String>> peakOffpeakConfigListString) {
        return peakOffpeakConfigListString.stream().map(CSVDataReadUtil::stringList2PeakOffpeakConfig).collect(Collectors.toList());
    }

    private static PeakOffpeakDecisionRuleConfig stringList2PeakOffpeakConfig(List<String>  peakOffpeakConfigFields) {
        PeakOffpeakDecisionRuleConfig obj = new PeakOffpeakDecisionRuleConfig();
        DateTimeFormatter formatter //= DateTimeFormatter.ofPattern("hh:mm");
         = DateTimeFormatter.ISO_LOCAL_TIME;



        obj.setFromDay(DayOfWeek.valueOf(peakOffpeakConfigFields.get(0)));
        obj.setToDay(DayOfWeek.valueOf(peakOffpeakConfigFields.get(1)));
        obj.setFromTime(LocalTime.parse(peakOffpeakConfigFields.get(2),formatter));
        obj.setToTime(LocalTime.parse(peakOffpeakConfigFields.get(3),formatter));
        obj.setFromZone(peakOffpeakConfigFields.get(4));
        obj.setToZone(peakOffpeakConfigFields.get(5));
        obj.setHourType(PeakOffpeakDecisionRuleConfig.HourType.valueOf(peakOffpeakConfigFields.get(6)));


        return obj;

    }
}
