package com.playtomic.tests.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.api.WalletController;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.useCase.GetWalletUseCase;
import com.playtomic.tests.wallet.util.ApiConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@Sql("/sql/test_data.sql")
public class WalletApplicationIT {

    private static final String TEST_WALLET_ID = "65027500-b98d-4d2d-bd25-d504eb625449";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GetWalletUseCase getWalletUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getWalletById() throws Exception {

        Optional<Wallet> wallet = getWalletUseCase.getWallet(TEST_WALLET_ID);

        assertTrue(wallet.isPresent());

        mockMvc.perform(get(ApiConstants.GET_WALLET_URL, TEST_WALLET_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(TEST_WALLET_ID))
                .andReturn();
    }

    @Test
    void makeChargeToWallet() throws Exception {

        Optional<Wallet> wallet = getWalletUseCase.getWallet(TEST_WALLET_ID);

        assertTrue(wallet.isPresent());

        WalletController.ChargeRequestDto request = new WalletController.ChargeRequestDto(new BigDecimal(10));

        BigDecimal expectedBalance = wallet.get().getBalance().subtract(request.getAmount());

        mockMvc.perform(post(ApiConstants.CHARGE_WALLET_URL, TEST_WALLET_ID)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(TEST_WALLET_ID))
                .andReturn();

        wallet = getWalletUseCase.getWallet(TEST_WALLET_ID);

        assertTrue(wallet.isPresent());

        assertEquals(expectedBalance, wallet.get().getBalance());
    }

    @Test
    void rechargeWallet() throws Exception {

        Optional<Wallet> wallet = getWalletUseCase.getWallet(TEST_WALLET_ID);

        assertTrue(wallet.isPresent());

        WalletController.RechargeRequestDto request = new WalletController.RechargeRequestDto(new BigDecimal(10));

        BigDecimal expectedBalance = wallet.get().getBalance().add(request.getAmount());

        mockMvc.perform(post(ApiConstants.RECHARGE_WALLET_URL, TEST_WALLET_ID)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(TEST_WALLET_ID))
                .andReturn();

        wallet = getWalletUseCase.getWallet(TEST_WALLET_ID);

        assertTrue(wallet.isPresent());

        assertEquals(expectedBalance, wallet.get().getBalance());
    }
}
