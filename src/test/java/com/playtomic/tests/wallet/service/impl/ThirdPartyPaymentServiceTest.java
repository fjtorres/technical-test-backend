package com.playtomic.tests.wallet.service.impl;


import com.playtomic.tests.wallet.service.PaymentServiceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ThirdPartyPaymentServiceTest {

    ThirdPartyPaymentService s = new ThirdPartyPaymentService();

    @Test
    public void test_exception() {
        assertThrows(PaymentServiceException.class, () -> s.charge(new BigDecimal(5)));
    }

    @Test
    public void test_ok() throws PaymentServiceException {
        s.charge(new BigDecimal(15));
    }
}
