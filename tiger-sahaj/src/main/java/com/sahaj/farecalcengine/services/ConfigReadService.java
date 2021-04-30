package com.sahaj.farecalcengine.services;

import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigReadService {


    @Autowired
    RuleConfigBean ruleConfigBean;

    public ConfigReadService() {

    }

    public PeakOffpeakDecisionRuleConfig.HourType getHourType(TripDetails tripDetails) {
        return processHourTypeExtractRules(tripDetails);


    }

    public BigDecimal toZeroIfNegative(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;
    }

    public  PeakOffpeakDecisionRuleConfig.HourType processHourTypeExtractRules(TripDetails tripDetails) {


        List<PeakOffpeakDecisionRuleConfig> peakHourTravelConfigs = filterApplicablePeakHourConfigs(tripDetails,
                ruleConfigBean.getPeakOffpeakDecisionRuleConfigs());

        if (!peakHourTravelConfigs.isEmpty()) {
            return findHourTypeByApplyingExceptionRules(peakHourTravelConfigs, tripDetails);
        }

        return PeakOffpeakDecisionRuleConfig.HourType.Offpeak;
    }


    private PeakOffpeakDecisionRuleConfig.HourType findHourTypeByApplyingExceptionRules(List<PeakOffpeakDecisionRuleConfig> peakHourTravelConfigs, TripDetails tripDetails) {
        List<PeakOffpeakDecisionRuleConfig> peakOffpeakDecisionRuleConfigs =  peakHourTravelConfigs.stream()
                .filter(c -> tripDetails.getFrom().equals(c.getFromZone()))
                .filter(c -> tripDetails.getTo().equals(c.getToZone()))
                .collect(Collectors.toList());
        if(!peakOffpeakDecisionRuleConfigs.isEmpty()){
            return PeakOffpeakDecisionRuleConfig.HourType.Offpeak;
        }else {
            return PeakOffpeakDecisionRuleConfig.HourType.Peak;
        }

    }


    private List<PeakOffpeakDecisionRuleConfig> filterApplicablePeakHourConfigs(TripDetails tripDetails, List<PeakOffpeakDecisionRuleConfig> peakOffpeakDecisionRuleConfigs) {
        return peakOffpeakDecisionRuleConfigs.stream().filter(c -> tripDetails.getTripStartTime().getDayOfWeek().getValue() >= c.getFromDay().getValue())
                .filter(c -> tripDetails.getTripStartTime().getDayOfWeek().getValue() <= c.getToDay().getValue())
                .filter(c -> tripDetails.getTripStartTime().toLocalTime().compareTo(c.getFromTime()) >= 0)
                .filter(c -> tripDetails.getTripStartTime().toLocalTime().compareTo(c.getToTime()) <= 0)
                .collect(Collectors.toList());
    }
}