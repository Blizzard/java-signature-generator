package com.blizzard.documentation.oauth2.demo.signature.oauth2;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.models.TokenResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApiCrawlerImplTest {
    @Mock
    RestTemplate restTemplate;

    @Mock
    private OAuth2FlowHandlerImpl oAuth2FlowHandler;

    @Mock
    private AppConfig appConfig;

    @Spy
    @InjectMocks
    private ApiCrawlerImpl apiCrawler;

    private String fakeToken, relativePath;
    private URL baseUrl;

    @Before
    public void setUp() throws Exception {
        fakeToken = "fakeToken";
        relativePath = "relativePath";
        baseUrl = new URL("http://www.google.com/");

        doReturn(fakeToken).when(oAuth2FlowHandler).getToken();
        doReturn(baseUrl).when(appConfig).getBaseUrl();
    }

    @Test
    public void getDataFromRelativePathWithTwoParamsGoldenPath() throws IOException, URISyntaxException {
        TokenResponse mockTokenResponse = mock(TokenResponse.class);
        doReturn(mockTokenResponse)
            .when(apiCrawler)
            .getDataFromRelativePath(anyString(), anyMap(), eq(TokenResponse.class), eq(Boolean.TRUE));

        Assert.assertEquals(mockTokenResponse, apiCrawler.getDataFromRelativePath(relativePath, new HashMap<>(), TokenResponse.class));
        verify(apiCrawler, times(1))
            .getDataFromRelativePath(eq(relativePath), anyMap(), eq(TokenResponse.class), eq(Boolean.TRUE));
    }

    @Test
    public void getDataFromRelativePathWithThreeParamsHeaderGoldenPath() throws IOException, URISyntaxException {
        TokenResponse mockTokenResponse = mock(TokenResponse.class);
        ResponseEntity<TokenResponse> mockedResponse = mock(ResponseEntity.class);

        doReturn(mockTokenResponse).when(mockedResponse).getBody();
        doReturn(mockedResponse).when(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(TokenResponse.class));

        Assert.assertEquals(mockTokenResponse, apiCrawler.getDataFromRelativePath(relativePath, new HashMap<>(), TokenResponse.class, Boolean.FALSE));
        verify(restTemplate, times(1)).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(TokenResponse.class));
    }

    @Test
    public void getDataFromRelativePathWithThreeParamsParamGoldenPath() throws IOException, URISyntaxException {
        TokenResponse mockTokenResponse = mock(TokenResponse.class);
        ResponseEntity<TokenResponse> mockedResponse = mock(ResponseEntity.class);

        doReturn(mockTokenResponse).when(mockedResponse).getBody();
        doReturn(mockedResponse).when(restTemplate).exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(TokenResponse.class));

        Assert.assertEquals(mockTokenResponse, apiCrawler.getDataFromRelativePath(relativePath, new HashMap<>(), TokenResponse.class, Boolean.FALSE));
        verify(restTemplate, times(1)).exchange(any(URI.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(TokenResponse.class));

    }
}