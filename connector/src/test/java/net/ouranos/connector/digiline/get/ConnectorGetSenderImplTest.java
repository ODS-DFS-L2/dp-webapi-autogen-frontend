package net.ouranos.connector.digiline.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * {@link ConnectorGetSenderImpl} クラスの単体テスト。
 */
@RestClientTest(ConnectorGetSenderImpl.class)
public class ConnectorGetSenderImplTest {

    /**
     * テスト対象の {@link ConnectorGetSenderImpl} インスタンス。
     */
    @InjectMocks
    private ConnectorGetSenderImpl connectorGetSender;

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
        ReflectionTestUtils.setField(connectorGetSender, "baseUrl", "http://localhost:8080");
        // Excluded headers setup
        ReflectionTestUtils.setField(connectorGetSender, "excludedHeaders", List.of("X-Forwarded-For", "X-amzn-"));
    }

    /**
     * クエリパラメータを含むsendメソッドのテスト。
     * GETリクエストが正しいURLに正しいヘッダーで送信され、
     * ボディに正しい値が入ることを検証します。
     */
    @Test
    public void testSend_withquery() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());
        ConnectorGetRequestSendData data = new ConnectorGetRequestSendData("parts", uuid, "query=test", "token", headers);
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/parts?query=test"))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).headers(headers));

        // Act
        ConnectorGetRequestReceivedData<Object> response = connectorGetSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.dataModelTemplate().toString());
        assertEquals(uuid, response.tracking());
        assertEquals("http://test.com", response.headers().getFirst("Link"));
        mockServer.verify();
    }

    /**
     * クエリパラメータを含まないsendメソッドのテスト。
     * GETリクエストが正しいURLに正しいヘッダーで送信され、
     * ボディに正しい値が入ることを検証します。
     */
    @Test
    public void testSend_noquery() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());
        ConnectorGetRequestSendData data = new ConnectorGetRequestSendData("parts", uuid, null, "token", headers);
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/parts"))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).headers(headers));

        // Act
        ConnectorGetRequestReceivedData<Object> response = connectorGetSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.dataModelTemplate().toString());
        assertEquals(uuid, response.tracking());
        assertEquals("http://test.com", response.headers().getFirst("Link"));
        mockServer.verify();
    }

    /**
     * クエリパラメータを含むsendメソッドのテスト。
     * GETリクエストが正しいURLに正しいヘッダーで送信され、
     * ボディに正しい値が入ることを検証します。
     */
    @Test
    public void testSend_withqueryArray() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        headers.add("Link", "http://test.com");
        ConnectorGetRequestSendData data = new ConnectorGetRequestSendData("parts", uuid, "query=[\"test\"]", "token", headers);
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response

        String encodeQuery = "query=[%22test%22]";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/parts?" + encodeQuery))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).header("X-Tracking", uuid.toString()));

        // Act
        ConnectorGetRequestReceivedData<Object> response = connectorGetSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.dataModelTemplate().toString());
        assertEquals(response.tracking(), uuid);
        mockServer.verify();
    }
}