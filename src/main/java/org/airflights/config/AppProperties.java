package org.airflights.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "application")
public class AppProperties {
    private List<URI> providersUrls;
    private Duration endpointsTimeout = Duration.ofSeconds(30);

}
