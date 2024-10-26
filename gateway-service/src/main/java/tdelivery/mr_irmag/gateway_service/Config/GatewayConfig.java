package tdelivery.mr_irmag.gateway_service.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tdelivery.mr_irmag.gateway_service.Service.JwtUtil;

@Configuration
public class GatewayConfig {
    private AuthenticationFilter filter;

    private JwtUtil jwtUtil;

    @Autowired
    public GatewayConfig(AuthenticationFilter filter, JwtUtil jwtUtil) {
        this.filter = filter;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("menu-service", r -> r.path("/menu/upload-csv-file")
                        .filters(f -> f.filter(filter))
                        .uri("lb://menu-service/"))
                .route("menu-service", r -> r.path("/menu/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://menu-service/"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .route("order-service", r -> r.path("/order/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://order-service"))
                .route("user-service", r -> r.path("/user/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://user-service"))
                .build();
    }

}
