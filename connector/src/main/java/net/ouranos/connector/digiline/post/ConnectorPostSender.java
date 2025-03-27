package net.ouranos.connector.digiline.post;

public interface ConnectorPostSender {
    ConnectorPostRequestReceivedData send(ConnectorPostRequestSendData data);
}
