package org.airflights.config;

import com.fasterxml.jackson.core.JsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
public class AppConfig {

    @Bean
    JsonFactory jsonFactory() {
        return new JsonFactory();
    }
    @Bean
    HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }
}
