package tdelivery.mr_irmag.courier_service.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.courier_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.domain.entity.Order;
import tdelivery.mr_irmag.courier_service.domain.entity.OrderStatus;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class OrderServiceClient {
    private final RestTemplate restTemplate;
    private final Gson gson;
    @Value("${tdelivery.order-service.url}")
    private String orderServiceUrl;
    @Value("${tdelivery.order-service.changeStatusUrl}")
    private String changeStatusUrl;

    @Autowired
    public OrderServiceClient(RestTemplate restTemplate, Gson gson) {
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    public List<Order> getNearestOrders(NearestOrderRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<NearestOrderRequestDto> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                orderServiceUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity == null || responseEntity.getBody() == null) {
            log.warn("Received null response or empty body from order service");
            return List.of();
        }

        log.info(responseEntity.getBody());
        return gson.fromJson(responseEntity.getBody(), new TypeToken<List<Order>>() {
        }.getType());
    }

    public HttpStatusCode changeStatusOfOrder(ProcessCourierOrderRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("id", request.getOrderId().toString());

        HttpEntity<ProcessCourierOrderRequest> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                changeStatusUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity == null) {
            log.warn("Received null response when attempting to change order status");
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return responseEntity.getStatusCode();
    }
}
