package com.playtomic.tests.wallet.util;

import com.playtomic.tests.wallet.model.Wallet;

import java.math.BigDecimal;

/**
 * Objet mother class to create instance of {@link Wallet} class.
 */
public final class WalletMother {

    /**
     * Create a {@link Wallet} instance with specific balance.
     *
     * @param balance Balance value.
     * @return New {@link Wallet} instance
     */
    public static Wallet newWalletWithBalance(BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setBalance(balance);

        return wallet;
    }

    /**
     * Create a {@link Wallet} instance with zero as balance.
     *
     * @return New {@link Wallet} instance.
     */
    public static Wallet newWallet() {
        return newWalletWithBalance(BigDecimal.ZERO);
    }
}
