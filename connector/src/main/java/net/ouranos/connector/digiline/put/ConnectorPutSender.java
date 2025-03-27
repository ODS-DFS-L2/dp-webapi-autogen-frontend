package net.ouranos.connector.digiline.put;

public interface ConnectorPutSender {
    ConnectorPutRequestReceivedData send(ConnectorPutRequestSendData data);
}
