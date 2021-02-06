package com.playtomic.tests.wallet.util;

import com.playtomic.tests.wallet.api.WalletController;

public class ApiConstants {

    public static final String GET_WALLET_URL = WalletController.BASE_URL + WalletController.GET_BY_ID;
    public static final String CHARGE_WALLET_URL = WalletController.BASE_URL + WalletController.CHARGE;
    public static final String RECHARGE_WALLET_URL = WalletController.BASE_URL + WalletController.RECHARGE;
}
