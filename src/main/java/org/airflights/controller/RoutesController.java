package org.airflights.controller;

import lombok.RequiredArgsConstructor;
import org.airflights.config.AppProperties;
import org.airflights.dto.Flight;
import org.airflights.service.FlightsAggregatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class RoutesController {

    private final HttpClient httpClient;

    private final AppProperties appProperties;
    private final FlightsAggregatorService aggregatorService;

    @GetMapping("/routes")
    public Set<Flight> handleRequest(@RequestParam Map<String, String> filters) {
        return aggregatorService.aggregateFlights(filters);
    }

}
