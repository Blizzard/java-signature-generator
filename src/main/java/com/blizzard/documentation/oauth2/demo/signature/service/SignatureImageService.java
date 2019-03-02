package com.blizzard.documentation.oauth2.demo.signature.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * A service for generating a signature based on a character name and realm name.
 */
public interface SignatureImageService {
    /**
     * Takes a character name, and programmatic realm name, looks up the character on US realms, and generates a
     * signature for them. Specifically for World of Warcraft.
     *
     * @param characterName The character name to look up.
     * @param realmName The realm the character exists on.
     * @return A {@link BufferedImage} rendering of the character.
     * @throws IOException When the required downstream services are not available.
     * @throws URISyntaxException Should not happen except for breaking API changes or a misconfigured application.yml
     */
    BufferedImage generateSignature(final String characterName, final String realmName) throws IOException, URISyntaxException;
}
