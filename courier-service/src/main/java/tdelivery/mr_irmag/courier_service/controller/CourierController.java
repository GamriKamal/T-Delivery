package tdelivery.mr_irmag.courier_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Курьер", description = "API для работы с курьером")
public class CourierController {
    private final CourierService courierService;

    @Autowired
    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @Operation(
            summary = "Получить ближайшие заказы",
            description = "Этот метод возвращает список ближайших заказов для курьера по его текущей локации.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список ближайших заказов")
            }
    )
    @PostMapping("/online")
    public List<GetNearestOrderResponse> getNearestOrder(@Valid @RequestBody NearestOrderRequestDto request) {
        log.info("Get nearest order: {}", request.getPoint().toString());
        return courierService.getNearestOrders(request);
    }

    @Operation(
            summary = "Принять заказ",
            description = "Этот метод позволяет курьеру подтвердить принятие заказа.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Заказ принят успешно"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос")
            }
    )
    @PostMapping("/takeOrder")
    public void approveOrder(@Valid @RequestBody GetOrderRequest request) {
        courierService.takeOrder(request);
    }
}

