/*
 * SearchRequest.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.request;

/**
 * The search request
 *
 * @param symbolOrName the search option: symbol or name
 * @param criteria     the search criteria
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record SearchRequest(String symbolOrName, String criteria) {
}
