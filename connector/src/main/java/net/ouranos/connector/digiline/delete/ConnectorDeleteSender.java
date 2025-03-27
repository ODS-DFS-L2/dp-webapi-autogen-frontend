package net.ouranos.connector.digiline.delete;

import org.springframework.http.ResponseEntity;

public interface ConnectorDeleteSender {
    ResponseEntity<Void> send(ConnectorDeleteRequestSendData data);
}
