package com.playtomic.tests.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.playtomic.tests.wallet.api.WalletController.ChargeRequestDto;
import com.playtomic.tests.wallet.api.WalletController.RechargeRequestDto;
import com.playtomic.tests.wallet.exception.WalletError;
import com.playtomic.tests.wallet.exception.WalletErrorException;
import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.useCase.GetWalletUseCase;
import com.playtomic.tests.wallet.useCase.MakeChargeUseCase;
import com.playtomic.tests.wallet.useCase.RechargeWalletUseCase;
import com.playtomic.tests.wallet.util.ApiConstants;
import com.playtomic.tests.wallet.util.WalletMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration(classes = {WalletController.class, ExceptionHandlerController.class})
@WebMvcTest
public class WalletControllerTest {

    @MockBean
    private GetWalletUseCase mockGetWalletUseCase;

    @MockBean
    private MakeChargeUseCase mockMakeChargeUseCase;

    @MockBean
    private RechargeWalletUseCase mockRechargeWalletUseCase;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Obtain a wallet by identifier with result status OK (200)")
    void getWalletByIdentifierWithStatusOk() throws Exception {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));

        given(mockGetWalletUseCase.getWallet(eq(wallet.getIdentifier())))
                .willReturn(Optional.of(wallet));

        mockMvc.perform(get(ApiConstants.GET_WALLET_URL, wallet.getIdentifier())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(wallet.getIdentifier()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()))
                .andReturn();

        verify(mockGetWalletUseCase, times(1)).getWallet(eq(wallet.getIdentifier()));
    }

    @Test
    @DisplayName("Obtain a wallet by identifier with result status NOT FOUND (404)")
    void getWalletByIdentifierWithStatusNotFound() throws Exception {

        given(mockGetWalletUseCase.getWallet(anyString()))
                .willReturn(Optional.empty());

        mockMvc.perform(get(ApiConstants.GET_WALLET_URL, UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        verify(mockGetWalletUseCase, times(1)).getWallet(anyString());
    }

    @Test
    @DisplayName("Make a charge to a wallet with result status OK (200)")
    void chargeWalletWithStatusOk() throws Exception {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));
        ChargeRequestDto request = new ChargeRequestDto(new BigDecimal(10));

        given(mockMakeChargeUseCase.makeCharge(eq(wallet.getIdentifier()), any()))
                .willReturn(Optional.of(wallet));

        mockMvc.perform(post(ApiConstants.CHARGE_WALLET_URL, wallet.getIdentifier())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(wallet.getIdentifier()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()))
                .andReturn();

        verify(mockMakeChargeUseCase, times(1))
                .makeCharge(eq(wallet.getIdentifier()), eq(request.getAmount()));
    }

    @Test
    @DisplayName("Make a charge to a wallet with result status NOT FOUND (404)")
    void chargeWalletWithStatusNotFound() throws Exception {

        given(mockMakeChargeUseCase.makeCharge(anyString(), any())).willReturn(Optional.empty());

        mockMvc.perform(post(ApiConstants.CHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new ChargeRequestDto(new BigDecimal(10))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        verify(mockMakeChargeUseCase, times(1)).makeCharge(anyString(), any());
    }

    @Test
    @DisplayName("Make a charge to a wallet with business error, status BAD REQUEST (400)")
    void chargeWalletWithBusinessError() throws Exception {

        willThrow(new WalletErrorException(WalletError.INSUFFICIENT_BALANCE))
                .given(mockMakeChargeUseCase)
                .makeCharge(anyString(), any());

        mockMvc.perform(post(ApiConstants.CHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new ChargeRequestDto(new BigDecimal(10))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[*].code").exists())
                .andExpect(jsonPath("$.details[*].message").exists())
                .andReturn();

        verify(mockMakeChargeUseCase, times(1)).makeCharge(anyString(), any());
    }

    @Test
    @DisplayName("Make a charge to a wallet without amount field, status BAD REQUEST (400)")
    void chargeWalletWithoutAmount() throws Exception {

        mockMvc.perform(post(ApiConstants.CHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new ChargeRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[*].code").exists())
                .andExpect(jsonPath("$.details[*].message").exists())
                .andReturn();

        verify(mockMakeChargeUseCase, never()).makeCharge(anyString(), any());
    }

    @Test
    @DisplayName("Make a recharge to a wallet with result status OK (200)")
    void rechargeWalletWithStatusOk() throws Exception {

        Wallet wallet = WalletMother.newWalletWithBalance(new BigDecimal(100));
        RechargeRequestDto request = new RechargeRequestDto(new BigDecimal(10));

        given(mockRechargeWalletUseCase.recharge(eq(wallet.getIdentifier()), any()))
                .willReturn(Optional.of(wallet));

        mockMvc.perform(post(ApiConstants.RECHARGE_WALLET_URL, wallet.getIdentifier())
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.identifier").exists())
                .andExpect(jsonPath("$.identifier").isString())
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.balance").isNumber())
                .andExpect(jsonPath("$.identifier").value(wallet.getIdentifier()))
                .andExpect(jsonPath("$.balance").value(wallet.getBalance()))
                .andReturn();

        verify(mockRechargeWalletUseCase, times(1))
                .recharge(eq(wallet.getIdentifier()), eq(request.getAmount()));
    }

    @Test
    @DisplayName("Make a recharge to a wallet with result status NOT FOUND (404)")
    void rechargeWalletWithStatusNotFound() throws Exception {

        given(mockRechargeWalletUseCase.recharge(anyString(), any())).willReturn(Optional.empty());

        mockMvc.perform(post(ApiConstants.RECHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new RechargeRequestDto(new BigDecimal(10))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();

        verify(mockRechargeWalletUseCase, times(1)).recharge(anyString(), any());
    }

    @Test
    @DisplayName("Make a recharge to a wallet with business error, status BAD REQUEST (400)")
    void rechargeWalletWithBusinessError() throws Exception {

        willThrow(new WalletErrorException(WalletError.PAYMENT_ERROR))
                .given(mockRechargeWalletUseCase)
                .recharge(anyString(), any());

        mockMvc.perform(post(ApiConstants.RECHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new RechargeRequestDto(new BigDecimal(10))))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[*].code").exists())
                .andExpect(jsonPath("$.details[*].message").exists())
                .andReturn();

        verify(mockRechargeWalletUseCase, times(1)).recharge(anyString(), any());
    }

    @Test
    @DisplayName("Make a recharge to a wallet without amount field, status BAD REQUEST (400)")
    void rechargeWalletWithoutAmount() throws Exception {

        mockMvc.perform(post(ApiConstants.RECHARGE_WALLET_URL, UUID.randomUUID().toString())
                .content(objectMapper.writeValueAsString(new RechargeRequestDto()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details[*].code").exists())
                .andExpect(jsonPath("$.details[*].message").exists())
                .andReturn();

        verify(mockMakeChargeUseCase, never()).makeCharge(anyString(), any());
    }

}