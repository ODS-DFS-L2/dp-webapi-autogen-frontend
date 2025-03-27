package net.ouranos.connector.digiline;



import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Objects;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import lombok.extern.slf4j.Slf4j;
import net.ouranos.connector.digiline.components.TokenIntrospection;
import net.ouranos.connector.digiline.components.TrackingHeaderBean;
import net.ouranos.connector.digiline.delete.ConnectorDeleteRequestSendData;
import net.ouranos.connector.digiline.delete.ConnectorDeleteSender;
import net.ouranos.connector.digiline.get.ConnectorGetRequestSendData;
import net.ouranos.connector.digiline.get.ConnectorGetSender;
import net.ouranos.connector.digiline.post.ConnectorPostRequestSendData;
import net.ouranos.connector.digiline.post.ConnectorPostSender;
import net.ouranos.connector.digiline.put.ConnectorPutRequestSendData;
import net.ouranos.connector.digiline.put.ConnectorPutSender;
import jakarta.annotation.Generated;
import jakarta.servlet.http.HttpServletRequest;


@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-07-31T14:08:30.360621200+09:00[Asia/Tokyo]", comments = "Generator version: 7.6.0")
@Validated
@Tag(name = "データ流通システム", description = "the データ流通システム API")
@Slf4j
@RestController
@RequestMapping("/")
public class TargetDataModelApiController implements TargetDataModelApi {

    private final ConnectorDeleteSender connectorDeleteSender;
    private final ConnectorGetSender connectorGetSender;
    private final ConnectorPutSender connectorPutSender;
    private final ConnectorPostSender connectorPostSender;
    private final TrackingHeaderBean trackingHeaderBean;
    private final TokenIntrospection tokenIntrospection;

    public TargetDataModelApiController(ConnectorDeleteSender connectorDeleteSender,
                                        ConnectorGetSender connectorGetSender,
                                        ConnectorPutSender connectorPutSender,
                                        ConnectorPostSender connectorPostSender,
                                        TrackingHeaderBean trackingHeaderBean,
                                        TokenIntrospection tokenIntrospection) {
        this.connectorDeleteSender = connectorDeleteSender;
        this.connectorGetSender = connectorGetSender;
        this.connectorPutSender = connectorPutSender;
        this.connectorPostSender = connectorPostSender;
        this.trackingHeaderBean = trackingHeaderBean;
        this.tokenIntrospection = tokenIntrospection;
    }

    @Override
    public ResponseEntity<Void> targetDataModelDelete(String targetDataModel, Object dataModelTemplate, UUID xTracking, HttpHeaders headers) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String query = request.getQueryString();
        String token = request.getHeader("Authorization");
        tokenIntrospection.verifyToken(token);
        UUID trackingUuid = this.trackingHeaderBean.getUUID();
        log.info("Starts a DELETE process. dataModel = {}, Tracking = {}", targetDataModel, trackingUuid);
        var input = new ConnectorDeleteRequestSendData(targetDataModel, dataModelTemplate, trackingUuid, query, token, headers);
        var output = this.connectorDeleteSender.send(input);
        log.info("DELETE completed successfully. dataModel = {}, Tracking = {}", targetDataModel, trackingUuid);
        return ResponseEntity.status(output.getStatusCode()).header("X-Tracking", trackingUuid.toString()).build();
    }

    @Override
    public ResponseEntity<Object> targetDataModelGet(String targetDataModel, UUID xTracking, HttpHeaders headers) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String query = request.getQueryString();
        String token = request.getHeader("Authorization");
        tokenIntrospection.verifyToken(token);
        UUID trackingUuid = this.trackingHeaderBean.getUUID();
        log.info("Starts a GET process. dataModel = {}, Tracking = {}", targetDataModel, trackingUuid);
        var input = new ConnectorGetRequestSendData(targetDataModel, trackingUuid, query, token, headers);
        var output = this.connectorGetSender.send(input);
        log.info("GET completed successfully. dataModel = {}, Tracking = {}", targetDataModel, trackingUuid);
        return ResponseEntity.status(output.getStatusCode())
            .headers(output.headers())
            .body(output.dataModelTemplate());
    }

    @Override
    public ResponseEntity<Object> targetDataModelPut(String targetDataModel, Object dataModelTemplate, UUID xTracking, HttpHeaders headers) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String query = request.getQueryString();
        String token = request.getHeader("Authorization");
        tokenIntrospection.verifyToken(token);
        UUID trackingUuid = this.trackingHeaderBean.getUUID();
        log.info("Starts a PUT process. dataModel = {}, tracking = {}", targetDataModel, trackingUuid);
        var input = new ConnectorPutRequestSendData(targetDataModel, dataModelTemplate, trackingUuid, query, token, headers);
        var output = this.connectorPutSender.send(input);
        log.info("PUT completed successfully. dataModel = {}, tracking = {}", targetDataModel, trackingUuid);
        return ResponseEntity.status(output.getStatusCode())
            .headers(output.headers())
            .body(output.dataModelTemplate());
    }

    @Override
    public ResponseEntity<Object> targetDataModelPost(String targetDataModel, Object dataModelTemplate, UUID xTracking, HttpHeaders headers) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String query = request.getQueryString();
        String token = request.getHeader("Authorization");
        tokenIntrospection.verifyToken(token);
        UUID trackingUuid = this.trackingHeaderBean.getUUID();
        log.info("Starts a POST process. dataModel = {}, tracking = {}", targetDataModel, trackingUuid);
        var input = new ConnectorPostRequestSendData(targetDataModel, dataModelTemplate, trackingUuid, query, token, headers);
        var output = this.connectorPostSender.send(input);
        log.info("POST completed successfully. dataModel = {}, tracking = {}", targetDataModel, trackingUuid);
        return ResponseEntity.status(output.getStatusCode())
            .headers(output.headers())
            .body(output.dataModelTemplate());
    }


}
