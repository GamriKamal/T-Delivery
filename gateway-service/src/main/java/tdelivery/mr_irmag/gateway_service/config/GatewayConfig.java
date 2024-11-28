package tdelivery.mr_irmag.gateway_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    private AuthenticationFilter filter;

    @Autowired
    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("menu-service", r -> r.path("/menu/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://menu-service/"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order-service"))
                .route("order-service", r -> r.path("/ws/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order-service"))
                .route("order-service", r -> r.path("/progress/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order-service"))
                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .route("route-service", r -> r.path("/delivery/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://route-service"))
                .route("route-service", r -> r.path("/restaurants/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://route-service"))
                .route("courier-service", r -> r.path("/courier/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://courier-service"))
                .route("message-service", r -> r.path("/message/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://message-service"))
                .build();
    }

}
