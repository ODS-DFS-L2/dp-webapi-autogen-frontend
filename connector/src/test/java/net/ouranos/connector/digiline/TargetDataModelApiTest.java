package net.ouranos.connector.digiline;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.Valid;

/**
 * Unit tests for the {@link TargetDataModelApi} class.
 */
public class TargetDataModelApiTest {

    private TargetDataModelApi targetDataModelApi = new TargetDataModelApi() {

        @Override
        public ResponseEntity<Void> targetDataModelDelete(String targetDataModel, Object dataModelTemplate, UUID xTracking, HttpHeaders headers) {
            throw new UnsupportedOperationException("Unimplemented method 'targetDataModelDelete'");
        }

        @Override
        public ResponseEntity<Object> targetDataModelGet(String targetDataModel, UUID xTracking, HttpHeaders headers) {
            throw new UnsupportedOperationException("Unimplemented method 'targetDataModelGet'");
        }

        @Override
        public ResponseEntity<Object> targetDataModelPut(String targetDataModel, @Valid Object dataModelTemplate,
                UUID xTracking, HttpHeaders headers) {
            throw new UnsupportedOperationException("Unimplemented method 'targetDataModelPut'");
        }

        @Override
        public ResponseEntity<Object> targetDataModelPost(String targetDataModel, @Valid Object dataModelTemplate,
                UUID xTracking, HttpHeaders headers) {
            throw new UnsupportedOperationException("Unimplemented method 'targetDataModelPost'");
        }
        
    };

    /**
        * test getRequest method.
    */
    @Test
    public void testGetRequest() {
        // Act
        Optional<NativeWebRequest> response = targetDataModelApi.getRequest();

        // Assert
        assertFalse(response.isPresent());
    }
}