package com.blizzard.documentation.oauth2.demo.signature.service;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.ClassName;

import java.util.concurrent.ExecutionException;

/**
 * A service to look up WoW Class by class id
 */
public interface WowClassDefinitionService {
    /**
     * Looks up class name by id.
     *
     * Uses a cache as this data should not change frequently.
     *
     * @param wowClassId The class id to look up
     * @return The {@link ClassName} instance referencing
     * @throws ExecutionException if there is an issue with the cache loading execution
     */
    ClassName getWoWClass(final Integer wowClassId) throws ExecutionException;
}
