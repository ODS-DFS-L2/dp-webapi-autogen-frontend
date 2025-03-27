package net.ouranos.connector;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataTransactionConnectorApplicationTest {

    @Test
    void applicationStarts() {
        // SpringApplication.runが例外を投げないことを確認するテスト
        assertDoesNotThrow(() -> SpringApplication.run(DataTransactionConnectorApplication.class, new String[] {}));
    }

}
