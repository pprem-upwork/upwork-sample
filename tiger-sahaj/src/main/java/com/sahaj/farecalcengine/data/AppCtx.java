package com.sahaj.farecalcengine.data;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
@Data
public class AppCtx {
    @Value("${ruleconfig.farecaprules.file}")
    private String fareCapRuleConfig;
    @Value("${ruleconfig.farerules.file}")
    private String fareRateRuleConfig;
    @Value("${ruleconfig.peakconfig.file}")
    private String peakHourRuleConfig;
}

