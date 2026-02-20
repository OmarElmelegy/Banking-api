package com.bank.api.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bank.api.service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService; // You need to make sure this Service exists!

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip JWT filter for auth endpoints
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Check if token exists
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No valid Authorization header found for: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract Token
        jwt = authHeader.substring(7); // Remove "Bearer "
        System.out.println("🔑 JWT Token extracted for path: " + path);

        try {
            username = jwtUtil.extractUsername(jwt);
            System.out.println("👤 Username extracted: " + username);

            // 3. Validate and Authenticate
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // Create the Authentication Object
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Log the user in
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("✅ Authentication successful for: " + username);
                } else {
                    System.out.println("❌ Token validation failed for: " + username);
                }
            } else if (username == null) {
                System.out.println("❌ Username is null from token");
            }
        } catch (Exception e) {
            System.out.println("❌ Error processing JWT: " + e.getMessage());
            e.printStackTrace();
        }

        // 4. Pass to next filter
        filterChain.doFilter(request, response);
    }
}
