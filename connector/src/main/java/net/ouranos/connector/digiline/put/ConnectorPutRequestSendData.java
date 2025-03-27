package net.ouranos.connector.digiline.put;

import java.util.UUID;

import org.springframework.http.HttpHeaders;

public record ConnectorPutRequestSendData<T>(String dataModelName, T dataModelTemplate, UUID tracking, String queryString, String token, HttpHeaders headers) {}
