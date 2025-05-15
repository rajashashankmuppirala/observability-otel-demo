package com.shashank.transactionservice.model;

import com.shashank.transactionservice.dto.TransactionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRequestTest {

    @Test
    void testBuilderAndGetters() {
        // Arrange
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;

        // Act
        TransactionRequest request = TransactionRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .currency(currency)
                .description(description)
                .type(type)
                .build();

        // Assert
        assertEquals(accountId, request.getAccountId());
        assertEquals(amount, request.getAmount());
        assertEquals(currency, request.getCurrency());
        assertEquals(description, request.getDescription());
        assertEquals(type, request.getType());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        TransactionRequest request = new TransactionRequest();

        // Assert
        assertNull(request.getAccountId());
        assertNull(request.getAmount());
        assertNull(request.getCurrency());
        assertNull(request.getDescription());
        assertNull(request.getType());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;

        // Act
        TransactionRequest request = new TransactionRequest(accountId, amount, currency, description, type);

        // Assert
        assertEquals(accountId, request.getAccountId());
        assertEquals(amount, request.getAmount());
        assertEquals(currency, request.getCurrency());
        assertEquals(description, request.getDescription());
        assertEquals(type, request.getType());
    }

    @Test
    void testSetters() {
        // Arrange
        TransactionRequest request = new TransactionRequest();
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;

        // Act
        request.setAccountId(accountId);
        request.setAmount(amount);
        request.setCurrency(currency);
        request.setDescription(description);
        request.setType(type);

        // Assert
        assertEquals(accountId, request.getAccountId());
        assertEquals(amount, request.getAmount());
        assertEquals(currency, request.getCurrency());
        assertEquals(description, request.getDescription());
        assertEquals(type, request.getType());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        TransactionRequest request1 = TransactionRequest.builder()
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .build();

        TransactionRequest request2 = TransactionRequest.builder()
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .build();

        TransactionRequest request3 = TransactionRequest.builder()
                .accountId("87654321")
                .amount(new BigDecimal("300.00"))
                .currency("EUR")
                .description("Different transaction")
                .type(TransactionType.WITHDRAWAL)
                .build();

        // Assert
        assertEquals(request1, request2);
        assertNotEquals(request1, request3);
        assertEquals(request1.hashCode(), request2.hashCode());
        assertNotEquals(request1.hashCode(), request3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        TransactionRequest request = TransactionRequest.builder()
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .build();

        // Act
        String toString = request.toString();

        // Assert
        assertTrue(toString.contains("12345678"));
        assertTrue(toString.contains("500.00"));
        assertTrue(toString.contains("USD"));
        assertTrue(toString.contains("Test transaction"));
        assertTrue(toString.contains("PAYMENT"));
    }
}
