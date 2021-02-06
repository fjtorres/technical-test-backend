package com.playtomic.tests.wallet.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @Column(name = "id", nullable = false, unique = true, length = 36)
    private String identifier = UUID.randomUUID().toString();

    @Column(name = "balance", precision = 9, scale = 2)
    private BigDecimal balance;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("identifier", identifier)
                .append("balance", balance)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Wallet)) return false;

        Wallet wallet = (Wallet) o;

        return new EqualsBuilder().append(getIdentifier(), wallet.getIdentifier()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getIdentifier()).toHashCode();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
