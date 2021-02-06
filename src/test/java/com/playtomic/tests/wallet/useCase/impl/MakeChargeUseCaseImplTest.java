package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.exception.WalletError;
import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.util.WalletMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MakeChargeUseCaseImplTest {

    @Mock
    private WalletRepository mockRepository;

    @InjectMocks
    private MakeChargeUseCaseImpl useCase;

    @Test
    @DisplayName("Make a charge to a wallet with positive value for amount to charge")
    void makeChargeToExistingWalletWithPositiveAmount() {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));

        given(mockRepository.findById(eq(wallet.getIdentifier()))).willReturn(Optional.of(wallet));

        Optional<Wallet> result = useCase.makeCharge(wallet.getIdentifier(), new BigDecimal(10));

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(wallet.getIdentifier(), result.get().getIdentifier());
        assertEquals(new BigDecimal(90), result.get().getBalance());

        verify(mockRepository, times(1)).findById(eq(wallet.getIdentifier()));
        verify(mockRepository, times(1)).save(same(wallet));
    }

    @Test
    @DisplayName("Make a charge to a wallet without enough balance")
    void makeChargeToExistingWalletWithoutEnoughBalance() {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(5));

        given(mockRepository.findById(eq(wallet.getIdentifier()))).willReturn(Optional.of(wallet));

        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.makeCharge(wallet.getIdentifier(), new BigDecimal(10)));
        assertEquals(WalletError.INSUFFICIENT_BALANCE, exception.getError());

        verify(mockRepository, times(1)).findById(eq(wallet.getIdentifier()));
        verify(mockRepository, never()).save(same(wallet));
    }

    @Test
    @DisplayName("Make a charge to a wallet with negative value for amount to charge")
    void makeChargeToExistingWalletWithNegativeAmount() {

        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.makeCharge(UUID.randomUUID().toString(), new BigDecimal(-1)));
        assertEquals(WalletError.INVALID_CHARGE_VALUE, exception.getError());
    }

    @Test
    @DisplayName("Make a charge to a wallet with zero as value for amount to charge")
    void makeChargeToExistingWalletWithZeroAsAmount() {
        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.makeCharge(UUID.randomUUID().toString(), BigDecimal.ZERO));
        assertEquals(WalletError.INVALID_CHARGE_VALUE, exception.getError());
    }

    @Test
    @DisplayName("Make a charge to a wallet with null value for amount to charge")
    void makeChargeToExistingWalletWithNullAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.makeCharge(UUID.randomUUID().toString(), null));
    }

    @Test
    @DisplayName("Make a charge to a wallet with null wallet identifier")
    void makeChargeToExistingWalletWithNullWalletIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.makeCharge(null, new BigDecimal(10)));
    }

    @Test
    @DisplayName("Make a charge to not found wallet")
    void makeChargeToNotExistingWallet() {

        given(mockRepository.findById(anyString())).willReturn(Optional.empty());

        Optional<Wallet> result = useCase.makeCharge(UUID.randomUUID().toString(), new BigDecimal(10));

        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(mockRepository, times(1)).findById(anyString());
        verify(mockRepository, never()).save(any());

    }
}