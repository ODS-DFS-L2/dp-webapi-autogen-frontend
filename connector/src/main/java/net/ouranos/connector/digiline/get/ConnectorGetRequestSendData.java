package net.ouranos.connector.digiline.get;

import org.springframework.http.HttpHeaders;
import java.util.UUID;

public record ConnectorGetRequestSendData(String dataModelName, UUID tracking, String queryString, String token, HttpHeaders headers) {}
