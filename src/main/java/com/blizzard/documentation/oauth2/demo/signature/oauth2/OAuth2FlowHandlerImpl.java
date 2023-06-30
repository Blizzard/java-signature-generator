package com.blizzard.documentation.oauth2.demo.signature.oauth2;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.config.EnvConfig;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.models.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import sun.net.www.protocol.http.Handler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLStreamHandler;
import java.time.Instant;
import java.util.Base64;

/**
 * {@inheritDoc}
 */
@Service
@Log4j2
public class OAuth2FlowHandlerImpl implements OAuth2FlowHandler {
    @Autowired
    private AppConfig appConfig;
    @Autowired
    private EnvConfig envConfig;
    @Autowired
    private ObjectMapper objectMapper;

    // To allow testing of the URL/Connection
//    private URLStreamHandler urlStreamHandler = new Handler();

    private String token = null;
    private Instant tokenExpiry = null; // Instant when the token will expire

    private final Object tokenLock = new Object();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToken() throws IOException {
        if(isTokenInvalid()){
            log.trace("---");
            log.trace("Fetching/Creating token.");

            String encodedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s", envConfig.getClientId(), envConfig.getClientSecret()).getBytes(appConfig.getEncoding()));

            // ------------------------------------------------- Allows testing/mocking of the URL connection object
            HttpURLConnection con = null;

            try{
            URL url = new URL(appConfig.getTokenUrl(), ""/*, urlStreamHandler*/);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", String.format("Basic %s", encodedCredentials));
            con.setDoOutput(true);
            con.getOutputStream().write("grant_type=client_credentials".getBytes(appConfig.getEncoding()));

            int responseCode = con.getResponseCode();
            log.trace(String.format("Sent 'POST' to %s requesting access token via client credentials grant type.", url));
            log.trace(String.format("Result code: %s", responseCode));

            String response = IOUtils.toString(con.getInputStream(), appConfig.getEncoding());

            log.trace(String.format("Response: %s", response));

            // Reads the JSON response and converts it to TokenResponse class or throws an exception
            TokenResponse tokenResponse = objectMapper.readValue(response, TokenResponse.class);
            synchronized (tokenLock) {
                tokenExpiry = Instant.now().plusSeconds(tokenResponse.getExpires_in());
                token = tokenResponse.getAccess_token();
            }

            log.trace("---");
            } finally {
                if(con != null){
                    con.disconnect();
                }
            }
        }
        synchronized (tokenLock){
            return token;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenInvalid(){
        synchronized (tokenLock) {
            if (token == null) {
                return true;
            }
            if (tokenExpiry == null) {
                return true;
            }
            return Instant.now().isAfter(tokenExpiry);
        }
    }
}
