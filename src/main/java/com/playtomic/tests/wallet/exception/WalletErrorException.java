package com.playtomic.tests.wallet.exception;

/**
 * Wrapper exception for application errors.
 */
public class WalletErrorException extends RuntimeException {

    private final WalletError error;

    public WalletErrorException(WalletError pError) {
        this.error = pError;
    }

    public WalletErrorException(WalletError pError, Throwable cause) {
        super(cause);
        this.error = pError;
    }

    @Override
    public String getMessage() {
        return error.toString();
    }

    public WalletError getError() {
        return error;
    }
}
