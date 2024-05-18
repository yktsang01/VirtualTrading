/*
 * BooleanConverter.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import jakarta.persistence.AttributeConverter;

/**
 * Converts boolean to/from database column.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class BooleanConverter implements AttributeConverter<Boolean, String> {

    /**
     * Converts boolean to database column.
     *
     * @param bool the boolean
     * @return the database value
     */
    @Override
    public String convertToDatabaseColumn(Boolean bool) {
        return bool ? "Y" : "N";
    }

    /**
     * Converts boolean from database column.
     *
     * @param str the database value
     * @return the boolean
     */
    @Override
    public Boolean convertToEntityAttribute(String str) {
        return str.equalsIgnoreCase("Y");
    }

}
