package net.ouranos.connector.digiline.common.exception;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import jakarta.servlet.http.HttpServletRequest;
import net.ouranos.connector.digiline.components.TrackingHeaderBean;


public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private TrackingHeaderBean trackingHeaderBean;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    /**
     * handleInternalServerErrorExceptionのテストメソッド
     */
    @Test
    public void testHandleInternalServerErrorException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        ResourceAccessException ex = new ResourceAccessException("Service Unavailable");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleInternalServerErrorException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        assertEquals("[dataspace] InternalServerError", response.getBody().code());
        assertEquals("Unexpected error occurred", response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleBadRequestMissingRequestValueExceptionのテストメソッド
     * TrackingIdがnullではない場合
     */
    @Test
    public void testHandleBadRequestMissingRequestValueException_trackingNotNull() {
        // Arrange
        String uuid = "X-Tracking";
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(uuid);
        MissingRequestValueException ex = new MissingRequestValueException("id");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestMissingRequestValueException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("[dataspace] BadRequest", response.getBody().code());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleBadRequestMissingRequestValueExceptionのテストメソッド
     * TrackingIdがnullの場合
     */
    @Test
    public void testHandleBadRequestMissingRequestValueException_trackingNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(null);
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        MissingRequestValueException ex = new MissingRequestValueException("id");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestMissingRequestValueException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("[dataspace] BadRequest", response.getBody().code());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleBadRequestTypeMismatchExceptionのテストメソッド
     * TrackingIdがnullではない場合
     */
    @Test
    public void testHandleBadRequestTypeMismatchException_TrackingNotNull() {
        // Arrange
        String uuid = "X-Tracking";
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(uuid);
        TypeMismatchException ex = new TypeMismatchException("id", String.class);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestTypeMismatchException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("[dataspace] BadRequest", response.getBody().code());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
       assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleBadRequestTypeMismatchExceptionのテストメソッド
     * TrackingIdがnullの場合
     */
    @Test
    public void testHandleBadRequestTypeMismatchException_TrackingNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(null);
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        TypeMismatchException ex = new TypeMismatchException("id", String.class);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequestTypeMismatchException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("[dataspace] BadRequest", response.getBody().code());
        assertEquals(ex.getMessage(), response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleNotFoundExceptionのテストメソッド
     * TrackingIdがnullではない場合
     */
    @Test
    public void testHandleNotFoundException_TrackingNotNull() {
        // Arrange
        String uuid = "X-Tracking";
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(uuid);
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "test");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertEquals("[dataspace] NotFound", response.getBody().code());
        assertEquals("Endpoint Not Found", response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleNotFoundExceptionのテストメソッド
     * TrackingIdがnullの場合
     */
    @Test
    public void testHandleNotFoundException_TrackingNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(null);
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.GET, "test");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNotFoundException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertEquals("[dataspace] NotFound", response.getBody().code());
        assertEquals("Endpoint Not Found", response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleServiceUnavailableExceptionのテストメソッド
     */
    @Test
    public void testHandleServiceUnavailableException() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatusCode.valueOf(503), "[dataspace] InternalServerError", "{\"code\":\"[dataspace] InternalServerError\",\"message\":\"InternalServerError\",\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}".getBytes(), null);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleServiceUnavailableException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(503), response.getStatusCode());
        String responsestr = response.getBody().toString();
        assertEquals("{\"code\":\"[dataspace] InternalServerError\"", responsestr.split(",")[0]);
        assertEquals("\"message\":\"InternalServerError\"", responsestr.split(",")[1]);
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertEquals("\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}", responsestr.split(",")[2]);
    }


    /**
     * handleClientErrorExceptionのテストメソッド
     */
    @Test
    public void testHandleClientErrorExceptionelse() throws JsonMappingException, JsonProcessingException {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatusCode.valueOf(400), "[dataspace] BadRequest", "{\"code\":\"[dataspace] BadRequest\",\"message\":\"Bad Request\",\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}".getBytes(), null);


        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleClientErrorException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        String responsestr = response.getBody().toString();
        assertEquals("{\"code\":\"[dataspace] BadRequest\"", responsestr.split(",")[0]);
        assertEquals("\"message\":\"Bad Request\"", responsestr.split(",")[1]);
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertEquals("\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}", responsestr.split(",")[2]);
    }

    /**
     * handleVerifyTokenIntrospectionFalseExceptionのテストメソッド
     * TrackingIdがnullではない場合
     */
    @Test
    public void testhandleVerifyTokenIntrospectionFalseException_TrackingNotNull() {
        // Arrange
        String uuid = "X-Tracking";
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(uuid);
        VerifyTokenIntrospectionFalseException ex = new VerifyTokenIntrospectionFalseException();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleVerifyTokenIntrospectionFalseException(ex);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
        assertEquals("[auth] Unauthorized", response.getBody().code());
        assertEquals("Invalid or expired token", response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }

    /**
     * handleVerifyTokenIntrospectionFalseExceptionのテストメソッド
     * TrackingIdがnullの場合
     */
    @Test
    public void testhandleVerifyTokenIntrospectionFalseException_TrackingNull() {
        // Arrange
        UUID uuid = UUID.randomUUID();
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(null);
        when(trackingHeaderBean.getUUID()).thenReturn(uuid);
        VerifyTokenIntrospectionFalseException ex = new VerifyTokenIntrospectionFalseException();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleVerifyTokenIntrospectionFalseException(ex);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
        assertEquals("[auth] Unauthorized", response.getBody().code());
        assertEquals("Invalid or expired token", response.getBody().message());
        assertEquals(uuid.toString(), response.getHeaders().get("X-Tracking").get(0));
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }
}