package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.useCase.GetWalletUseCase;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GetWalletUseCaseImpl implements GetWalletUseCase {

    private final WalletRepository walletRepository;

    public GetWalletUseCaseImpl(final WalletRepository pWalletRepository) {
        this.walletRepository = pWalletRepository;
    }

    @Override
    public Optional<Wallet> getWallet(String identifier) {

        if (ObjectUtils.isEmpty(identifier)) {
            return Optional.empty();
        }

        return walletRepository.findById(identifier);
    }
}
