package tdelivry.mr_irmag.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tdelivry.mr_irmag.user_service.domain.dto.UserOrderResponse;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceClient {
    private final RestTemplate restTemplate;
    @Value("${tdelivery.order-service.url}")
    private String orderServiceUrl;

    public List<UserOrderResponse> getOrderOfUser(UUID userId, int page, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("id", userId.toString());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(orderServiceUrl)
                .queryParam("page", page)
                .queryParam("size", size);

        String url = uriBuilder.toUriString();
        log.info(url);
        ResponseEntity<List<UserOrderResponse>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<List<UserOrderResponse>>() {
                }
        );

        log.info(responseEntity.getBody().toString());
        return responseEntity.getBody();

    }


}
