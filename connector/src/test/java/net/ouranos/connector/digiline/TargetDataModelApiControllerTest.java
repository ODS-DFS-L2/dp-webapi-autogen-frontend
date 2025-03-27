package net.ouranos.connector.digiline;

import net.ouranos.connector.digiline.components.TokenIntrospection;
import net.ouranos.connector.digiline.components.TrackingHeaderBean;
import net.ouranos.connector.digiline.delete.ConnectorDeleteRequestSendData;
import net.ouranos.connector.digiline.delete.ConnectorDeleteSender;
import net.ouranos.connector.digiline.get.ConnectorGetRequestReceivedData;
import net.ouranos.connector.digiline.get.ConnectorGetRequestSendData;
import net.ouranos.connector.digiline.get.ConnectorGetSender;
import net.ouranos.connector.digiline.post.ConnectorPostRequestReceivedData;
import net.ouranos.connector.digiline.post.ConnectorPostRequestSendData;
import net.ouranos.connector.digiline.post.ConnectorPostSender;
import net.ouranos.connector.digiline.put.ConnectorPutRequestReceivedData;
import net.ouranos.connector.digiline.put.ConnectorPutRequestSendData;
import net.ouranos.connector.digiline.put.ConnectorPutSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

/**
 * {@link TargetDataModelApiController} クラスの単体テスト。
 */
@WebMvcTest(TargetDataModelApiController.class)
public class TargetDataModelApiControllerTest {

    /**
     * リクエストのモック
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     *  {@link ConnectorDeleteSender} のモック。
     */
    @MockBean
    private ConnectorDeleteSender connectorDeleteSender;

    /**
     * {@link ConnectorGetSender} のモック。
     */
    @MockBean
    private ConnectorGetSender connectorGetSender;

    /**
     * {@link ConnectorPutSender} のモック。
     */
    @MockBean
    private ConnectorPutSender connectorPutSender;

    /**
     * {@link ConnectorPostSender} のモック。
     */
    @MockBean
    private ConnectorPostSender connectorPostSender;

    /**
     * {@link TrackingHeaderBean} のモック。
     */
    @MockBean
    private TrackingHeaderBean trackingHeaderBean;

    /**
     * {@link TokenIntrospection} のモック。
     */
    @MockBean
    private TokenIntrospection tokenIntrospection;


