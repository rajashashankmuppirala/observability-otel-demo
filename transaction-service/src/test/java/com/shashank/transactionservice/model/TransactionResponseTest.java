package com.shashank.transactionservice.model;

import com.shashank.transactionservice.dto.TransactionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionResponseTest {

    @Test
    void testBuilderAndGetters() {
        // Arrange
        String id = "transaction-123";
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;
        TransactionStatus status = TransactionStatus.COMPLETED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        TransactionResponse response = TransactionResponse.builder()
                .id(id)
                .accountId(accountId)
                .amount(amount)
                .currency(currency)
                .description(description)
                .type(type)
                .status(status)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        // Assert
        assertEquals(id, response.getId());
        assertEquals(accountId, response.getAccountId());
        assertEquals(amount, response.getAmount());
        assertEquals(currency, response.getCurrency());
        assertEquals(description, response.getDescription());
        assertEquals(type, response.getType());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        TransactionResponse response = new TransactionResponse();

        // Assert
        assertNull(response.getId());
        assertNull(response.getAccountId());
        assertNull(response.getAmount());
        assertNull(response.getCurrency());
        assertNull(response.getDescription());
        assertNull(response.getType());
        assertNull(response.getStatus());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String id = "transaction-123";
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;
        TransactionStatus status = TransactionStatus.COMPLETED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        TransactionResponse response = new TransactionResponse(
                id, accountId, amount, currency, description, type, status, createdAt, updatedAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(accountId, response.getAccountId());
        assertEquals(amount, response.getAmount());
        assertEquals(currency, response.getCurrency());
        assertEquals(description, response.getDescription());
        assertEquals(type, response.getType());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void testSetters() {
        // Arrange
        TransactionResponse response = new TransactionResponse();
        String id = "transaction-123";
        String accountId = "12345678";
        BigDecimal amount = new BigDecimal("500.00");
        String currency = "USD";
        String description = "Test transaction";
        TransactionType type = TransactionType.PAYMENT;
        TransactionStatus status = TransactionStatus.COMPLETED;
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();

        // Act
        response.setId(id);
        response.setAccountId(accountId);
        response.setAmount(amount);
        response.setCurrency(currency);
        response.setDescription(description);
        response.setType(type);
        response.setStatus(status);
        response.setCreatedAt(createdAt);
        response.setUpdatedAt(updatedAt);

        // Assert
        assertEquals(id, response.getId());
        assertEquals(accountId, response.getAccountId());
        assertEquals(amount, response.getAmount());
        assertEquals(currency, response.getCurrency());
        assertEquals(description, response.getDescription());
        assertEquals(type, response.getType());
        assertEquals(status, response.getStatus());
        assertEquals(createdAt, response.getCreatedAt());
        assertEquals(updatedAt, response.getUpdatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        TransactionResponse response1 = TransactionResponse.builder()
                .id("transaction-123")
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        TransactionResponse response2 = TransactionResponse.builder()
                .id("transaction-123")
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        TransactionResponse response3 = TransactionResponse.builder()
                .id("transaction-456")
                .accountId("87654321")
                .amount(new BigDecimal("300.00"))
                .currency("EUR")
                .description("Different transaction")
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.FAILED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(response1, response2);
        assertNotEquals(response1, response3);
        assertEquals(response1.hashCode(), response2.hashCode());
        assertNotEquals(response1.hashCode(), response3.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        
        TransactionResponse response = TransactionResponse.builder()
                .id("transaction-123")
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        String toString = response.toString();

        // Assert
        assertTrue(toString.contains("transaction-123"));
        assertTrue(toString.contains("12345678"));
        assertTrue(toString.contains("500.00"));
        assertTrue(toString.contains("USD"));
        assertTrue(toString.contains("Test transaction"));
        assertTrue(toString.contains("PAYMENT"));
        assertTrue(toString.contains("COMPLETED"));
    }
}
