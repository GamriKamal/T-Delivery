package tdelivery.mr_irmag.route_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculateOrderRequest;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.CalculationDeliveryResponse;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.CourierServiceRequest;
import tdelivery.mr_irmag.route_service.domain.dto.courierCalculation.OrderForRouteDto;
import tdelivery.mr_irmag.route_service.service.DistanceService;

import java.util.UUID;

@RestController
@RequestMapping("/delivery")
@Log4j2
public class OrderRouterController {

    private final DistanceService distanceService;

    @Autowired
    public OrderRouterController(DistanceService distanceService) {
        this.distanceService = distanceService;
    }

    @Operation(
            summary = "Расчет стоимости и времени доставки для заказа",
            description = "Вычисляет стоимость доставки и предполагаемое время для предоставленных данных заказа.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Тело запроса с деталями заказа для расчета доставки",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CalculateOrderRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Доставка для заказа успешно рассчитана",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CalculationDeliveryResponse.class)
                            )
                    )
            }
    )
    @PostMapping("/calculate")
    public ResponseEntity<CalculationDeliveryResponse> calculateDeliveryOrder(
            @RequestBody @Parameter(description = "Детали заказа для расчета доставки") CalculateOrderRequest calculateOrderRequest) {

        log.info("Calculating delivery order {}", calculateOrderRequest.toString());
        return new ResponseEntity<>(distanceService.calculateDelivery(calculateOrderRequest), HttpStatus.OK);
    }

    @Operation(
            summary = "Поиск ближайшего заказа для курьера",
            description = "Находит ближайший заказ по текущим координатам курьера.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Тело запроса с деталями запроса службы курьерской доставки",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CourierServiceRequest.class)
                    )
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Ближайший заказ успешно найден",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderForRouteDto.class)
                            )
                    )
            }
    )
    @PostMapping("/closestOrder")
    public ResponseEntity<OrderForRouteDto> findClosestOrder(
            @RequestBody @Parameter(description = "Запрос для нахождения ближайшего заказа для курьера") CourierServiceRequest request) {

        return new ResponseEntity<>(distanceService.findClosestCoordinates(request), HttpStatus.OK);
    }
}
