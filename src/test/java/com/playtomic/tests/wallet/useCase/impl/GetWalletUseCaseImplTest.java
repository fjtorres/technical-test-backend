package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.util.WalletMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetWalletUseCaseImplTest {

    @Mock
    private WalletRepository mockRepository;

    @InjectMocks
    private GetWalletUseCaseImpl useCase;

    @Test
    @DisplayName("Try to obtain a wallet by a valid identifier")
    void getWalletWithValidIdentifier() {
        Wallet wallet = WalletMother.newWallet();

        given(mockRepository.findById(eq(wallet.getIdentifier()))).willReturn(Optional.of(wallet));
        Optional<Wallet> result = useCase.getWallet(wallet.getIdentifier());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(wallet.getIdentifier(), result.get().getIdentifier());
        assertEquals(wallet.getBalance(), result.get().getBalance());
    }

    @Test
    @DisplayName("Try to obtain a wallet with empty string as identifier")
    void getWalletWithEmptyIdentifier() {

        Optional<Wallet> result = useCase.getWallet("");

        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(mockRepository, never()).findById(anyString());
    }

    @Test
    @DisplayName("Try to obtain a wallet with null as identifier")
    void getWalletWithNullIdentifier() {

        Optional<Wallet> result = useCase.getWallet(null);

        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(mockRepository, never()).findById(anyString());
    }
}