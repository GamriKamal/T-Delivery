package tdelivery.mr_irmag.gateway_service.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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
//                .route("menu-service", r -> r.path("/menu/upload-csv-file")
//                        .filters(f -> f.filter((exchange, chain) ->
//                        {
//                            return checkUserRole(exchange, "ADMIN")
//                                    .flatMap(hasRole -> {
//                                        if (hasRole) {
//                                            return chain.filter(exchange);
//                                        }
//                                        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Access Denied"));
//                                    });
//                        }
//                        ))
//                        .uri("lb://menu-service/"))
                .route("menu-service", r -> r.path("/menu/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://menu-service/"))
                .route("auth-service", r -> r.path("/auth/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://auth-service"))
                .build();
    }

    private Mono<Boolean> checkUserRole(ServerWebExchange exchange, String requiredRole) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String role = jwtUtil.extractRole(token);
            return Mono.just(requiredRole.equals(role));
        }
        return Mono.just(false);
    }

}
