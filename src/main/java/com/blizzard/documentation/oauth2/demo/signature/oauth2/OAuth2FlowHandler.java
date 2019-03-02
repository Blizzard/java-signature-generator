package com.blizzard.documentation.oauth2.demo.signature.oauth2;

import java.io.IOException;

/**
 * A service for handling the oAuth2 flow, giving us an access token every time we want to interact with an API.
 */
public interface OAuth2FlowHandler {
    /**
     * Attempts to re-use an existing token if one exists and is valid, otherwise it fetches one based on our client id
     * and client secret. This can be called repeatedly, as the token is stored in memory, and a call is only made if
     * the token is known to have expired.
     *
     * @return The Authorization Token used in future API requests.
     * @throws IOException if the downstream service encounters any issues
     */
    String getToken() throws IOException;

    /**
     * Checks to see if the known token is both valid and not expired.
     *
     * @return True if the token is eligible for future API requests immediately. False otherwise.
     */
    boolean isTokenInvalid();
}
