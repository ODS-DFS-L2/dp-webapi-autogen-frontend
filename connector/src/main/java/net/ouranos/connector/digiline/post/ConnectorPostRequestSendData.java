package net.ouranos.connector.digiline.post;

import org.springframework.http.HttpHeaders;
import java.util.UUID;

public record ConnectorPostRequestSendData<T>(String dataModelName, T dataModelTemplate, UUID tracking, String queryString, String token, HttpHeaders headers) {}
