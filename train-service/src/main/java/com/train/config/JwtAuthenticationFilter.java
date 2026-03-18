package com.train.config;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.train.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,  HttpServletResponse response, FilterChain filterChain)	throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        log.debug("Authorization Header: {}", authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.debug("Extracted Token: {}", token.substring(0, Math.min(20, token.length())) + "...");
            
            try {
                String email = jwtUtil.extractEmail(token);
                String role = jwtUtil.extractRole(token);
                log.info("JWT Validated - Email: {}, Role: {}", email, role);
                
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                log.debug("Granted Authority: {}", authority.getAuthority());
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set successfully for user: {}", email);
            } catch (Exception e) {
                log.error("JWT validation failed: {}", e.getMessage(), e);
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }
        } else {
            log.warn("No valid Authorization header found");
        }
        filterChain.doFilter(request, response);
    }
}
