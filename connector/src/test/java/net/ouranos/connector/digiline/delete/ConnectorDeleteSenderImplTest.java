package net.ouranos.connector.digiline.delete;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

/**
 * {@link ConnectorDeleteSenderImpl} クラスの単体テスト。
 */
@RestClientTest(ConnectorDeleteSenderImpl.class)
public class ConnectorDeleteSenderImplTest {

    /**
     * テスト対象の {@link ConnectorDeleteSenderImpl} インスタンス。
     */
    @InjectMocks
    private ConnectorDeleteSenderImpl connectorDeleteSender;

    /**
     * RestClient.Builderのモック。
     */
    @Mock
    private RestClient.Builder mockRestClient;

    /**
     * RestClientAutoConfigurationでBean定義されるRestClient.Builder
     */
    @Autowired
    private RestClient.Builder restClient;

    /**
     * Restサーバのモック。
     */
    private MockRestServiceServer mockServer;

    /**
     * テスト前セットアップ。
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockServer = MockRestServiceServer.bindTo(restClient).build();
        ReflectionTestUtils.setField(connectorDeleteSender, "baseUrl", "http://localhost:8080");
        // Excluded headers setup
        ReflectionTestUtils.setField(connectorDeleteSender, "excludedHeaders", List.of("X-Forwarded-For", "X-amzn-"));
    }

    /**
     * リクエストボディを含むsendメソッドのテスト。
     * DELETEリクエストが正しいURLに正しいヘッダーで送信され、
     * レスポンスステータスがNO_CONTENTであることを検証します。
     * クエリパラメータが含まれる場合かつ、リクエストボディが存在する場合。
     */
    @Test
    public void testSendSuccess_withQuery_withRequestBody() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Object dataModelTemplate = "{\"key\":\"value\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData data = new ConnectorDeleteRequestSendData("testModel", dataModelTemplate, uuid, "query=test", "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel?query=test"))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andExpect(content().string("{\"key\":\"value\"}"))
                  .andRespond(withStatus(HttpStatus.NO_CONTENT).header("X-Tracking", uuid.toString()));

        // Act
        ResponseEntity<Void> response = connectorDeleteSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), uuid.toString());
        mockServer.verify();
    }

    /**
     * クエリパラメータを含むsendメソッドのテスト。
     * DELETEリクエストが正しいURLに正しいヘッダーで送信され、
     * レスポンスステータスがNO_CONTENTであることを検証します。
     * クエリパラメータが含まれる場合かつ、リクエストボディがnullの場合。
     */
    @Test
    public void testSendSuccess_withQuery_withoutRequestBody() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData data = new ConnectorDeleteRequestSendData("testModel", null, uuid, "query=test", "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel?query=test"))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withStatus(HttpStatus.NO_CONTENT).header("X-Tracking", uuid.toString()));
        // Act
        ResponseEntity<Void> response = connectorDeleteSender.send(data);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), uuid.toString());
        mockServer.verify();
    }

    /**
     * クエリパラメータを含まないsendメソッドのテスト。
     * DELETEリクエストが正しいURLに正しいヘッダーで送信され、
     * レスポンスステータスがNO_CONTENTであることを検証します。
     * クエリパラメータが含まれない場合かつ、リクエストボディが存在する場合。
     */
    @Test
    public void testSendSuccess_withoutQuery_withRequestBody() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        Object dataModelTemplate = "{\"key\":\"value\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData data = new ConnectorDeleteRequestSendData("testModel", dataModelTemplate, uuid, null, "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel"))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withStatus(HttpStatus.NO_CONTENT).header("X-Tracking", uuid.toString()));

        // Act
        ResponseEntity<Void> response = connectorDeleteSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), uuid.toString());
        mockServer.verify();
    }

    /**
     * クエリパラメータを含まないsendメソッドのテスト。
     * DELETEリクエストが正しいURLに正しいヘッダーで送信され、
     * レスポンスステータスがNO_CONTENTであることを検証します。
     * クエリパラメータが含まれない場合かつ、リクエストボディがnullの場合。
     */
    @Test
    public void testSendSuccess_withoutQuery_withoutRequestBody() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData data = new ConnectorDeleteRequestSendData("testModel", null, uuid, null, "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel"))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withStatus(HttpStatus.NO_CONTENT).header("X-Tracking", uuid.toString()));
        // Act
        ResponseEntity<Void> response = connectorDeleteSender.send(data);
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), uuid.toString());
        mockServer.verify();
    }
}