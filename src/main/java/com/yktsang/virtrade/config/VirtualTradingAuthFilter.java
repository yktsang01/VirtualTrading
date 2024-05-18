/*
 * VirtualTradingAuthFilter.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.config;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.api.jwt.UserDetailsServiceImpl;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.util.JsonUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MissingClaimException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * The web filter for API authentication and authorization purposes.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@WebFilter
public class VirtualTradingAuthFilter extends OncePerRequestFilter {

    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(VirtualTradingAuthFilter.class);
    /**
     * The user details service implementation.
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * Intercepts requests requiring a JWT in the authorization header, bypasses requests otherwise.
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain
     * @throws ServletException when servlet processing fails
     * @throws IOException      when I/O processing fails
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        try {
            if (Objects.isNull(authHeader)
                    && ((requestUri.startsWith("/api/member") || requestUri.startsWith("/api/admin")))) {
                throw new MissingClaimException(null, null, null, null, "Missing authentication token");
            }

            String token = null;
            String username = null;
            if (Objects.nonNull(authHeader)
                    && authHeader.startsWith("Bearer")) {
                token = authHeader.substring(7);
                username = jwtService.extractUsername(token);
            }

            if (Objects.nonNull(username)
                    && Objects.isNull(SecurityContextHolder.getContext().getAuthentication())) {

                UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("token validated");
                }
            }
        } catch (MissingClaimException mce) {
            logger.error(mce.getMessage());
            this.exceptionHandler(response, HttpServletResponse.SC_UNAUTHORIZED, "Missing authentication token");
            return;
        } catch (UsernameNotFoundException unfe) {
            logger.error(unfe.getMessage());
            this.exceptionHandler(response, HttpServletResponse.SC_NOT_FOUND, "Account not found");
            return;
        } catch (ExpiredJwtException | SignatureException ex) {
            logger.error(ex.getMessage());
            this.exceptionHandler(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid authentication token");
            return;
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Provides exception handling using the <code>ErrorResponse</code>.
     *
     * @param response     the HTTP servlet response
     * @param statusCode   the HTTP status code
     * @param errorMessage the error message
     * @throws IOException when I/O processing fails
     */
    private void exceptionHandler(HttpServletResponse response, int statusCode, String errorMessage)
            throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(statusCode);
        PrintWriter writer = response.getWriter();
        ErrorResponse err = new ErrorResponse(errorMessage);
        writer.print(JsonUtil.toJson(err));
    }

}
