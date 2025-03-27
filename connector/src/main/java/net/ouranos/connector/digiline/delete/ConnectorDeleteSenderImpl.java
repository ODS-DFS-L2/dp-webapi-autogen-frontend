package net.ouranos.connector.digiline.delete;

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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConnectorDeleteSenderImpl implements ConnectorDeleteSender {

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
    public ResponseEntity<Void> send(ConnectorDeleteRequestSendData data){
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

        ResponseEntity<Void> result;
        if (data.queryString() == null) {
            String url = baseUrl + "/" + data.dataModelName();
            if(data.dataModelTemplate() == null) {
                result = this.restClient.build().delete()
                .uri(url)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .retrieve()
                .toBodilessEntity();
            } else {
                result = this.restClient.build().method(HttpMethod.DELETE)
                .uri(url)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .body(data.dataModelTemplate())
                .retrieve()
                .toBodilessEntity();
            }
        } else {
            String url = baseUrl + "/" + data.dataModelName();
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(data.queryString())
            .build().toUri();
            if(data.dataModelTemplate() == null) {
                result = this.restClient.build().delete()
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .retrieve()
                .toBodilessEntity();
            } else {
                result = this.restClient.build().method(HttpMethod.DELETE)
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add("Content-Type", "application/json");
                    httpHeaders.add("Authorization", data.token());
                    httpHeaders.add("apiKey", apiKey);
                    xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
                })
                .body(data.dataModelTemplate())
                .retrieve()
                .toBodilessEntity();
            }
        }
        log.info("Data transmission completed successfully. datamodel = {}, Tracking = {}", data.dataModelName(), data.tracking());
        return result;
    }
}
