package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.exception.WalletError;
import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.service.PaymentService;
import com.playtomic.tests.wallet.service.PaymentServiceException;
import com.playtomic.tests.wallet.useCase.RechargeWalletUseCase;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Component
@Transactional
public class RechargeWalletUseCaseImpl implements RechargeWalletUseCase {

    private final PaymentService paymentService;
    private final WalletRepository walletRepository;

    public RechargeWalletUseCaseImpl(PaymentService pPaymentService, WalletRepository pWalletRepository) {
        this.paymentService = pPaymentService;
        this.walletRepository = pWalletRepository;
    }

    @Override
    public Optional<Wallet> recharge(String identifier, BigDecimal amount) {

        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier is required.");
        }

        if (amount == null) {
            throw new IllegalArgumentException("Balance is required.");
        }

        if (BigDecimal.ZERO.equals(amount) || amount.doubleValue() < 0) {
            throw new WalletErrorException(WalletError.INVALID_CHARGE_VALUE);
        }

        return walletRepository.findById(identifier).map(wallet -> {

            try {
                paymentService.charge(amount);
            } catch (PaymentServiceException e) {
                throw new WalletErrorException(WalletError.PAYMENT_ERROR, e);
            }

            wallet.setBalance(wallet.getBalance().add(amount));

            walletRepository.save(wallet);

            return wallet;
        });
    }
}
