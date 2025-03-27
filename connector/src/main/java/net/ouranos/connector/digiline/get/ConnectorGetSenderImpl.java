package net.ouranos.connector.digiline.get;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class ConnectorGetSenderImpl implements ConnectorGetSender {

    @Value("${apiKey}")
    private String apiKey;

    @Value("${restclient.base-url}")
    private String baseUrl;

    @Autowired
    private RestClient.Builder restClient;

    private List<String> excludedHeaders;
    // "excluded-headers.txt" に記載のある文字列を含むヘッダーを除外する
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("excluded-headers.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                excludedHeaders = reader.lines().collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to load excluded headers", e);
            excludedHeaders = new ArrayList<>();
        }
    }

    @Override
    public ConnectorGetRequestReceivedData<Object> send(ConnectorGetRequestSendData data) {
        log.info("Start data transmission. datamodel = {}, Tracking = {}", data.dataModelName(), data.tracking());

        // "x-" を含むリクエストヘッダーを抽出
        MultiValueMap<String, String> xheaders = new LinkedMultiValueMap<>();
        data.headers().forEach((key, values) -> {
            if (key.startsWith("x-")) {
                    xheaders.put(key, values);
                }
            });
        // "x-"を含むリクエストヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            xheaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        // "x-tracking" リクエストヘッダーの処理
        if (!xheaders.containsKey("x-tracking")) {
            xheaders.add("x-tracking", data.tracking().toString());
        }

        ResponseEntity<Object> result;
        if (data.queryString() == null) {
            String url = baseUrl + "/" + data.dataModelName();
            result = this.restClient.build().get()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", data.token());
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toEntity(Object.class);
            } else if(data.queryString().contains("[") && data.queryString().contains("]")) {
                String url = baseUrl + "/" + data.dataModelName();
                URI uri = UriComponentsBuilder.fromUriString(url)
                .query(data.queryString())
                .build().toUri();
                result = this.restClient.build().get()
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .retrieve()
                .toEntity(Object.class);
            } else {
                String url = baseUrl + "/" + data.dataModelName();
                URI uri = UriComponentsBuilder.fromUriString(url)
                .query(data.queryString())
                .build(true).toUri();
                result = this.restClient.build().get()
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .retrieve()
                .toEntity(Object.class);
            }

        log.info("Data transmission completed successfully.  datamodel = {}, Tracking = {}", data.dataModelName(), data.tracking());

        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        
        return new ConnectorGetRequestReceivedData<>(result.getBody(), data.tracking(), data.queryString(), result.getStatusCode(), filteredHeaders);
    }
}
