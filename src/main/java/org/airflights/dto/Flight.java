package org.airflights.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Flight {
    private String codeShare;
    private String sourceAirport;
    private String equipment;
    private Integer stops;
    private String airline;
    private String destinationAirport;
}
