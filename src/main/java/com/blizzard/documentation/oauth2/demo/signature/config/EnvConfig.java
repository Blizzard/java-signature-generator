package com.blizzard.documentation.oauth2.demo.signature.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * Pulls config values specifically from the environment, in this case, from the environment variables specified by
 * {@link #CLIENT_ID_ENVIRONMENT_VARIABLE_NAME} and {@link #CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME}, typically:
 *
 * BLIZZARD_CLIENT_ID
 * and
 * BLIZZARD_CLIENT_SECRET
 *
 * Other classes can import this service and read the values as required for their logic.
 */
@Configuration
@Data
public class EnvConfig {
    public static final String CLIENT_ID_ENVIRONMENT_VARIABLE_NAME = "BLIZZARD_CLIENT_ID";
    public static final String CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME = "BLIZZARD_CLIENT_SECRET";

    private String clientId;
    private String clientSecret;

    @PostConstruct
    public void init(){
        clientId = System.getenv(CLIENT_ID_ENVIRONMENT_VARIABLE_NAME);
        Assert.notNull(clientId, String.format("Environment Variable %s must be specified.", CLIENT_ID_ENVIRONMENT_VARIABLE_NAME));
        Assert.hasText(clientId, String.format("Environment Variable %s must be specified.", CLIENT_ID_ENVIRONMENT_VARIABLE_NAME));

        clientSecret = System.getenv(CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME);
        Assert.notNull(clientSecret, String.format("Environment Variable %s must be specified.", CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME));
        Assert.hasText(clientSecret, String.format("Environment Variable %s must be specified.", CLIENT_SECRET_ENVIRONMENT_VARIABLE_NAME));
    }
}
