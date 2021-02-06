package com.playtomic.tests.wallet.useCase;

import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;

import java.math.BigDecimal;
import java.util.Optional;

public interface MakeChargeUseCase {

    Optional<Wallet> makeCharge(String identifier, BigDecimal amount) throws WalletErrorException;
}
