package org.airflights.parser;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import lombok.RequiredArgsConstructor;
import org.airflights.dto.Flight;
import org.airflights.exception.ProviderException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class FlightsParser {
    private final JsonFactory jfactory;

    public List<Flight> parseFlights(@NotNull InputStream inputStream, @NotNull Predicate<Flight> filter) {
        List<Flight> flights = new ArrayList<>();
        try (JsonParser jParser = jfactory.createParser(inputStream)) {
            if (!jParser.nextToken().equals(JsonToken.START_ARRAY)) {
                throw new IllegalArgumentException("Array expected, but found " + jParser.currentToken());
            }
            while (jParser.nextToken() != JsonToken.END_ARRAY) {
                Flight.FlightBuilder flightBuilder = Flight.builder();
                while (jParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jParser.getCurrentName();
                    switch (fieldName) {
                        case "codeShare" -> flightBuilder.codeShare(jParser.getText());
                        case "sourceAirport" -> flightBuilder.sourceAirport(jParser.getText());
                        case "equipment" -> flightBuilder.equipment(jParser.getText());
                        case "airline" -> flightBuilder.airline(jParser.getText());
                        case "destinationAirport" -> flightBuilder.destinationAirport(jParser.getText());
                        case "stops" -> flightBuilder.stops(jParser.getValueAsInt());
                        default -> throw new ProviderException("Unknown field " + fieldName);
                    }
                }

                Flight flight = flightBuilder.build();
                if (filter.test(flight)) {
                    flights.add(flight);
                }
            }
        } catch (IOException e) {
            throw new ProviderException(e);
        }
        return flights;
    }
}
