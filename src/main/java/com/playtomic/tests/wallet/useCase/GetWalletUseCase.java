package com.playtomic.tests.wallet.useCase;

import com.playtomic.tests.wallet.model.Wallet;

import java.util.Optional;

public interface GetWalletUseCase {

    Optional<Wallet> getWallet(String identifier);
}
