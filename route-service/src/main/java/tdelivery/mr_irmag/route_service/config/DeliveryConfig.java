package tdelivery.mr_irmag.route_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tdelivery")
@Data
public class DeliveryConfig {
    private double basePrice;
    private double distancePrice;
    private double timePrice;
}
