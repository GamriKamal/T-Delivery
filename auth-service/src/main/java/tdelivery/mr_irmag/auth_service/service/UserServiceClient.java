package tdelivery.mr_irmag.auth_service.service;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.auth_service.domain.dto.SignInRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.SignUpRequest;
import tdelivery.mr_irmag.auth_service.domain.dto.UserDTO;
import tdelivery.mr_irmag.auth_service.domain.model.User;
import tdelivery.mr_irmag.auth_service.exceptions.*;


@Service
@Log4j2
@RequiredArgsConstructor
public class UserServiceClient {
    private final RestTemplate restTemplate;
    private final Gson gson;
    @Value("${tdelivery.user-service.url}")
    private String userServiceURL;

    public User getUserByUsername(SignInRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }

        log.info("Fetching user for username: {}", request.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<SignInRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    userServiceURL + "/check-username",
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info(responseEntity.getBody() + "privet");

            return User.of(gson.fromJson(responseEntity.getBody(), UserDTO.class));

        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User with username " + request.getUsername() + " not found. " + e.getLocalizedMessage());
        } catch (HttpClientErrorException.BadRequest e) {
            throw new InvalidRequestException("Invalid request for username " + request.getUsername() + ". " + e.getLocalizedMessage());
        } catch (ResourceAccessException e) {
            throw new ServiceUnavailableException("Unable to access user-service. Please try again later. " + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new UnexpectedErrorException("An unexpected error occurred while fetching user with username " + request.getUsername() + ". " + e.getLocalizedMessage());
        }
    }


    public User createUser(SignUpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<SignUpRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                userServiceURL,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return User.of(gson.fromJson(responseEntity.getBody(), UserDTO.class));
    }
}
