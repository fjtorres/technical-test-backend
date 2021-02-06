package com.playtomic.tests.wallet.exception;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Enum with application errors.
 */
public enum WalletError {

    INVALID_CHARGE_VALUE("BUS-001", "Value to charge should be a positive number greater then 0."),
    INSUFFICIENT_BALANCE("BUS-002", "Balance is not enough to make the charge."),
    PAYMENT_ERROR("BUS-003", "Recharge cannot be completed because we found a problem to charge it to third party platform.");


    final String code;
    final String message;

    WalletError(String pCode, String pMessage) {
        this.code = pCode;
        this.message = pMessage;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("code", code)
                .append("message", message)
                .toString();
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
