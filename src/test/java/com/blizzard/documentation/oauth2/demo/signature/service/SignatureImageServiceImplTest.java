package com.blizzard.documentation.oauth2.demo.signature.service;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.CharacterItemsGuild;
import com.blizzard.documentation.oauth2.demo.signature.model.apiresult.ClassName;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.ApiCrawler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.util.Assert;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignatureImageServiceImplTest {

    @Mock
    private ApiCrawler apiCrawler;

    @Mock
    private AppConfig appConfig;

    @Mock
    private WowClassDefinitionService wowClassDefinitionService;

    @InjectMocks
    @Spy
    private SignatureImageServiceImpl signatureImageServiceImpl;

    @Before
    public void setUp() throws Exception {
        signatureImageServiceImpl.setTotalHeight(1);
        signatureImageServiceImpl.setTotalWidth(1);
    }

    @Test
    public void generateSignatureGoldenPath() throws IOException, URISyntaxException, ExecutionException {

        String characterName = "someFakeCharacterName";
        String realmName = "someFakeRealmName";

        CharacterItemsGuild mockedCharacterItemsGuild = mock(CharacterItemsGuild.class);
        ClassName mockedClassName = mock(ClassName.class);

        BufferedImage sampleImage = new BufferedImage(720, 120, BufferedImage.TYPE_INT_RGB);
        BufferedImage backgroundImage = new BufferedImage(720, 120, BufferedImage.TYPE_INT_RGB);
        BufferedImage portraitImage = new BufferedImage(120, 120, BufferedImage.TYPE_INT_RGB);

        doReturn(sampleImage)
            .when(signatureImageServiceImpl).addTextToImage(any(BufferedImage.class), any(CharacterItemsGuild.class), any(ClassName.class));
        doReturn(mockedClassName)
            .when(signatureImageServiceImpl).getClassName(anyInt());
        doReturn(portraitImage)
            .when(signatureImageServiceImpl).getCharacterPortrait(any(CharacterItemsGuild.class));
        doReturn(backgroundImage)
            .when(signatureImageServiceImpl).getImageBackground(any(CharacterItemsGuild.class));
        doReturn(mockedCharacterItemsGuild)
            .when(apiCrawler).getDataFromRelativePath(anyString(), anyMap(), eq(CharacterItemsGuild.class), anyBoolean());
        doReturn(sampleImage)
            .when(signatureImageServiceImpl).addBackgroundToBaseImage(any(BufferedImage.class), any(BufferedImage.class));
        doReturn(sampleImage)
            .when(signatureImageServiceImpl).addPortraitToBaseImage(any(BufferedImage.class), any(BufferedImage.class));

        BufferedImage result = signatureImageServiceImpl.generateSignature(characterName, realmName);

        assertEquals(sampleImage, result);

        Assert.notNull(result, "Result of generateSignature should not be mull.");

        verify(apiCrawler, times(1)).getDataFromRelativePath(anyString(), anyMap(), eq(CharacterItemsGuild.class), eq(true));
        verify(signatureImageServiceImpl, times(1)).addTextToImage(any(BufferedImage.class), eq(mockedCharacterItemsGuild), eq(mockedClassName));
        verify(signatureImageServiceImpl, times(1)).getClassName(anyInt());
        verify(signatureImageServiceImpl, times(1)).getCharacterPortrait(mockedCharacterItemsGuild);
        verify(signatureImageServiceImpl, times(1)).getImageBackground(mockedCharacterItemsGuild);

    }

    @Test
    public void getClassNameGoldenPath() throws ExecutionException {
        ClassName mockedClassName = mock(ClassName.class);

        doReturn("mockedClassName")
            .when(mockedClassName).getName();
        doReturn(mockedClassName)
            .when(wowClassDefinitionService).getWoWClass(anyInt());

        signatureImageServiceImpl.getClassName(1);

        verify(wowClassDefinitionService, times(1)).getWoWClass(1);
    }
}