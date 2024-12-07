package tdelivery.mr_irmag.order_service.controller;

import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.order_service.domain.dto.ProcessCourierOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.order_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderResponseDto;
import tdelivery.mr_irmag.order_service.domain.dto.courierServiceDTO.NearestOrderRequestDto;
import tdelivery.mr_irmag.order_service.domain.dto.userServiceDTO.UserOrderRequestDTO;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.service.OrderService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.content.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Рассчитать стоимость доставки",
            description = "Этот метод рассчитывает стоимость доставки для заказа на основе предоставленных данных."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Успешный расчет стоимости доставки",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CalculationDeliveryResponse.class))
    )
    @PostMapping("/calculate")
    public ResponseEntity<CalculationDeliveryResponse> calculateOrder(@RequestBody @Valid CalculateOrderRequest calculateOrderRequest) {
        CalculationDeliveryResponse result = orderService.calculateOrder(calculateOrderRequest);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Создать заказ",
            description = "Этот метод обрабатывает создание нового заказа на основе переданных данных."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Заказ успешно создан"
    )
    @PostMapping("/makeOrder")
    public void makeOrder(@RequestHeader("id") UUID id, @RequestBody CalculateOrderRequest calculateOrderRequest) {
        log.info(calculateOrderRequest.toString());
        orderService.processOrder(id, calculateOrderRequest);
    }

    @Operation(
            summary = "Получить ближайшие заказы",
            description = "Этот метод позволяет получить список ближайших заказов в зависимости от радиуса поиска."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список ближайших заказов успешно получен",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = NearestOrderResponseDto.class))
    )
    @PostMapping("/nearestOrder")
    public List<NearestOrderResponseDto> getNearestOrder(@Valid @RequestBody NearestOrderRequestDto request) {
        log.info(request.getPoint().toString());
        return orderService.getNearestOrders(request.getRadius(), request.getPoint());
    }

    @Operation(
            summary = "Изменить статус заказа",
            description = "Этот метод позволяет изменить статус заказа на основе переданных данных."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Статус заказа успешно изменен"
    )
    @PostMapping("/changeStatus")
    public ResponseEntity<String> changeStatus(@RequestHeader("id") UUID id, @RequestBody String orderStatus) {
        orderService.changeStatusOfOrder(id, orderStatus);
        return ResponseEntity.ok("");
    }

    @Operation(
            summary = "Изменить статус через WebSocket",
            description = "Этот метод позволяет изменить статус заказа, полученный через WebSocket-сообщение."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Статус заказа успешно обновлен через WebSocket"
    )
    @MessageMapping("/hello")
    @Generated("WebSocket")
    public ResponseEntity<String> changeStatusFromWebSocket(@Payload Map<String, Object> payload) {
        log.debug("Data from websocket {}", payload.toString());
        UUID orderId = UUID.fromString(payload.get("id").toString());
        String orderStatus = payload.get("status").toString();

        orderService.changeStatusOfOrder(orderId, orderStatus);
        orderService.sendEmail(orderId);
        return ResponseEntity.ok("Status updated successfully for order ID: " + orderId);
    }

    @Operation(
            summary = "Курьер берет заказ",
            description = "Этот метод позволяет курьеру взять заказ для выполнения."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Курьер успешно взял заказ"
    )
    @PostMapping("/courier/takeOrder")
    public ResponseEntity<String> courierTakeOrder(@RequestBody ProcessCourierOrderRequest request) {
        orderService.updateSocket(request);
        return ResponseEntity.ok("");
    }

    @Operation(
            summary = "Получить заказы пользователя",
            description = "Этот метод позволяет получить список заказов пользователя с пагинацией."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список заказов пользователя успешно получен",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserOrderRequestDTO.class))
    )
    @GetMapping("/getUserOrder")
    public ResponseEntity<List<UserOrderRequestDTO>> getUserOrder(
            @RequestHeader("id") UUID id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<UserOrderRequestDTO> orderPage = orderService.getOrdersOfUser(id, page, size);

        log.info(orderPage.toString());
        return ResponseEntity.ok(orderPage);
    }

    @Operation(
            summary = "Отменить заказ",
            description = "Этот метод позволяет отменить заказ на основе переданного идентификатора заказа."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Заказ успешно отменен"
    )
    @GetMapping("/cancel")
    public ResponseEntity<?> cancelOrder(@RequestHeader("order_id") UUID orderId) {
        orderService.changeStatusOfOrder(orderId, "CANCELED");
        orderService.sendEmail(orderId);
        return ResponseEntity.ok().build();
    }
}

