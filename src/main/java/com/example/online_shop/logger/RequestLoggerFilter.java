package com.example.online_shop.logger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RequestLoggerFilter extends OncePerRequestFilter {

    private final Level level;
    private final boolean enabled;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (enabled && !request.getRequestURI().contains("image"))
            log.atLevel(level).log("Получен {} запрос {}", request.getMethod(), request.getRequestURI());
        filterChain.doFilter(request, response);
    }

}
