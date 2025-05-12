package com.shashank.balancesservice.controller;

import com.shashank.balancesservice.model.AccountBalance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;

@RestController
@RequestMapping("/api/balances")
public class BalanceController {

    private final Random random = new Random();

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountBalance> getAccountBalance(@PathVariable String accountNumber) {
        // Generate a random balance greater than $10,000
        BigDecimal availableBalance = generateRandomBalanceAbove10000();
        
        // Current balance is slightly higher than available balance
        BigDecimal currentBalance = availableBalance.add(
                new BigDecimal(random.nextInt(1000)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
        );
        
        AccountBalance balance = AccountBalance.builder()
                .accountNumber(accountNumber)
                .availableBalance(availableBalance)
                .currentBalance(currentBalance)
                .currency("USD")
                .lastUpdated(LocalDateTime.now())
                .build();
        
        return ResponseEntity.ok(balance);
    }
    
    private BigDecimal generateRandomBalanceAbove10000() {
        // Generate a random balance between $10,000 and $100,000
        double randomValue = 10000 + (random.nextDouble() * 90000);
        
        // Format to 2 decimal places
        return new BigDecimal(randomValue).setScale(2, RoundingMode.HALF_UP);
    }
}
