package tdelivery.mr_irmag.order_service.controller;

import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.service.OrderService;
import tdelivery.mr_irmag.order_service.service.WebSocketDeliveryStatusService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DeliveryStatusController {

    @Operation(
            summary = "Перенаправление на страницу прогресса",
            description = "Этот метод выполняет перенаправление на страницу index.html, чтобы показать прогресс выполнения."
    )
    @ApiResponse(
            responseCode = "302",
            description = "Успешное перенаправление на страницу index.html",
            content = @Content(mediaType = "text/html", schema = @Schema(implementation = String.class))
    )
    @GetMapping("/progress")
    public String progress() {
        return "redirect:/index.html";
    }
}
