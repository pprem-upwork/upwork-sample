package com.sahaj.farecalcengine.rules.config;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;


@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class PeakOffpeakDecisionRuleConfig {
    public enum HourType{Peak,Offpeak};
    private DayOfWeek fromDay;
    private DayOfWeek toDay;
    private LocalTime fromTime;
    private LocalTime toTime;
    private String fromZone;
    private String toZone;

    @Builder.Default
    private HourType hourType = HourType.Offpeak;

}
