/*
 * BankAccountTransaction.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yktsang.virtrade.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * The bank account transaction.
 * Fields come from <code>com.yktsang.virtrade.entity.BankAccountTransaction</code>.
 *
 * @param email                  the email address
 * @param currency               the currency
 * @param transactionDateTime    the transaction datetime
 * @param transactionDescription the transaction description
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
public record BankAccountTransaction(String email, String currency,
                                     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                                     LocalDateTime transactionDateTime,
                                     String transactionDescription) {

    /**
     * Returns the transaction datetime as <code>java.util.Date</code>
     *
     * @return the transaction datetime as java.util.Date
     */
    @JsonIgnore
    public Date transactionDateTimeAsDate() {
        return DateTimeUtil.toDate(transactionDateTime);
    }

}
