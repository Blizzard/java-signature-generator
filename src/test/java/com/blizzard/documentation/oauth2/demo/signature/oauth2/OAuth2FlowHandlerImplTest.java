package com.blizzard.documentation.oauth2.demo.signature.oauth2;

import com.blizzard.documentation.oauth2.demo.signature.config.AppConfig;
import com.blizzard.documentation.oauth2.demo.signature.config.EnvConfig;
import com.blizzard.documentation.oauth2.demo.signature.oauth2.models.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OAuth2FlowHandlerImplTest {

    @Mock
    public AppConfig appConfig;
    @Mock
    public EnvConfig envConfig;
    @Mock
    public ObjectMapper objectMapper;

    @InjectMocks
    private OAuth2FlowHandlerImpl oAuth2FlowHandler;

    @Test
    public void getTokenFreshGoldenPath() throws IOException, NoSuchFieldException {
        final String token = "exampleToken";
        final HttpURLConnection mockUrlConnection = mock(HttpURLConnection.class);
        final OutputStream outputStream = mock(OutputStream.class);
        final TokenResponse mockTokenResponse = mock(TokenResponse.class);

        final URLStreamHandler urlStreamHandler = new URLStreamHandler() {
            @Override
            protected URLConnection openConnection(URL u) throws IOException {
                return mockUrlConnection;
            }
        };

        String clientId = "someClientId";
        String clientSecret = "someClientSecret";
        String encodeFormat = "UTF-8";
        int responseCode = 200;

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            String.format("{'access_token':'%s', 'expires_in':'1'}", token).getBytes("UTF-8")
        );

        doReturn(clientId).when(envConfig).getClientId();
        doReturn(clientSecret).when(envConfig).getClientSecret();
        doReturn(encodeFormat).when(appConfig).getEncoding();
        doReturn(new URL("http://www.google.com/")).when(appConfig).getTokenUrl();
        doReturn(mockTokenResponse).when(objectMapper).readValue(anyString(), eq(TokenResponse.class));

        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("urlStreamHandler"), urlStreamHandler);

        doReturn(byteArrayInputStream).when(mockUrlConnection).getInputStream();
        doReturn(outputStream).when(mockUrlConnection).getOutputStream();
        doReturn(responseCode).when(mockUrlConnection).getResponseCode();

        doReturn(token).when(mockTokenResponse).getAccess_token();

        Assert.assertEquals(token, oAuth2FlowHandler.getToken());
        verify(mockUrlConnection, times(1)).setRequestMethod("POST");
        verify(mockUrlConnection, times(1))
            .setRequestProperty(
                "Authorization",
                String.format("Basic %s",
                    Base64.getEncoder().encodeToString(
                        String.format("%s:%s", clientId, clientSecret).getBytes(encodeFormat)
                    )
                )
            );
        verify(mockUrlConnection, times(1)).setDoOutput(true);
        verify(outputStream, times(1)).write("grant_type=client_credentials".getBytes(encodeFormat));
        verify(mockUrlConnection, times(1)).getResponseCode();
    }

    @Test
    public void getTokenCachedGoldenPath() throws NoSuchFieldException, IOException {
        final String token = "myCachedToken";
        // Cached token condition and setting the token
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("tokenExpiry"), Instant.now().plus(5, ChronoUnit.MINUTES));
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("token"), token);

        Assert.assertEquals(token, oAuth2FlowHandler.getToken());
    }

    @Test
    public void isTokenInvalidGoldenPath() throws NoSuchFieldException {
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("tokenExpiry"), Instant.now().plus(5, ChronoUnit.MINUTES));
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("token"), "SomeSampleToken");
        Assert.assertFalse(oAuth2FlowHandler.isTokenInvalid());

    }

    @Test
    public void isTokenInvalidNullToken() throws NoSuchFieldException {
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("tokenExpiry"), null);
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("token"), null);
        Assert.assertTrue(oAuth2FlowHandler.isTokenInvalid());

    }

    @Test
    public void isTokenInvalidExpiredTokenExpiry() throws NoSuchFieldException {
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("tokenExpiry"), Instant.EPOCH);
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("token"), "SomeSampleToken");
        Assert.assertTrue(oAuth2FlowHandler.isTokenInvalid());
    }

    @Test
    public void isTokenInvalidNullTokenExpiry() throws NoSuchFieldException {
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("tokenExpiry"), null);
        FieldSetter.setField(oAuth2FlowHandler, oAuth2FlowHandler.getClass().getDeclaredField("token"), "SomeSampleToken");
        Assert.assertTrue(oAuth2FlowHandler.isTokenInvalid());
    }
}
