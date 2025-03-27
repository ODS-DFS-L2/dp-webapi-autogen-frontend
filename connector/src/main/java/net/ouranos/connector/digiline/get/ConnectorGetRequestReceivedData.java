package net.ouranos.connector.digiline.get;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public record ConnectorGetRequestReceivedData<T>(T dataModelTemplate, UUID tracking, String queryString, HttpStatusCode statusCode, HttpHeaders headers) {
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }}
