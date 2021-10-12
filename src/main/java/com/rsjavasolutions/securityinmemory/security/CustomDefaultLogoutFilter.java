package com.rsjavasolutions.securityinmemory.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomDefaultLogoutFilter extends OncePerRequestFilter {

    private RequestMatcher matcher = new AntPathRequestMatcher("/actuator/**", "GET");
    private RequestMatcher matcher2 = new AntPathRequestMatcher("/favicon.ico", "GET");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.matcher.matches(request) || this.matcher2.matches(request))
            filterChain.doFilter(request, response);
        else if (request.getSession() != null && !hasSecurityContext(request)) {
            log.info("CustomDefaultLogoutFilter missing SPRING_SECURITY_CONTEXT sessionId: "
                    + request.getSession().getId());
            response.setStatus(HttpStatus.FORBIDDEN.value());
        } else if (request.getSession() == null) {
            log.info("CustomDefaultLogoutFilter session is null");
            response.setStatus(HttpStatus.FORBIDDEN.value());
        } else
            filterChain.doFilter(request, response);
    }

    private boolean hasSecurityContext(HttpServletRequest request) {
        return request.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null;
    }
}
