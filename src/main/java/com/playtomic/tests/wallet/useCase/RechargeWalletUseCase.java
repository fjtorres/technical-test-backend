package com.playtomic.tests.wallet.useCase;

import com.playtomic.tests.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

public interface RechargeWalletUseCase {

    Optional<Wallet> recharge(String identifier, BigDecimal amount);
}
