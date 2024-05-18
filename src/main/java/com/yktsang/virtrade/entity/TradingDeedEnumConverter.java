/*
 * TradingDeedEnumConverter.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import jakarta.persistence.AttributeConverter;

/**
 * The <code>TradingDeed</code> enum converter.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class TradingDeedEnumConverter implements AttributeConverter<Enum<TradingDeed>, String> {

    /**
     * Converts trading deed enum to database column.
     *
     * @param tradingDeedEnum the trading deed enum
     * @return the database value
     */
    @Override
    public String convertToDatabaseColumn(Enum<TradingDeed> tradingDeedEnum) {
        return tradingDeedEnum.name();
    }

    /**
     * Converts trading deed enum from database column.
     *
     * @param str the database value
     * @return the trading deed enum
     * @throws IllegalStateException if database value does not match
     */
    @Override
    public Enum<TradingDeed> convertToEntityAttribute(String str) {
        return switch (str.toLowerCase()) {
            case "buy" -> TradingDeed.BUY;
            case "sell" -> TradingDeed.SELL;
            default -> throw new IllegalStateException("Unexpected value: " + str.toLowerCase());
        };
    }

}
