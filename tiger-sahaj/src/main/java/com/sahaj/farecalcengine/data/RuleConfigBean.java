package com.sahaj.farecalcengine.data;

import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.rules.config.BaseFareRuleConfig;
import com.sahaj.farecalcengine.rules.config.PeakOffpeakDecisionRuleConfig;
import com.sahaj.farecalcengine.rules.config.WeeklyDailyFareCappingRuleConfig;
import com.sahaj.farecalcengine.utils.CSVDataReadUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;


@Service
public class RuleConfigBean {
    @Value("${ruleconfig.farecaprules.file}")
    private String fareCapRuleConfig;
    @Value("${ruleconfig.farerules.file}")
    private String fareRateRuleConfig;
    @Value("${ruleconfig.peakconfig.file}")
    private String peakHourRuleConfig;


    @Getter
    private List<PeakOffpeakDecisionRuleConfig> peakOffpeakDecisionRuleConfigs;
    @Getter
    private List<WeeklyDailyFareCappingRuleConfig> weeklyDailyFareCappingRuleConfigs;
    @Getter
    private List<BaseFareRuleConfig> baseFareRuleConfigs;

    @PostConstruct
    public void initalize() throws CsvValidationException, IOException {
        peakOffpeakDecisionRuleConfigs =  CSVDataReadUtil.getPeakOffpeakConfigListFromCSV(peakHourRuleConfig);
        weeklyDailyFareCappingRuleConfigs =  CSVDataReadUtil.getFareCappingRuleListFromCSV(fareCapRuleConfig);
        baseFareRuleConfigs =  CSVDataReadUtil.getFareRuleListFromCSV(fareRateRuleConfig);
    }
}
