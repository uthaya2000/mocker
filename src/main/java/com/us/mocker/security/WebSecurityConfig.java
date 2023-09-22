package com.us.mocker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private GoogleOauthUserService googleOauthUserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request.requestMatchers("/", "/oauth2/**", "/api/**", "/img/**")
                        .permitAll()
                .anyRequest().authenticated())
                .oauth2Login(oAuthUser -> {
                    oAuthUser.loginPage("/");
                    oAuthUser.userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(googleOauthUserService));
                    oAuthUser.successHandler((request, response, authentication) -> response.sendRedirect("/collection"));
                }).logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .addLogoutHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                            try {
                                response.sendRedirect("/");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }));

        return httpSecurity.build();
    }
}
