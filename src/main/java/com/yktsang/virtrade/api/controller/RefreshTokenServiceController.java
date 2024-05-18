/*
 * RefreshTokenServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.request.RefreshTokenRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.JwtResponse;
import com.yktsang.virtrade.util.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The API controller for implementing <code>RefreshTokenService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class RefreshTokenServiceController implements RefreshTokenService {
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(RefreshTokenServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> refreshToken(RequestEntity<RefreshTokenRequest> req) {
        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            RefreshTokenRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.username())
                            || actualReq.username().isEmpty()
                            || Objects.isNull(actualReq.existingToken())
                            || actualReq.existingToken().isEmpty()

                            || SecurityUtil.isEmailNotValid(actualReq.username())
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String username = actualReq.username();
            String existingToken = actualReq.existingToken();

            try {
                Date existingTokenExpiry = jwtService.extractExpiration(existingToken); //future, 30 min from creation
                Date now = new Date();
                long timeLeftInMinutes = TimeUnit.MINUTES.convert(
                        existingTokenExpiry.getTime() - now.getTime(),
                        TimeUnit.MILLISECONDS);
                if (timeLeftInMinutes <= 5L) {
                    String refreshToken = jwtService.generateToken(username);
                    logger.info("New JWT generated");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new JwtResponse(refreshToken));
                } else {
                    logger.info("Existing JWT returned");
                    return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
                }
            } catch (ExpiredJwtException eje) {
                String refreshToken = jwtService.generateToken(username);
                logger.info("New JWT generated after expired");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new JwtResponse(refreshToken));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
