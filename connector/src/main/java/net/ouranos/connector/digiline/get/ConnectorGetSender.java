package net.ouranos.connector.digiline.get;

public interface ConnectorGetSender {
    ConnectorGetRequestReceivedData<Object> send(ConnectorGetRequestSendData data);
}
