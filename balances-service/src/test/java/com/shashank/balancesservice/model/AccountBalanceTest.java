package com.shashank.balancesservice.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class AccountBalanceTest {

    @Test
    public void testBuilderAndGetters() {
        // Arrange
        String accountNumber = "12345678";
        BigDecimal availableBalance = new BigDecimal("15000.50");
        BigDecimal currentBalance = new BigDecimal("15200.75");
        String currency = "USD";
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        AccountBalance accountBalance = AccountBalance.builder()
                .accountNumber(accountNumber)
                .availableBalance(availableBalance)
                .currentBalance(currentBalance)
                .currency(currency)
                .lastUpdated(lastUpdated)
                .build();

        // Assert
        assertEquals(accountNumber, accountBalance.getAccountNumber());
        assertEquals(availableBalance, accountBalance.getAvailableBalance());
        assertEquals(currentBalance, accountBalance.getCurrentBalance());
        assertEquals(currency, accountBalance.getCurrency());
        assertEquals(lastUpdated, accountBalance.getLastUpdated());
    }

    @Test
    public void testNoArgsConstructor() {
        // Act
        AccountBalance accountBalance = new AccountBalance();

        // Assert
        assertNull(accountBalance.getAccountNumber());
        assertNull(accountBalance.getAvailableBalance());
        assertNull(accountBalance.getCurrentBalance());
        assertNull(accountBalance.getCurrency());
        assertNull(accountBalance.getLastUpdated());
    }

    @Test
    public void testAllArgsConstructor() {
        // Arrange
        String accountNumber = "12345678";
        BigDecimal availableBalance = new BigDecimal("15000.50");
        BigDecimal currentBalance = new BigDecimal("15200.75");
        String currency = "USD";
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        AccountBalance accountBalance = new AccountBalance(
                accountNumber, availableBalance, currentBalance, currency, lastUpdated);

        // Assert
        assertEquals(accountNumber, accountBalance.getAccountNumber());
        assertEquals(availableBalance, accountBalance.getAvailableBalance());
        assertEquals(currentBalance, accountBalance.getCurrentBalance());
        assertEquals(currency, accountBalance.getCurrency());
        assertEquals(lastUpdated, accountBalance.getLastUpdated());
    }

    @Test
    public void testSetters() {
        // Arrange
        AccountBalance accountBalance = new AccountBalance();
        String accountNumber = "12345678";
        BigDecimal availableBalance = new BigDecimal("15000.50");
        BigDecimal currentBalance = new BigDecimal("15200.75");
        String currency = "USD";
        LocalDateTime lastUpdated = LocalDateTime.now();

        // Act
        accountBalance.setAccountNumber(accountNumber);
        accountBalance.setAvailableBalance(availableBalance);
        accountBalance.setCurrentBalance(currentBalance);
        accountBalance.setCurrency(currency);
        accountBalance.setLastUpdated(lastUpdated);

        // Assert
        assertEquals(accountNumber, accountBalance.getAccountNumber());
        assertEquals(availableBalance, accountBalance.getAvailableBalance());
        assertEquals(currentBalance, accountBalance.getCurrentBalance());
        assertEquals(currency, accountBalance.getCurrency());
        assertEquals(lastUpdated, accountBalance.getLastUpdated());
    }

    @Test
    public void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        AccountBalance balance1 = AccountBalance.builder()
                .accountNumber("12345678")
                .availableBalance(new BigDecimal("15000.50"))
                .currentBalance(new BigDecimal("15200.75"))
                .currency("USD")
                .lastUpdated(now)
                .build();
        
        AccountBalance balance2 = AccountBalance.builder()
                .accountNumber("12345678")
                .availableBalance(new BigDecimal("15000.50"))
                .currentBalance(new BigDecimal("15200.75"))
                .currency("USD")
                .lastUpdated(now)
                .build();
        
        AccountBalance balance3 = AccountBalance.builder()
                .accountNumber("87654321")
                .availableBalance(new BigDecimal("25000.50"))
                .currentBalance(new BigDecimal("25200.75"))
                .currency("EUR")
                .lastUpdated(now)
                .build();

        // Assert
        assertEquals(balance1, balance2);
        assertEquals(balance1.hashCode(), balance2.hashCode());
        
        assertNotEquals(balance1, balance3);
        assertNotEquals(balance1.hashCode(), balance3.hashCode());
    }

    @Test
    public void testToString() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        AccountBalance accountBalance = AccountBalance.builder()
                .accountNumber("12345678")
                .availableBalance(new BigDecimal("15000.50"))
                .currentBalance(new BigDecimal("15200.75"))
                .currency("USD")
                .lastUpdated(now)
                .build();

        // Act
        String toStringResult = accountBalance.toString();

        // Assert
        assertTrue(toStringResult.contains("12345678"));
        assertTrue(toStringResult.contains("15000.50"));
        assertTrue(toStringResult.contains("15200.75"));
        assertTrue(toStringResult.contains("USD"));
    }
}
