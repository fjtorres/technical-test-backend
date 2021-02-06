package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.exception.WalletError;
import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.useCase.MakeChargeUseCase;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Component
@Transactional
public class MakeChargeUseCaseImpl implements MakeChargeUseCase {

    private final WalletRepository walletRepository;

    public MakeChargeUseCaseImpl(WalletRepository pWalletRepository) {
        this.walletRepository = pWalletRepository;
    }

    @Override
    public Optional<Wallet> makeCharge(String identifier, BigDecimal amount) {

        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier is required.");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Amunt is required.");
        }

        if (BigDecimal.ZERO.equals(amount) || amount.doubleValue() < 0) {
            throw new WalletErrorException(WalletError.INVALID_CHARGE_VALUE);
        }

        return walletRepository.findById(identifier).map(wallet -> {

            if (BigDecimal.ZERO.equals(wallet.getBalance())
                    || amount.doubleValue() > wallet.getBalance().doubleValue()) {
                throw new WalletErrorException(WalletError.INSUFFICIENT_BALANCE);
            }

            wallet.setBalance(wallet.getBalance().subtract(amount));

            walletRepository.save(wallet);

            return wallet;
        });
    }
}
