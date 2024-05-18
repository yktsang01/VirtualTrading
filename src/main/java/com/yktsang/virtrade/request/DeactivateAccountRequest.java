/*
 * DeactivateAccountRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The deactivate account request.
 *
 * @param deactivate         the deactivate indicator
 * @param deactivationReason the deactivation reason
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record DeactivateAccountRequest(Boolean deactivate, String deactivationReason) {
}
