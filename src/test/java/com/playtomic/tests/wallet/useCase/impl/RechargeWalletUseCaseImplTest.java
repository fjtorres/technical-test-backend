package com.playtomic.tests.wallet.useCase.impl;

import com.playtomic.tests.wallet.exception.WalletError;
import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.respository.WalletRepository;
import com.playtomic.tests.wallet.service.PaymentService;
import com.playtomic.tests.wallet.service.PaymentServiceException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RechargeWalletUseCaseImplTest {

    @Mock
    private WalletRepository mockRepository;

    @Mock
    private PaymentService mockPaymentService;

    @InjectMocks
    private RechargeWalletUseCaseImpl useCase;

    @Test
    @DisplayName("Make a recharge to a wallet with positive value for amount to charge")
    void rechargeToExistingWalletWithPositiveAmount() throws PaymentServiceException {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));

        given(mockRepository.findById(eq(wallet.getIdentifier()))).willReturn(Optional.of(wallet));

        Optional<Wallet> result = useCase.recharge(wallet.getIdentifier(), new BigDecimal(10));

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(wallet.getIdentifier(), result.get().getIdentifier());
        assertEquals(new BigDecimal(110), result.get().getBalance());

        verify(mockRepository, times(1)).findById(eq(wallet.getIdentifier()));
        verify(mockRepository, times(1)).save(same(wallet));
        verify(mockPaymentService, times(1)).charge(any());
    }

    @Test
    @DisplayName("Make a recharge to a wallet with negative value for amount to charge")
    void rechargeToExistingWalletWithNegativeAmount() {

        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.recharge(UUID.randomUUID().toString(), new BigDecimal(-1)));
        assertEquals(WalletError.INVALID_CHARGE_VALUE, exception.getError());
    }

    @Test
    @DisplayName("Make a recharge to a wallet with zero as value for amount to charge")
    void makeChargeToExistingWalletWithZeroAsAmount() {
        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.recharge(UUID.randomUUID().toString(), BigDecimal.ZERO));
        assertEquals(WalletError.INVALID_CHARGE_VALUE, exception.getError());
    }

    @Test
    @DisplayName("Make a recharge to a wallet with null value for amount to charge")
    void makeChargeToExistingWalletWithNullAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.recharge(UUID.randomUUID().toString(), null));
    }

    @Test
    @DisplayName("Make a recharge to a wallet with null wallet identifier")
    void makeChargeToExistingWalletWithNullWalletIdentifier() {
        assertThrows(IllegalArgumentException.class,
                () -> useCase.recharge(null, new BigDecimal(10)));
    }

    @Test
    @DisplayName("Make a recharge to not found wallet")
    void makeChargeToNotExistingWallet() throws PaymentServiceException {

        given(mockRepository.findById(anyString())).willReturn(Optional.empty());

        Optional<Wallet> result = useCase.recharge(UUID.randomUUID().toString(), new BigDecimal(10));

        assertNotNull(result);
        assertFalse(result.isPresent());

        verify(mockRepository, times(1)).findById(anyString());
        verify(mockRepository, never()).save(any());
        verify(mockPaymentService, never()).charge(any());

    }

    @Test
    @DisplayName("Make a charge and receive a payment service exception")
    void makeChargeWithPaymentServiceException() throws PaymentServiceException {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));

        given(mockRepository.findById(eq(wallet.getIdentifier()))).willReturn(Optional.of(wallet));
        willThrow(new PaymentServiceException()).given(mockPaymentService).charge(any());

        WalletErrorException exception = assertThrows(WalletErrorException.class,
                () -> useCase.recharge(wallet.getIdentifier(), new BigDecimal(10)));
        assertEquals(WalletError.PAYMENT_ERROR, exception.getError());

        verify(mockRepository, times(1)).findById(eq(wallet.getIdentifier()));
        verify(mockRepository, never()).save(same(wallet));
        verify(mockPaymentService, times(1)).charge(any());
    }
}