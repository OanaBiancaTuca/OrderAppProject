package com.codejava.orderapp.filters;

import com.codejava.orderapp.services.JWTService;
import com.codejava.orderapp.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    JWTService jwtService;
    UserService userService;

    @Autowired
    public JwtTokenFilter(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isEmpty(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        final String token = header.split(" ")[1].trim();
        String username = jwtService.validateToken(token);

        UserDetails userDetails = userService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken usernamePasswordToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), "",
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordToken);
        filterChain.doFilter(request, response);

    }
}
