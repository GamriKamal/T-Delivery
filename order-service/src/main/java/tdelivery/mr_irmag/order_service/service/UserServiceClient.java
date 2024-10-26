package tdelivery.mr_irmag.order_service.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.order_service.domain.dto.UserDTO;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceClient {
    @Value("${tdelivery.userService.url}")
    private String userServiceUrl;
    private final RestTemplate restTemplate;

    public String getUserByID(UUID id) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("id", id.toString());

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                userServiceUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        log.info("Response: " + responseEntity.getBody());

        return responseEntity.getBody();
    }
}
