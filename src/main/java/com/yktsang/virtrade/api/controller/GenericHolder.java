/*
 * GenericHolder.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import org.springframework.http.HttpHeaders;

import java.util.List;

/**
 * The generic holder.
 *
 * @param items   the generic list of items
 * @param headers the HttpHeaders
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record GenericHolder(List<?> items, HttpHeaders headers) {
}
