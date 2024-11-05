package tdelivery.mr_irmag.order_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderRequestDto;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
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
    public ResponseEntity<CalculationDeliveryResponse> calculateOrder(@RequestBody CalculateOrderRequest calculateOrderRequest) {
        CalculationDeliveryResponse result = orderService.calculateOrder(calculateOrderRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/makeOrder")
    public void makeOrder(@RequestHeader("id") UUID id, @RequestBody CalculateOrderRequest calculateOrderRequest){
        log.info(calculateOrderRequest.toString());
        orderService.processOrder(id, calculateOrderRequest);
    }

    @PostMapping("/nearestOrder")
    public List<NearestOrderResponseDto> getNearestOrder(@Valid @RequestBody NearestOrderRequestDto request) {
        log.info(request.getPoint().toString());
        return orderService.getNearestOrders(request.getRadius(), request.getPoint());
    }

    @PostMapping("/changeStatus")
    public ResponseEntity<String> changeStatus(@RequestHeader("id") UUID id, @RequestBody String orderStatus) {
        orderService.changeStatusOfOrder(id, OrderStatus.valueOf(orderStatus));
        return ResponseEntity.ok("");
    }


    @GetMapping("/getUserOrder")
    public ResponseEntity<List<UserOrderRequestDTO>> getUserOrder(
            @RequestHeader("id") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UserOrderRequestDTO> orderPage = orderService.getOrdersOfUser(id, page, size);

        log.info(orderPage.toString());
        return ResponseEntity.ok(orderPage);
    }

    @GetMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("id") UUID id) {
        orderService.changeStatusOfOrder(id, OrderStatus.CANCELED);
        return ResponseEntity.ok().build();
    }

}
