package com.rsjavasolutions.securityinmemory.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("robert")
                .roles("ADMIN", "USER")
                .password(passwordEncoder().encode("password"))
                .and()
                .withUser("rambo")
                .roles("ADMIN", "USER")
                .password(passwordEncoder().encode("password"))
                .and()
                .withUser("rocky")
                .roles("USER")
                .password(passwordEncoder().encode("password"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterAfter(new CustomDefaultLogoutFilter(), DefaultLogoutPageGeneratingFilter.class)
                .authorizeRequests()
                .antMatchers("/login", "/actuator/**").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().formLogin()
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .and().logout()
                .logoutSuccessHandler(logoutSuccessHandler())
                .and()
                .csrf().disable()
                .httpBasic()
                .and()
                .sessionManagement().maximumSessions(1);
    }

    private AuthenticationSuccessHandler loginSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            log.info("loginSuccessHandler name: {}, sessionId: {} ", authentication.getName()
                    , httpServletRequest.getSession().getId());
            httpServletResponse.setStatus(HttpStatus.OK.value());
        };
    }

    private AuthenticationFailureHandler loginFailureHandler() {
        return (httpServletRequest, httpServletResponse, exception)
                -> httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    private LogoutSuccessHandler logoutSuccessHandler() {
        return (httpServletRequest, httpServletResponse, authentication) -> {
            log.info("logoutSuccessHandler name: {}, sessionId: {} ", authentication.getName()
                    , httpServletRequest.getSession().getId());
            httpServletResponse.setStatus(HttpStatus.RESET_CONTENT.value());
        };
    }
}

