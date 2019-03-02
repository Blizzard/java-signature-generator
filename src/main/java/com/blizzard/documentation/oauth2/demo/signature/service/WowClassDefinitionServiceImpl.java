package com.blizzard.documentation.oauth2.demo.signature.service;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.ClassName;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.ApiCrawler;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
/**
 * Uses a cache to infrequently look up class definitions from the server.
 */
public class WowClassDefinitionServiceImpl implements WowClassDefinitionService{
    @Autowired
    private ApiCrawler apiCrawler;

    private LoadingCache<Integer, ClassName> cachedClasses = CacheBuilder.newBuilder()
        .refreshAfterWrite(12, TimeUnit.HOURS) // This data doesn't change frequently at all
        .build(new CacheLoader<Integer, ClassName>() {
            @Override
            public ClassName load(final Integer wowClassId) throws Exception {
                return getClassForClassId(wowClassId);
            }
        });

    @Override
    public ClassName getWoWClass(final Integer wowClassId) throws ExecutionException {
        return cachedClasses.get(wowClassId);
    }

    /**
     * Looks up the classId in the US region. This should be cached for performance reasons.
     * @param classId the classId to look up.
     * @return the {@link ClassName} instance to return
     * @throws IOException if the service is not available
     * @throws URISyntaxException
     */
    protected ClassName getClassForClassId(final Integer classId) throws IOException, URISyntaxException {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("namespace", "static-us");
        queryParams.put("locale", "en_US");

        log.debug(String.format("Cache miss for regionId %s, fetching data.", classId));
        ClassName value = apiCrawler.getDataFromRelativePath(String.format("/data/wow/playable-class/%s", classId), queryParams, ClassName.class);
        log.debug(String.format("Caching regionId %s as %s", classId, value));
        return value;
    }
}
