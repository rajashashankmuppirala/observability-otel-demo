package com.shashank.balancesservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalance {
    private String accountNumber;
    private BigDecimal availableBalance;
    private BigDecimal currentBalance;
    private String currency;
    private LocalDateTime lastUpdated;
}
