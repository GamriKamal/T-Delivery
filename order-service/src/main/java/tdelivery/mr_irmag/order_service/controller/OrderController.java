package tdelivery.mr_irmag.order_service.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.order_service.domain.dto.DeliveryDTO;
import tdelivery.mr_irmag.order_service.domain.dto.OrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.UserOrderDTO;
import tdelivery.mr_irmag.order_service.service.OrderService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/calculate")
    public ResponseEntity<DeliveryDTO> calculateOrder(@RequestHeader("id") UUID id, @RequestBody OrderRequest orderRequest) {
        DeliveryDTO result = orderService.calculateOrder(id, orderRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/makeOrder")
    public void makeOrder(@RequestHeader("id") UUID id, @RequestBody OrderRequest orderRequest){
        orderService.processOrder(id, orderRequest);
    }

    @GetMapping("/getUserOrder")
    public ResponseEntity<List<UserOrderDTO>> getUserOrder(
            @RequestHeader("id") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UserOrderDTO> orderPage = orderService.getOrdersOfUser(id, page, size);

        log.info(orderPage.toString());
        return ResponseEntity.ok(orderPage);
    }



}
