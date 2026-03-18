package com.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GlobalFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        log.debug("API Gateway - Incoming request: {} {}", exchange.getRequest().getMethod(), path);

        // Allow OPTIONS
        if (exchange.getRequest().getMethod().name().equals("OPTIONS")) {
            return chain.filter(exchange);
        }

        // Allow Swagger
        if (path.startsWith("/swagger") ||
            path.startsWith("/v3/api-docs") ||
            path.contains("api-docs") ||
            path.contains("swagger-ui") ||
            path.contains("webjars")) {
            return chain.filter(exchange);
        }

        // Allow Auth endpoints
        if (path.contains("/auth/register") ||
            path.contains("/auth/login")) {
            return chain.filter(exchange);
        }
        // Allow fare endpoints — GET only (POST /fare is admin-only, enforced downstream)
        if (path.contains("/fare") &&
            exchange.getRequest().getMethod().name().equals("GET")) {
            return chain.filter(exchange);
        }

        // Allow Search endpoints (stations + train search — no auth required)
        if (path.contains("/search") ||
            path.contains("/search-service")) {
            return chain.filter(exchange);
        }

        // Allow public train/coach read endpoints
        if (path.contains("/trains") ||
            path.contains("/coaches")) {
            return chain.filter(exchange);
        }

        // Allow journey read endpoints (used by search results page)
        if (path.contains("/journeys")) {
            return chain.filter(exchange);
        }

        // Require token for everything else
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("API Gateway - No valid Authorization header for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            log.error("API Gateway - Invalid JWT token for path: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        log.info("API Gateway - JWT validated successfully, forwarding to: {}", path);
        return chain.filter(exchange);
    }
}