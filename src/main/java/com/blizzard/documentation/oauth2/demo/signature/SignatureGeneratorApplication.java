package com.blizzard.documentation.oauth2.demo.signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main entry point for a Spring Boot Application. {@link SpringBootApplication}
 *
 * Check out {@link com.blizzard.documentation.oauth2.demo.signature.controller.SignatureController} as that is the main
 * endpoint that starts up as a result of this application starting.
 */
@SpringBootApplication
public class SignatureGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SignatureGeneratorApplication.class, args);
    }
}
