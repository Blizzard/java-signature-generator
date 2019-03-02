package com.blizzard.documentation.oauth2.demo.signature.oauth2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Used to retrieve arbitrary data from the Blizzard Battle.net APIs, transform the result, and return the result.
 */
public interface ApiCrawler {
    /**
     * Same as {@link #getDataFromRelativePath(String, Map, Class, Boolean)} but specifies that the authorization token must
     * be sent in the header, not in the parameters.
     *
     * This is the default use case method.
     *
     * @param relativePath The relative URL against which to make the request
     * @param params A map of the values to be added to the URL as query parameters
     * @param klazz The class to transform the response into; typically a POJO
     * @param <T>
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    <T> T getDataFromRelativePath(final String relativePath, final Map<String, String> params, Class<T> klazz) throws IOException, URISyntaxException;

    /**
     * Same as {@link #getDataFromRelativePath(String, Map, Class)} but allows for specifying whether the access token will
     * be sent in the header (true) or in the path (false).
     *
     * @param relativePath The path from which to make the request.
     * @param params A map of the values to be added to the URL as query parameters
     * @param klazz The Java class to transform the response into.
     * @param sendInHeader As stated above.
     * @param <T>
     * @return The Response; usually a POJO
     * @throws IOException
     * @throws URISyntaxException
     */
    <T> T getDataFromRelativePath(final String relativePath, final Map<String, String> params, Class<T> klazz, final Boolean sendInHeader) throws IOException, URISyntaxException;
}
