package tdelivery.mr_irmag.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tdelivery.mr_irmag.order_service.domain.dto.OrderRequest;
import tdelivery.mr_irmag.order_service.service.OrderService;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> makeOrder(@RequestBody OrderRequest orderRequest){
        log.info("Making a new order {}", orderRequest);
        var result = orderService.processOrder(orderRequest);
        return ResponseEntity.ok(result.toString());
    }
}
