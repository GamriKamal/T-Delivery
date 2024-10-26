package tdelivry.mr_irmag.user_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tdelivry.mr_irmag.user_service.domain.dto.OrderDTO;
import tdelivry.mr_irmag.user_service.domain.dto.UserOrderDTO;
import tdelivry.mr_irmag.user_service.domain.dto.UserOrderResponse;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceClient {
    @Value("${tdelivery.order-service.url}")
    private String orderServiceUrl;
    private final RestTemplate restTemplate;

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
                new ParameterizedTypeReference<List<UserOrderResponse>>() {}
        );

        log.info(responseEntity.getBody().toString());
        return responseEntity.getBody();

    }



}
