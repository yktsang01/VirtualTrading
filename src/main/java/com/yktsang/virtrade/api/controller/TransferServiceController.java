/*
 * TransferServiceController.java
 *
 * Virtual Trading is a web application simulating online stock trading.
 *
 * This class or interface is part of the Virtual Trading project.
 * The class or interface must not be used outside of this context.
 */
package com.yktsang.virtrade.api.controller;

import com.yktsang.virtrade.api.jwt.JwtService;
import com.yktsang.virtrade.entity.*;
import com.yktsang.virtrade.request.TransferFundRequest;
import com.yktsang.virtrade.response.ErrorResponse;
import com.yktsang.virtrade.response.SuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * The API controller for implementing <code>TransferService</code>.
 *
 * @author Tsang Yiu Kee Kay
 * @version 1.0
 */
@RestController
public class TransferServiceController implements TransferService {

    /**
     * The maximum account balance limit (1 trillion).
     */
    private static final BigDecimal AMOUNT_LIMIT_TRILLION = new BigDecimal("1000000000000");
    /**
     * The logger.
     */
    private final Logger logger = LoggerFactory.getLogger(TransferServiceController.class);
    /**
     * The JWT service.
     */
    @Autowired
    private JwtService jwtService;
    /**
     * The account balance repository.
     */
    @Autowired
    private AccountBalanceRepository acctBalRepo;
    /**
     * The account transaction repository.
     */
    @Autowired
    private AccountTransactionRepository acctTxnRepo;
    /**
     * The bank account repository.
     */
    @Autowired
    private BankAccountRepository bankAcctRepo;
    /**
     * The bank account transaction repository.
     */
    @Autowired
    private BankAccountTransactionRepository bankAcctTxnRepo;

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseEntity<?> transferFunds(RequestEntity<TransferFundRequest> req) {
        String tokenUser = jwtService.extractUsernameFromHeaders(req.getHeaders());

        if (req.hasBody() && Objects.nonNull(req.getBody())) {
            TransferFundRequest actualReq = req.getBody();

            if (
                    Objects.isNull(actualReq.fromAccountCurrency())
                            || actualReq.fromAccountCurrency().isEmpty()
                            || Objects.isNull(actualReq.toBankAccountId())
                            || Objects.isNull(actualReq.transferAmount())

                            // transferAmount not positive (>0)
                            || actualReq.transferAmount().signum() != 1

                            // request transferAmount is greater than or equals to one trillion limit
                            || actualReq.transferAmount().compareTo(AMOUNT_LIMIT_TRILLION) >= 0
            ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Validation failed"));
            }

            String accountCcy = actualReq.fromAccountCurrency();
            BigInteger bankAccountId = actualReq.toBankAccountId();
            BigDecimal transferAmount = actualReq.transferAmount();

            Optional<AccountBalance> fromAccountOpt = acctBalRepo.findById(new AccountBalancePK(tokenUser, accountCcy));
            Optional<BankAccount> toBankOpt = bankAcctRepo.findById(bankAccountId);

            if (fromAccountOpt.isPresent() && toBankOpt.isPresent()) {
                AccountBalance fromAccount = fromAccountOpt.get();
                BankAccount toBank = toBankOpt.get();

                // check bank account destination belong to caller tokenUser
                if (!toBank.getEmail().equals(tokenUser)) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Bank account ID not belong to caller"));
                }

                // check bank account destination is active (in use)
                if (!toBank.isInUse()) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Bank account ID not in use"));
                }

                // check currency matches in both source and destination
                if (!fromAccount.getCurrency().equalsIgnoreCase(toBank.getCurrency())) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Account balance and bank account currency not match"));
                }

                // check sufficient funds in account balance source
                // balance is less than request transferAmount
                if (fromAccount.getNonTradingAmount().compareTo(transferAmount) < 0) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(new ErrorResponse("Insufficient funds to transfer"));
                }

                BigDecimal newAmount = fromAccount.getNonTradingAmount().subtract(transferAmount);
                fromAccount.setNonTradingAmount(newAmount);
                fromAccount.setLastUpdatedDateTime(LocalDateTime.now());
                acctBalRepo.save(fromAccount);
                logger.info("account balance updated");
                String txnDesc = "Transferred " + accountCcy + " "
                        + new DecimalFormat("#,###.0000").format(transferAmount)
                        + " to bank " + toBank.getBankName()
                        + " with bank account number " + toBank.getBankAccountNumber()
                        + " for currency " + toBank.getCurrency();
                AccountTransaction acctTxn = new AccountTransaction(tokenUser, accountCcy, txnDesc);
                acctTxnRepo.save(acctTxn);
                logger.info("account transaction created");
                BankAccountTransaction bankTxn = new BankAccountTransaction(tokenUser, accountCcy, txnDesc);
                bankAcctTxnRepo.save(bankTxn);
                logger.info("bank account transaction created");
                logger.info("funds transferred");
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new SuccessResponse("Transfer successful"));

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Account balance source and/or bank account destination not found"));
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request"));
        }
    }

}
