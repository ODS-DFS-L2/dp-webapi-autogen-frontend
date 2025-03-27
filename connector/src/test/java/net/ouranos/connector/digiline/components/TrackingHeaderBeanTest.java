package net.ouranos.connector.digiline.components;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;


public class TrackingHeaderBeanTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletRequestAttributes servletRequestAttributes;

    private TrackingHeaderBean trackingHeaderBean;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    public void testGetUUID_WithNullTracking() {
        // Arrange
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(null);

        // Act
        trackingHeaderBean = new TrackingHeaderBean();

        // Assert
        assertNotNull(trackingHeaderBean.getUUID());
    }

    @Test
    public void testGetUUID_WithNonNullTracking() {
        // Arrange
        String trackingId = UUID.randomUUID().toString();
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("x-Tracking")).thenReturn(trackingId);

        // Act
        trackingHeaderBean = new TrackingHeaderBean();

        // Assert
        assertNotNull(trackingHeaderBean.getUUID());
        assertEquals(UUID.fromString(trackingId), trackingHeaderBean.getUUID());
    }
}