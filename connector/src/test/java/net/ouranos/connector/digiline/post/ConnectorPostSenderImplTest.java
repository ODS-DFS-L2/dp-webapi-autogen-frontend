package net.ouranos.connector.digiline.post;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;

/**
 * {@link ConnectorPostSenderImpl} クラスの単体テスト。
 */
@RestClientTest(ConnectorPostSenderImpl.class)
public class ConnectorPostSenderImplTest {

    /**
     * テスト対象の {@link ConnectorPostSenderImpl} インスタンス。
     */
    @InjectMocks
    private ConnectorPostSenderImpl connectorPostSender;

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
        ReflectionTestUtils.setField(connectorPostSender, "baseUrl", "http://localhost:8080");
        // Excluded headers setup
        ReflectionTestUtils.setField(connectorPostSender, "excludedHeaders", List.of("X-Forwarded-For", "X-amzn-"));
    }

    /**
     * クエリパラメータを含むsendメソッドのテスト。
     * POSTリクエストが正しいURLに正しいヘッダーで送信され、
     * ボディに正しい値が入ることを検証します。
     */
    @Test
    public void testSend_withquery() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        String queryString = "query=test";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());
        ConnectorPostRequestSendData<String> data = new ConnectorPostRequestSendData("testModel", "{\"key\":\"value\"}", uuid, queryString, "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel?query=test"))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(content().json("{\"key\":\"value\"}"))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withSuccess(data.dataModelTemplate().toString(), MediaType.APPLICATION_JSON).headers(headers));

        // Act
        ConnectorPostRequestReceivedData response = connectorPostSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals("testModel", response.dataModelName());
        assertEquals("{key=value}", response.dataModelTemplate().toString());
        assertEquals(response.tracking(), uuid);
        assertEquals("http://test.com", response.headers().getFirst("Link"));
        assertEquals(response.queryString(), queryString);
        assertEquals(response.statusCode(), HttpStatus.OK);
        mockServer.verify();
    }

    /**
     * クエリパラメータを含まないsendメソッドのテスト。
     * POSTリクエストが正しいURLに正しいヘッダーで送信され、
     * ボディに正しい値が入ることを検証します。
     */
    @Test
    public void testSend_noquery() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());
        ConnectorPostRequestSendData<String> data = new ConnectorPostRequestSendData("testModel", "{\"key\":\"value\"}", uuid, null, "token", headers);

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/testModel"))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(content().json("{\"key\":\"value\"}"))
                  .andExpect(header("X-Tracking", uuid.toString()))
                  .andExpect(header("Authorization", "token"))
                  .andRespond(withSuccess(data.dataModelTemplate().toString(), MediaType.APPLICATION_JSON).headers(headers));

        // Act
        ConnectorPostRequestReceivedData response = connectorPostSender.send(data);

        // Assert
        assertNotNull(response);
        assertEquals("testModel", response.dataModelName());
        assertEquals("{key=value}", response.dataModelTemplate().toString());
        assertEquals(response.tracking(), uuid);
        assertEquals("http://test.com", response.headers().getFirst("Link"));
        assertEquals(response.statusCode(), HttpStatus.OK);
        mockServer.verify();
    }
}
