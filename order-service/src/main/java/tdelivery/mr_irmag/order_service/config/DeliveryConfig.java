package tdelivery.mr_irmag.order_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "delivery")
@Data
public class DeliveryConfig {
    private double basePrice;
    private double distancePrice;
    private double timePrice;
}
