package org.airflights.adapter;

import lombok.RequiredArgsConstructor;
import org.airflights.config.AppProperties;
import org.airflights.exception.HttpException;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HttpAdapter {
    private final AppProperties appProperties;
    private final HttpClient httpClient;

    private HttpRequest getHttpRequest(URI uri) {
        return HttpRequest.newBuilder()
                .GET()
                .timeout(appProperties.getEndpointsTimeout())
                .uri(uri)
                .build();
    }

    public @NotNull CompletableFuture<InputStream> getHttpResponseAsync(URI uri) {
        HttpRequest httpRequest = getHttpRequest(uri);
        var response = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
        return response.thenApply(r -> {
            if (r.statusCode() != 200) {
                throw new HttpException("Invalid response status code " + r.statusCode());
            }
            return r.body();
        });
    }
}
