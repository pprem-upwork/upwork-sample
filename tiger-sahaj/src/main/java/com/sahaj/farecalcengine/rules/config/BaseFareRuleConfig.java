package com.sahaj.farecalcengine.rules.config;

import lombok.*;

import java.math.BigDecimal;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class BaseFareRuleConfig {

    private String fromZone;
    private String toZone;
    private BigDecimal peakRate;
    private BigDecimal offPeakRate;

    public BigDecimal getFareForHourType(PeakOffpeakDecisionRuleConfig.HourType hourType){
        return hourType == PeakOffpeakDecisionRuleConfig.HourType.Peak?peakRate:offPeakRate;
    }
}
