package net.ouranos.connector.digiline.delete;

import org.springframework.http.HttpHeaders;
import java.util.UUID;

public record ConnectorDeleteRequestSendData<T>(String dataModelName, T dataModelTemplate, UUID tracking, String queryString, String token, HttpHeaders headers) {}
