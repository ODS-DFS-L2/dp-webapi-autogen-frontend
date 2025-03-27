package net.ouranos.connector.configration;

import java.util.concurrent.TimeUnit;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientAutoConfiguration {

        @Value("${rest.client.connection.ttl}")
        private int connectionTtl;

        @Value("${rest.client.connection.timeout}")
        private int connectionTimeout;

        @Value("${rest.client.socket.timeout}")
        private int socketTimeout;

        @Value("${rest.client.max.conn.total}")
        private int maxConnTotal;

        @Value("${rest.client.max.conn.per.route}")
        private int maxConnPerRoute;

        @Bean(name = "customRestClient")
        RestClient.Builder customRestClient() {
                var connectionConfig = ConnectionConfig.custom()
                // TTLを設定
                .setTimeToLive(TimeValue.of(connectionTtl, TimeUnit.SECONDS))
                // コネクションタイムアウト値を設定
                .setConnectTimeout(Timeout.of(connectionTimeout, TimeUnit.SECONDS))
                // ソケットタイムアウト値を設定（レスポンスタイムアウトと同義）
                .setSocketTimeout(Timeout.of(socketTimeout, TimeUnit.SECONDS))
                .build();

                // PoolingHttpClientConnectionManagerを使うことでコネクションがプールされて
                // リクエストごとにコネクションを確立する必要がなくなる
                var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(connectionConfig)
                // 全ルート合算の最大接続数
                .setMaxConnTotal(maxConnTotal)
                // ルート（基本的にはドメイン）ごとの最大接続数
                .setMaxConnPerRoute(maxConnTotal)
                .build();
                var httpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .build();
                var requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
                return RestClient.builder()
                .requestFactory(requestFactory)
                .defaultStatusHandler(new DefaultResponseErrorHandler());
        }

        @Bean
        RestTemplate customRestTemplate() {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
                return restTemplate;
        }
}