    /**
     * jsonオブジェクトのマッパー
     */
    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * テスト前セットアップ。
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * {@link TargetDataModelApiController#delete(String, UUID)} のリクエストボディなしのテスト。
     * @throws Exception
     */
    @Test
    void testDelete_withoutRequestBody() throws Exception {
        String targetDataModel = "testModel";
        String token = "token";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData input = new ConnectorDeleteRequestSendData(targetDataModel, null, uuid, "query", token, headers);

        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorDeleteSender.send(any(ConnectorDeleteRequestSendData.class))).thenReturn(ResponseEntity.noContent().header("X-Tracking", uuid.toString()).build());

        mockMvc.perform(delete("/{targetDataModel}?query", targetDataModel)
                .header("X-Tracking", uuid.toString())
                .header("Authorization", token))
                .andExpect(status().isNoContent());

        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorDeleteSender, times(1)).send(any(ConnectorDeleteRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
        assertEquals(input.dataModelTemplate(), null);
        assertEquals(input.tracking(), uuid);
    }

    /**
     * {@link TargetDataModelApiController#delete(String, UUID)} リクエストボディありのテスト。
     * @throws Exception
     */
    @Test
    void testDelete_withRequestBody() throws Exception {
        String targetDataModel = "testModel";
        String token = "token";
        Object dataModelTemplate = "{\\\"key\\\":\\\"value\\\"}";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        ConnectorDeleteRequestSendData input = new ConnectorDeleteRequestSendData(targetDataModel, dataModelTemplate, uuid, "query", token, headers);

        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorDeleteSender.send(any(ConnectorDeleteRequestSendData.class))).thenReturn(ResponseEntity.noContent().header("X-Tracking", uuid.toString()).build());

        mockMvc.perform(delete("/{targetDataModel}?query", targetDataModel)
                .header("X-Tracking", uuid.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataModelTemplate)))
                .andExpect(status().isNoContent());

        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorDeleteSender, times(1)).send(any(ConnectorDeleteRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
        assertEquals(input.dataModelTemplate(), dataModelTemplate);
        assertEquals(input.tracking(), uuid);
    }

    /**
     * {@link TargetDataModelApiController#get(String, UUID)} のテスト。linkヘッダがある場合。
     * @throws Exception
     */
    @Test
    void testGet_withLink() throws Exception {
        String expected = "expected";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());

        // mock
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorGetSender.send(Mockito.any(ConnectorGetRequestSendData.class)))
                .thenReturn(new ConnectorGetRequestReceivedData<Object>(expected, uuid, "query", HttpStatus.OK, headers));

        // test
        mockMvc.perform(get("/{targetDataModel}", "test")
                .header("X-Tracking", uuid.toString())
                .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Tracking", uuid.toString()))
                .andExpect(header().string("Link", "http://test.com"))
                .andExpect(jsonPath("$").value(is((expected))));
        
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorGetSender, times(1)).send(any(ConnectorGetRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
    }

    /**
     * {@link TargetDataModelApiController#get(String, UUID)} のテスト。linkヘッダがない場合。
     * @throws Exception
     */
    @Test
    void testGet_withoutLink() throws Exception {
        String expected = "expected";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());

        // mock
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorGetSender.send(Mockito.any(ConnectorGetRequestSendData.class)))
                .thenReturn(new ConnectorGetRequestReceivedData<Object>(expected, uuid, "query", HttpStatus.OK, headers));

        // test
        mockMvc.perform(get("/{targetDataModel}", "test")
                .header("X-Tracking", uuid.toString())
                .header("Authorization", "token"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Tracking", uuid.toString()))
                .andExpect(header().doesNotExist("Link"))
                .andExpect(jsonPath("$").value(is((expected))));
        
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorGetSender, times(1)).send(any(ConnectorGetRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
    }

    /**
     * {@link TargetDataModelApiController#put(String, Object, UUID)} のテスト。
     * @throws Exception
     */
    @Test
    public void testPut() throws Exception {
        // Arrange
        String targetDataModel = "testModel";
        String token = "token";
        Object dataModelTemplate = "{}";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Tracking", uuid.toString());
        headers.add("Link", "http://test.com");
        ConnectorPutRequestReceivedData<Object> output = new ConnectorPutRequestReceivedData<Object>("responseTemplate", dataModelTemplate, uuid, "query", HttpStatus.OK, headers);

        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorPutSender.send(any(ConnectorPutRequestSendData.class))).thenReturn(output);

        // Act & Assert
        mockMvc.perform(put("/{targetDataModel}?query", targetDataModel)
                .header("X-Tracking", uuid.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataModelTemplate)))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))
                .andExpect(header().string("X-Tracking", uuid.toString()))
                .andExpect(header().string("Link", "http://test.com"));

        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorPutSender, times(1)).send(any(ConnectorPutRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
        assertEquals(output.dataModelTemplate(), dataModelTemplate);
        assertEquals(output.tracking(), uuid);
        assertEquals(output.headers(), headers);
        assertEquals(output.getStatusCode(), HttpStatus.OK);
    }

    /**
     * {@link TargetDataModelApiController#post(String, Object, UUID)} のテスト。
     * @throws Exception
     */
    @Test
    public void testPost() throws Exception {
        // Arrange
        String targetDataModel = "testModel";
        String token = "token";
        Object dataModelTemplate = "{}";
        UUID uuid = UUID.randomUUID();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Link", "http://test.com");
        headers.add("X-Tracking", uuid.toString());
        ConnectorPostRequestReceivedData<Object> output = new ConnectorPostRequestReceivedData<Object>("responseTemplate", dataModelTemplate, uuid, "query", HttpStatus.OK, headers);

        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        when(connectorPostSender.send(any(ConnectorPostRequestSendData.class))).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/{targetDataModel}?query", targetDataModel)
                .header("X-Tracking", uuid.toString())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dataModelTemplate)))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"))
                .andExpect(header().string("X-Tracking", uuid.toString()))
                .andExpect(header().string("Link", "http://test.com"));;

        verify(trackingHeaderBean, times(1)).getUUID();
        verify(connectorPostSender, times(1)).send(any(ConnectorPostRequestSendData.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
        assertEquals(output.dataModelTemplate(), dataModelTemplate);
        assertEquals(output.tracking(), uuid);
        assertEquals(output.headers(), headers);
        assertEquals(output.getStatusCode(), HttpStatus.OK);
    }
}