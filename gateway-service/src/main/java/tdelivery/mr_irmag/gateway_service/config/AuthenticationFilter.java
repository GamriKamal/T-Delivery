package tdelivery.mr_irmag.gateway_service.config;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import tdelivery.mr_irmag.gateway_service.dto.JwtAuthenticationResponse;
import tdelivery.mr_irmag.gateway_service.service.JwtUtil;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {

    private final RouterValidator routerValidator;
    private final JwtUtil jwtUtil;
    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthenticationFilter(RouterValidator routerValidator, JwtUtil jwtUtil, WebClient.Builder webClientBuilder) {
        this.routerValidator = routerValidator;
        this.jwtUtil = jwtUtil;
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (routerValidator.isSecured.test(request)) {
            if (this.isAuthMissing(request)) {
                return this.redirectToAuthService(exchange, chain);
            }

            final String token = this.getAuthHeader(request);

            try {
                if (jwtUtil.isInvalid(token)) {
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    return exchange.getResponse().setComplete();
                }
            } catch (ExpiredJwtException e) {
                System.err.println("JWT token expired: " + e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            String id = jwtUtil.extractID(token);
            String role = jwtUtil.extractRole(token);
            System.out.println(role + " role");

            // Прочие проверки ролей
            if (request.getURI().getPath().matches("/users(/.*)?") && !role.equals("ADMIN")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if (request.getURI().getPath().matches("/menu(/.*)?") && !role.equals("ADMIN")) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if(request.getURI().getPath().matches("/delivery(/.*)?") && !role.equals("ADMIN")){
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if(request.getURI().getPath().matches("/restaurants(/.*)?") && !role.equals("ADMIN")){
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            if(request.getURI().getPath().matches("/courier(/.*)?") && !role.equals("COURIER")){
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            this.updateRequest(exchange, token, id);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> redirectToAuthService(ServerWebExchange exchange, GatewayFilterChain chain) {
        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/auth/signIn")
                .body(BodyInserters.fromFormData("username", "user").with("password", "password"))
                .retrieve()
                .bodyToMono(JwtAuthenticationResponse.class)
                .flatMap(response -> {
                    String token = response.getToken();
                    String id = jwtUtil.extractID(token);

                    this.updateRequest(exchange, token, id);

                    return chain.filter(exchange);
                });
    }

    private String getAuthHeader(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getOrEmpty("Authorization").get(0);

        if (bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private void updateRequest(ServerWebExchange exchange, String token, String id) {
        exchange.getRequest().mutate()
                .header("Authorization", "Bearer " + token)
                .header("id", id)
                .build();
    }
}


