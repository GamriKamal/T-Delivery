package tdelivery.mr_irmag.courier_service.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdelivery.mr_irmag.courier_service.domain.dto.GetOrderRequest;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.GetNearestOrderResponse;
import tdelivery.mr_irmag.courier_service.domain.dto.findNearestOrder.NearestOrderRequestDto;
import tdelivery.mr_irmag.courier_service.service.CourierService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/courier")
public class CourierController {
    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @PostMapping("/online")
    public List<GetNearestOrderResponse> getNearestOrder(@Valid @RequestBody NearestOrderRequestDto request) {
        log.info("Get nearest order: {}", request.getPoint().toString());
        return courierService.getNearestOrders(request);
    }

    @PostMapping("/takeOrder")
    public void approveOrder(@Valid @RequestBody GetOrderRequest request) {
        courierService.takeOrder(request);
    }

}
