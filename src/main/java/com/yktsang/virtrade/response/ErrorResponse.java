/*
 * ErrorResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

/**
 * The error response.
 * Used whenever the intended API response fails.
 *
 * @param errorMessage the error message
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record ErrorResponse(String errorMessage) {
}
