/*
 * RiskToleranceLevelEnumConverter.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.entity;

import jakarta.persistence.AttributeConverter;

/**
 * The <code>RiskToleranceLevel</code> enum converter.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public class RiskToleranceLevelEnumConverter implements AttributeConverter<Enum<RiskToleranceLevel>, String> {

    /**
     * Converts risk tolerance level to database column.
     *
     * @param riskToleranceLevelEnum the risk tolerance level enum
     * @return the database value
     */
    @Override
    public String convertToDatabaseColumn(Enum<RiskToleranceLevel> riskToleranceLevelEnum) {
        return riskToleranceLevelEnum.name();
    }

    /**
     * Converts risk tolerance level from database column.
     *
     * @param str the database value
     * @return the risk tolerance level enum
     * @throws IllegalStateException if database value does not match
     */
    @Override
    public Enum<RiskToleranceLevel> convertToEntityAttribute(String str) {
        return switch (str.toLowerCase()) {
            case "low" -> RiskToleranceLevel.LOW;
            case "high" -> RiskToleranceLevel.HIGH;
            case "medium" -> RiskToleranceLevel.MEDIUM;
            default -> throw new IllegalStateException("Unexpected value: " + str.toLowerCase());
        };
    }

}
