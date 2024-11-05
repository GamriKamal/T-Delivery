package tdelivery.mr_irmag.order_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tdelivery.mr_irmag.order_service.exception.UserServiceClientException;

import java.net.URI;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceClient {

    @Value("${tdelivery.userService.url}")
    private String userServiceUrl;

    private final RestTemplate restTemplate;

    public String getUserByID(UUID id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("id", id.toString());
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            log.info("UserServiceClient.getUserByID: Received response for user ID {}: {}", id, responseEntity.getBody());
            return responseEntity.getBody();

        } catch (Exception e) {
            log.error("Error fetching user by ID {}: {}", id, e.getMessage(), e);
            throw new UserServiceClientException("Failed to retrieve user details", id.toString(), e);
        }
    }
}
