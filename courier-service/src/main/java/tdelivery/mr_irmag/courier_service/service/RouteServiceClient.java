package tdelivery.mr_irmag.courier_service.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.google.gson.Gson;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.RouteServiceResponse;

@Service
@Log4j2
public class RouteServiceClient {
    private final RestTemplate restTemplate;
    private final Gson gson;
    @Value("${tdelivery.route-service.url}")
    private String routeServiceUrl;

    @Autowired
    public RouteServiceClient(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public RouteServiceResponse findNearestOrder(RouteServiceRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<RouteServiceRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    routeServiceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            log.info(responseEntity.getBody());

            if (responseEntity.getBody() == null || responseEntity.getBody().isEmpty()) {
                throw new NullPointerException("Received empty response body");
            }

            return gson.fromJson(responseEntity.getBody(), RouteServiceResponse.class);
        } catch (HttpClientErrorException | NullPointerException e) {
            log.error(e.getLocalizedMessage());
            throw e;
        }

    }


}
