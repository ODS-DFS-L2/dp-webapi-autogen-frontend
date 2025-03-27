package net.ouranos.connector.digiline.post;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;

public record ConnectorPostRequestReceivedData<T>(String dataModelName, T dataModelTemplate, UUID tracking, String queryString, HttpStatusCode statusCode, HttpHeaders headers) {

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }}
