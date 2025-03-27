package net.ouranos.connector.digiline.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import net.ouranos.connector.digiline.common.exception.VerifyTokenIntrospectionFalseException;
import net.ouranos.domain.digiline.model.AuthenticationTokenInputModel;
import net.ouranos.domain.digiline.model.AuthenticationTokenOutputModel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.test.util.ReflectionTestUtils;

public class TokenIntrospectionTest {

    /**
     * テスト対象の {@link TokenIntrospection} インスタンス。
     */
    @InjectMocks
    private TokenIntrospection tokenIntrospection;

    /**
     * モックされた {@link RestTemplate} インスタンス。
     */
    @Mock
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * テスト前セットアップ。
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(tokenIntrospection, "authUrl", "http://localhost:8080/auth");
        ReflectionTestUtils.setField(tokenIntrospection, "apiKey", "testApiKey");
    }

    /**
     * トークン検証が成功する場合のテスト。
     */
    @Test
    public void testVerifyTokenSuccess() {
        // Arrange
        String token = "Bearer t";
        AuthenticationTokenInputModel authenticationTokenInputModel = new AuthenticationTokenInputModel();
        authenticationTokenInputModel.idToken(token.substring(7)); 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", "testApiKey");
        HttpEntity<AuthenticationTokenInputModel> requestEntity = new HttpEntity<>(authenticationTokenInputModel, headers);
        AuthenticationTokenOutputModel expect = new AuthenticationTokenOutputModel();
        expect.active(true);
        ResponseEntity<AuthenticationTokenOutputModel> responseEntity = new ResponseEntity<AuthenticationTokenOutputModel>(expect, HttpStatusCode.valueOf(200));

        when(restTemplate.postForEntity(eq("http://localhost:8080/auth"), eq(requestEntity), eq(AuthenticationTokenOutputModel.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        assertDoesNotThrow(() -> tokenIntrospection.verifyToken(token));
    }

    /**
     * トークン検証が有効期限切れで失敗する場合のテスト。
     */
    @Test
    public void testVerifyTokenexpired() {
        // Arrange
        String token = "Bearer t";
        AuthenticationTokenInputModel authenticationTokenInputModel = new AuthenticationTokenInputModel();
        authenticationTokenInputModel.idToken(token.substring(7)); 
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", "testApiKey");
        HttpEntity<AuthenticationTokenInputModel> requestEntity = new HttpEntity<>(authenticationTokenInputModel, headers);
        AuthenticationTokenOutputModel expect = new AuthenticationTokenOutputModel();
        expect.active(false);
        ResponseEntity<AuthenticationTokenOutputModel> responseEntity = new ResponseEntity<AuthenticationTokenOutputModel>(expect, HttpStatusCode.valueOf(200));

        when(restTemplate.postForEntity(eq("http://localhost:8080/auth"), eq(requestEntity), eq(AuthenticationTokenOutputModel.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        assertThrows(VerifyTokenIntrospectionFalseException.class, () -> tokenIntrospection.verifyToken(token));
    }

    /**
     * Token=nullで400エラーで返ってくる場合のテスト
     */
    @Test
    public void testVerifyTokennull() {
        // Arrange
        String token = null;
        AuthenticationTokenInputModel authenticationTokenInputModel = new AuthenticationTokenInputModel();
        authenticationTokenInputModel.idToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", "testApiKey");
        HttpEntity<AuthenticationTokenInputModel> requestEntity = new HttpEntity<>(authenticationTokenInputModel, headers);
        when(restTemplate.postForEntity(eq("http://localhost:8080/auth"), eq(requestEntity), eq(AuthenticationTokenOutputModel.class)))
                .thenThrow(HttpClientErrorException.class);

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> tokenIntrospection.verifyToken(token));
    }

    /**
     * Tokenが7文字以下で400エラーで返ってくる場合のテスト
     */
    @Test
    public void testVerifyTokenInvalid() {
        // Arrange
        String token = "Bearer ";
        AuthenticationTokenInputModel authenticationTokenInputModel = new AuthenticationTokenInputModel();
        authenticationTokenInputModel.idToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", "testApiKey");
        HttpEntity<AuthenticationTokenInputModel> requestEntity = new HttpEntity<>(authenticationTokenInputModel, headers);
        when(restTemplate.postForEntity(eq("http://localhost:8080/auth"), eq(requestEntity), eq(AuthenticationTokenOutputModel.class)))
                .thenThrow(HttpClientErrorException.class);

        // Act & Assert
        assertThrows(HttpClientErrorException.class, () -> tokenIntrospection.verifyToken(token));
    }
}
