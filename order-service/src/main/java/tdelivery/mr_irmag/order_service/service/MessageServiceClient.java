package tdelivery.mr_irmag.order_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.order_service.domain.entity.Order;

@Service
@RequiredArgsConstructor
public class MessageServiceClient {
    private final RestTemplate restTemplate;


    public void sendEmail(String statusOfOrder, String email, Order order, int timeOfCooking) {
    }
}
