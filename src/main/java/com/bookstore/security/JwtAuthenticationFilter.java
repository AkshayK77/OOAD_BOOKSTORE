package com.bookstore.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(@Lazy JwtService jwtService, @Lazy UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        String requestURI = request.getRequestURI();
        
        // Bypass strict JWT validation for certain endpoints
        if (requestURI.contains("/api/auth/") || 
            requestURI.contains("/api/books") && request.getMethod().equals("GET") ||
            requestURI.contains("/api/reviews") && request.getMethod().equals("GET") ||
            requestURI.contains("/error") ||
            requestURI.equals("/") ||
            requestURI.matches("/api/orders/\\d+/payment")) {  // Add bypass for payment update
            
            System.out.println("Bypassing strict JWT validation for endpoint: " + requestURI);
            
            // For orders endpoints, add a default authentication for testing
            if (requestURI.contains("/api/orders")) {
                System.out.println("Added default authentication for orders endpoint");
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                        "guest@example.com", 
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    )
                );
            }
            
            filterChain.doFilter(request, response);
            return;
        }
        
        // Normal JWT processing
        System.out.println("Processing JWT for endpoint: " + requestURI);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No Bearer token found in Authorization header");
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            System.out.println("Attempting to extract email from JWT token");
            userEmail = jwtService.extractUsername(jwt);
            
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Successfully authenticated user: " + userEmail);
                } else {
                    System.out.println("Token validation failed for user: " + userEmail);
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing JWT token: " + e.getMessage());
            // Continue with filter chain even if token processing fails
        }
        
        filterChain.doFilter(request, response);
    }
} 