/*
 * VirtualTradingSecurityConfiguration.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.config;

import com.yktsang.virtrade.api.jwt.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * The security configuration for the application.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class VirtualTradingSecurityConfiguration {

    /**
     * Returns the security filter chain.
     * Configures how the endpoints are passing through the application.
     *
     * @param http the HTTP security
     * @return the security filter chain
     * @throws Exception when a problem occurs
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // disable CSRF
                .csrf(AbstractHttpConfigurer::disable)


                // permit all section
                .authorizeHttpRequests((auth) -> auth.requestMatchers(
                        "/**" // index page
                        , "/admin" // admin index
                        , "/api" // API index (swagger)
                        , "/api/v1/login" // login (no need JWT)
                        , "/api/v1/registration" // registration (no need JWT)
                        , "/api/v1/password/reset" // reset password (no need JWT)
                        , "/api/v1/refreshToken" // refresh token (no need JWT)
                ).permitAll())


                // authenticated section
                .authorizeHttpRequests((auth) -> auth.requestMatchers(
                        "/member/**" // member UI
                        , "/admin/**" // admin UI
                        , "/api/v1/member/**" // API endpoints for member (need JWT)
                        , "/api/v1/admin/**" // API endpoints for admin (need JWT)
                ).authenticated())


                // session management
                .sessionManagement((sess) -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // auth provider
                .authenticationProvider(this.authenticationProvider())
                // custom filter
                .addFilterBefore(this.authFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Returns the auth filter.
     *
     * @return the auth filter
     */
    @Bean
    public VirtualTradingAuthFilter authFilter() {
        return new VirtualTradingAuthFilter();
    }

    /**
     * Returns the authentication provider.
     *
     * @return the authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(this.userDetailsService());
        authenticationProvider.setPasswordEncoder(this.passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Returns the user details service.
     *
     * @return the user details service
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    /**
     * Returns the password encoder.
     * Uses Spring Security <code>BCryptPasswordEncoder</code>.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Returns the authentication manager.
     *
     * @param config the authentication configuration
     * @return the authentication manager
     * @throws Exception when a problem occurs
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}