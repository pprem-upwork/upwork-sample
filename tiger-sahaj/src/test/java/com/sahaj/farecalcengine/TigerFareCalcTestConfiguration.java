package com.sahaj.farecalcengine;

import com.sahaj.farecalcengine.data.RuleConfigBean;
import com.sahaj.farecalcengine.rules.DayCapFareCalcHelperService;
import com.sahaj.farecalcengine.services.ConfigReadService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class TigerFareCalcTestConfiguration {
    @Bean("ruleConfigBeanTest")
    @Primary
    public RuleConfigBean ruleConfigBean() {
        return Mockito.mock(RuleConfigBean.class);
    }

    @Bean("configReadServiceTest")
    @Primary
    public ConfigReadService configReadService() {
        return Mockito.mock(ConfigReadService.class);
    }

    @Bean("dayCapFareCalcHelperServiceTest")
    @Primary
    public DayCapFareCalcHelperService dayCapFareCalcHelperService(){return Mockito.mock(DayCapFareCalcHelperService.class);}
}