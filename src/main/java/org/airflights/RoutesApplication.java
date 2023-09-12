package org.airflights;

import org.airflights.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@ConfigurationPropertiesScan
public class RoutesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoutesApplication.class, args);
    }
}
