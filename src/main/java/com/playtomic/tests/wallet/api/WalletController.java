package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.model.Wallet;
import com.playtomic.tests.wallet.useCase.GetWalletUseCase;
import com.playtomic.tests.wallet.useCase.MakeChargeUseCase;
import com.playtomic.tests.wallet.useCase.RechargeWalletUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@RestController
@RequestMapping(WalletController.BASE_URL)
public class WalletController {

    public static final String BASE_URL = "/wallet";
    public static final String GET_BY_ID = "/{id}";
    public static final String CHARGE = "/{id}/charge";
    public static final String RECHARGE = "/{id}/recharge";

    private final GetWalletUseCase getWalletUseCase;
    private final MakeChargeUseCase makeChargeUseCase;
    private final RechargeWalletUseCase rechargeWalletUseCase;

    @Autowired
    public WalletController(final GetWalletUseCase pGetWalletUseCase,
                            final MakeChargeUseCase pMakeChargeUseCase,
                            final RechargeWalletUseCase rechargeWalletUseCase) {
        this.getWalletUseCase = pGetWalletUseCase;
        this.makeChargeUseCase = pMakeChargeUseCase;
        this.rechargeWalletUseCase = rechargeWalletUseCase;
    }

    @GetMapping(GET_BY_ID)
    public ResponseEntity<Wallet> getWalletById(@PathVariable("id") String identifier) {
        return getWalletUseCase.getWallet(identifier)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(CHARGE)
    public ResponseEntity<Wallet> charge(@PathVariable("id") String identifier, @Valid @RequestBody ChargeRequestDto request) {
        return makeChargeUseCase.makeCharge(identifier, request.getAmount())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(RECHARGE)
    public ResponseEntity<Wallet> recharge(@PathVariable("id") String identifier, @Valid @RequestBody RechargeRequestDto request) {
        return rechargeWalletUseCase.recharge(identifier, request.getAmount())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Wrapper class for make a charge.
     */
    public static class ChargeRequestDto {

        @NotNull(message = "Amount is required.")
        private BigDecimal amount;

        public ChargeRequestDto() {
        }

        public ChargeRequestDto(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    /**
     * Wrapper class for recharge.
     */
    public static class RechargeRequestDto {

        @NotNull(message = "Amount is required.")
        private BigDecimal amount;

        public RechargeRequestDto() {
        }

        public RechargeRequestDto(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
