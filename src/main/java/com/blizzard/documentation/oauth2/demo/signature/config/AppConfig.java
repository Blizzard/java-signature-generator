package com.blizzard.documentation.oauth2.demo.signature.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.URL;

/**
 * Reads in the application.yml and assigns values to the member variables.
 *
 * As a service, it is available to other aspects of the application, to derive data from the application.yml.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
@Data
public class AppConfig {
    private URL tokenUrl;
    private String encoding;
    private URL baseUrl;
    private URL baseImageUrl;
    private Float compressionQuality;
}
