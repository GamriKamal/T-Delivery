package tdelivery.mr_irmag.route_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderServiceClient {
    private final RestTemplate restTemplate;
    private final DistanceService distanceService;
    private final RestaurantService restaurantService;

    @Autowired
    public OrderServiceClient(RestTemplate restTemplate, DistanceService distanceService, RestaurantService restaurantService) {
        this.restTemplate = restTemplate;
        this.distanceService = distanceService;
        this.restaurantService = restaurantService;
    }



}
