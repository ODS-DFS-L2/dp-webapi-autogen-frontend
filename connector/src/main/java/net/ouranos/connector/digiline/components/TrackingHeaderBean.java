package net.ouranos.connector.digiline.components;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * A bean that handles tracking headers for requests.
 * This bean is request-scoped, meaning a new instance is created for each HTTP request.
 */
@Component
@RequestScope
public class TrackingHeaderBean {

    private UUID uuid;

    /**
     * Constructs a new TrackingHeaderBean.
     * If the request contains a header named "xTracking", its value is used as the UUID.
     * Otherwise, a new random UUID is generated.
     */
    public TrackingHeaderBean() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
        .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String trackingId = request.getHeader("x-Tracking");
        if(trackingId != null) {
            this.uuid = UUID.fromString(trackingId);
        } else {
            this.uuid = UUID.randomUUID();
        }
    }
    
    /**
     * Returns the UUID associated with the current request.
     *
     * @return the UUID associated with the current request
     */
    public UUID getUUID() {
        return this.uuid;
    }
}