package net.ouranos.connector.digiline.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import net.ouranos.connector.digiline.common.exception.VerifyTokenIntrospectionFalseException;
import net.ouranos.domain.digiline.model.AuthenticationTokenInputModel;
import net.ouranos.domain.digiline.model.AuthenticationTokenOutputModel;

@Component
@Slf4j
public class TokenIntrospection {

    @Value("${auth.url}")
    private String authUrl;

    @Value("${apiKey}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    public void verifyToken(String token) {
        log.info("Token verification is done.");
        AuthenticationTokenInputModel authenticationTokenInputModel = new AuthenticationTokenInputModel();
        if (token != null && token.length() > 7){
            authenticationTokenInputModel.idToken(token.substring(7));
        } else {
            authenticationTokenInputModel.idToken(token);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("apiKey", apiKey);
        HttpEntity<AuthenticationTokenInputModel> requestEntity = new HttpEntity<>(authenticationTokenInputModel, headers);

        ResponseEntity<AuthenticationTokenOutputModel> result = this.restTemplate.postForEntity(authUrl, requestEntity, AuthenticationTokenOutputModel.class);
        if (result.getBody().getActive() == false){
            log.error("Token verification failed.");
            throw new VerifyTokenIntrospectionFalseException();
        }
    }
}
