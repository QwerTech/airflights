package org.airflights.service;

import lombok.RequiredArgsConstructor;
import org.airflights.adapter.HttpAdapter;
import org.airflights.config.AppProperties;
import org.airflights.dto.Flight;
import org.airflights.parser.FlightsParser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightsAggregatorService {
    public static final Predicate<Flight> DEFAULT_PREDICATE = f -> true;
    private final HttpAdapter httpAdapter;
    private final FlightsParser flightsParser;
    private final AppProperties appProperties;

    private static <T> boolean fieldEquals(Flight flight,
                                           Function<Flight, T> fieldAccessor, T value) {
        return Objects.equals(fieldAccessor.apply(flight), value);
    }

    private static <T> Predicate<Flight> applyFilterIfPresent(Predicate<Flight> originalPredicate,
                                                              Function<Flight, T> fieldAccessor, T filterValue) {
        if (filterValue != null) {
            return originalPredicate.and(f -> fieldEquals(f, fieldAccessor, filterValue));
        }
        return originalPredicate;
    }

    @NotNull
    private static Predicate<Flight> getFlightPredicate(@NotNull Map<String, String> filters) {
        Predicate<Flight> flightPredicate = DEFAULT_PREDICATE;
        flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getCodeShare, filters.get("codeShare"));
        flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getAirline, filters.get("airline"));
        flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getEquipment, filters.get("equipment"));
        flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getSourceAirport, filters.get("sourceAirport"));
        flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getDestinationAirport, filters.get("destinationAirport"));
        String stops = filters.get("stops");
        if (StringUtils.isNotBlank(stops)) {
            flightPredicate = applyFilterIfPresent(flightPredicate, Flight::getStops, Integer.parseInt(stops));
        }

        return flightPredicate;
    }

    public @NotNull Set<Flight> aggregateFlights(@NotNull Map<String, String> filters) {

        Predicate<Flight> flightPredicate = getFlightPredicate(filters);

        var completableFutures = appProperties.getProvidersUrls().stream().map(httpAdapter::getHttpResponseAsync)
                .map(c -> c.thenApply(f -> flightsParser.parseFlights(f, flightPredicate))).toList();

        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();

        return completableFutures.stream().map(CompletableFuture::join)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }
}
