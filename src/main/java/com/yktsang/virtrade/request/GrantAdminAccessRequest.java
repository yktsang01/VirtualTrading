/*
 * GrantAdminAccessRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

import java.util.List;

/**
 * The grant admin access request.
 *
 * @param emailsToGrant the email addresses to grant
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record GrantAdminAccessRequest(List<String> emailsToGrant) {
}
