package com.sahaj.farecalcengine.rules.config;

import lombok.*;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class WeeklyDailyFareCappingRuleConfig {

    private String fromZone;
    private String toZone;
    private BigDecimal dailyCap;
    private BigDecimal WeeklyCap;
}
