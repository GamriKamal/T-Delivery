package tdelivery.mr_irmag.gateway_service.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

            if (jwtUtil.isInvalid(token)) {
                return this.redirectToAuthService(exchange, chain);
            }

            String email = jwtUtil.extractEmail(token);
            this.updateRequest(exchange, token, email);
        }

        return chain.filter(exchange);
    }

    private Mono<Void> redirectToAuthService(ServerWebExchange exchange, GatewayFilterChain chain) {
        return webClientBuilder.build()
                .post()
                .uri("http://auth-service/auth/signIn")
                .body(BodyInserters.fromFormData("username", "user").with("password", "password")) // Adjust form data as needed
                .retrieve()
                .bodyToMono(JwtAuthenticationResponse.class)
                .flatMap(response -> {
                    String token = response.getToken();
                    String email = jwtUtil.extractEmail(token);

                    this.updateRequest(exchange, token, email);

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

    private void updateRequest(ServerWebExchange exchange, String token, String email) {
        exchange.getRequest().mutate()
                .header("Authorization", "Bearer " + token)
                .header("email", email)
                .build();
    }
}


