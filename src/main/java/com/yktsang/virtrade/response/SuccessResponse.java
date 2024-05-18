/*
 * SuccessResponse.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

/**
 * The success response.
 * Used whenever the intended API response succeeds with no specific data.
 *
 * @param successMessage the success message
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record SuccessResponse(String successMessage) {
}
