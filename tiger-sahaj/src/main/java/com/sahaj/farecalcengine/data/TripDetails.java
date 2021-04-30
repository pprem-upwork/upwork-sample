package com.sahaj.farecalcengine.data;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString
@Builder(toBuilder=true)

public class TripDetails {

    private String from;
    private String to;
    private LocalDateTime tripStartTime;
    private BigDecimal calculatedFare;
    private String explanation;
}
