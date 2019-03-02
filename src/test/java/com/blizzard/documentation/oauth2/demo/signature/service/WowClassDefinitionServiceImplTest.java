package com.blizzard.documentation.oauth2.demo.signature.service;

import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.ClassName;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.ApiCrawler;
import com.google.common.cache.LoadingCache;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WowClassDefinitionServiceImplTest {

    @Mock
    private LoadingCache<Integer, ClassName> cachedClasses;

    @Mock
    private ApiCrawler apiCrawler;

    @InjectMocks
    private WowClassDefinitionServiceImpl wowClassDefinitionService;

    @Test
    public void getWoWClass() throws ExecutionException {
        ClassName mockedClassName = mock(ClassName.class);
        Integer wowClassId = 1;

        doReturn(mockedClassName).when(cachedClasses).get(anyInt());

        Assert.assertEquals(mockedClassName, wowClassDefinitionService.getWoWClass(wowClassId));
        verify(cachedClasses, times(1)).get(wowClassId);
    }

    @Test
    public void getClassForClassId() throws IOException, URISyntaxException {
        ClassName mockedClassName = mock(ClassName.class);
        Integer wowClassId = 1;

        doReturn(mockedClassName).when(apiCrawler).getDataFromRelativePath(anyString(), anyMap(), eq(ClassName.class));

        Assert.assertEquals(mockedClassName, wowClassDefinitionService.getClassForClassId(wowClassId));
        verify(apiCrawler, times(1))
            .getDataFromRelativePath(
                eq(String.format("/data/wow/playable-class/%s", wowClassId)),
                anyMap(),
                eq(ClassName.class)
            );
    }
}