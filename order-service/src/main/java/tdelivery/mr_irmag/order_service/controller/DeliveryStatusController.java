package tdelivery.mr_irmag.order_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DeliveryStatusController {
    @GetMapping("/progress")
    public String progress() {
        return "redirect:/index.html";
    }
}