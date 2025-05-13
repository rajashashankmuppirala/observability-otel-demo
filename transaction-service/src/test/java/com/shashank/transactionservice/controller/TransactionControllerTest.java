package com.shashank.transactionservice.controller;

import com.shashank.transactionservice.dto.TransactionRequest;
import com.shashank.transactionservice.dto.TransactionResponse;
import com.shashank.transactionservice.exception.InsufficientFundsException;
import com.shashank.transactionservice.exception.TransactionNotFoundException;

import com.shashank.transactionservice.model.TransactionStatus;
import com.shashank.transactionservice.model.TransactionType;
import com.shashank.transactionservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionsController transactionController;

    private TransactionRequest validRequest;
    private TransactionResponse sampleResponse;

    @BeforeEach
    void setUp() {
        validRequest = TransactionRequest.builder()
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .build();

        sampleResponse = TransactionResponse.builder()
                .id("transaction-123")
                .accountId("12345678")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .description("Test transaction")
                .type(TransactionType.PAYMENT)
                .status(TransactionStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createTransaction_ValidRequest_ReturnsCreatedResponse() {
        // Arrange
        when(transactionService.createTransaction(any(TransactionRequest.class))).thenReturn(sampleResponse);

        // Act
        ResponseEntity<TransactionResponse> response = transactionController.createTransaction(validRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleResponse, response.getBody());
    }

    @Test
    void createTransaction_InsufficientFunds_ReturnsBadRequest() {
        // Arrange
        String errorMessage = "Insufficient funds for transaction";
        when(transactionService.createTransaction(any(TransactionRequest.class)))
                .thenThrow(new InsufficientFundsException(errorMessage));

        // Act & Assert
        Exception exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionController.createTransaction(validRequest);
        });
        
        assertTrue(exception.getMessage().contains(errorMessage));
    }

    @Test
    void getTransactionById_ExistingId_ReturnsTransaction() {
        // Arrange
        String transactionId = "transaction-123";
        when(transactionService.getTransactionById(transactionId)).thenReturn(sampleResponse);

        // Act
        ResponseEntity<TransactionResponse> response = transactionController.getTransactionById(transactionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleResponse, response.getBody());
    }

    @Test
    void getTransactionById_NonExistingId_ReturnsNotFound() {
        // Arrange
        String nonExistingId = "non-existing-id";
        when(transactionService.getTransactionById(nonExistingId))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        // Act & Assert
        Exception exception = assertThrows(TransactionNotFoundException.class, () -> {
            transactionController.getTransactionById(nonExistingId);
        });
        
        assertTrue(exception.getMessage().contains("Transaction not found"));
    }

    @Test
    void getAllTransactions_ReturnsAllTransactions() {
        // Arrange
        List<TransactionResponse> transactions = Arrays.asList(
                sampleResponse,
                TransactionResponse.builder()
                        .id("transaction-456")
                        .accountId("87654321")
                        .amount(new BigDecimal("300.00"))
                        .currency("EUR")
                        .description("Another transaction")
                        .type(TransactionType.WITHDRAWAL)
                        .status(TransactionStatus.COMPLETED)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getAllTransactions();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void getAllTransactions_NoTransactions_ReturnsEmptyList() {
        // Arrange
        when(transactionService.getAllTransactions()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getAllTransactions();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getTransactionsByAccountId_ExistingAccount_ReturnsTransactions() {
        // Arrange
        String accountId = "12345678";
        List<TransactionResponse> transactions = Collections.singletonList(sampleResponse);
        
        when(transactionService.getTransactionsByAccountId(accountId)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactionsByAccountId(accountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(transactions, response.getBody());
    }

    @Test
    void getTransactionsByAccountId_NoTransactions_ReturnsEmptyList() {
        // Arrange
        String accountId = "no-transactions-account";
        when(transactionService.getTransactionsByAccountId(accountId)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactionsByAccountId(accountId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}
