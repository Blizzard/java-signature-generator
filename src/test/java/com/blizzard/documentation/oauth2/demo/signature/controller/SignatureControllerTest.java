package com.blizzard.documentation.oauth2.demo.signature.controller;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.service.SignatureImageService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.awt.image.*;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignatureControllerTest {

    @Mock
    private SignatureImageService mockedSignatureImageService;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private SignatureController signatureController;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void getSignatureGoldenPath() throws IOException, URISyntaxException {
        String mockRealmName = "someFakeRealmName";
        String mockCharacterName = "someFakeCharacterName";
        BufferedImage resultImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        when(mockedSignatureImageService.generateSignature(anyString(), anyString())).thenReturn(resultImage);

        signatureController.getSignature(mockCharacterName, mockRealmName);

        verify(mockedSignatureImageService, times(1)).generateSignature(mockCharacterName, mockRealmName);

    }
}