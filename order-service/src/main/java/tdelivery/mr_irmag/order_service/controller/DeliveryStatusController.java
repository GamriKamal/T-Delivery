package tdelivery.mr_irmag.order_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import tdelivery.mr_irmag.order_service.domain.entity.Order;
import tdelivery.mr_irmag.order_service.domain.entity.OrderStatus;
import tdelivery.mr_irmag.order_service.service.OrderService;
import tdelivery.mr_irmag.order_service.service.WebSocketDeliveryStatusService;

@Controller
public class DeliveryStatusController {
    @GetMapping("/progress")
    public String progress() {
        return "redirect:/index.html";
    }
}