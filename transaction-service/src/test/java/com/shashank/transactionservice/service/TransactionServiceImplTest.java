package com.shashank.transactionservice.service;

import com.shashank.transactionservice.dto.TransactionRequest;
import com.shashank.transactionservice.dto.TransactionResponse;
import com.shashank.transactionservice.exception.InsufficientFundsException;
import com.shashank.transactionservice.exception.TransactionNotFoundException;
import com.shashank.transactionservice.model.AccountBalance;
import com.shashank.transactionservice.model.TransactionStatus;
import com.shashank.transactionservice.model.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Captor
    private ArgumentCaptor<String> urlCaptor;

    private final String ACCOUNT_ID = "12345678";
    private final String BALANCE_SERVICE_URL = "http://localhost:8081/api/balances";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(transactionService, "balancesServiceUrl", BALANCE_SERVICE_URL);
    }

    @Test
    void createTransaction_PaymentWithSufficientFunds_ReturnsCompletedTransaction() {
        // Arrange
        BigDecimal transactionAmount = new BigDecimal("500.00");
        BigDecimal availableBalance = new BigDecimal("1000.00");
        
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, transactionAmount, TransactionType.PAYMENT);
        AccountBalance mockBalance = createAccountBalance(ACCOUNT_ID, availableBalance);
        
        when(restTemplate.getForObject(anyString(), eq(AccountBalance.class))).thenReturn(mockBalance);

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);
        assertEquals(ACCOUNT_ID, response.getAccountId());
        assertEquals(transactionAmount, response.getAmount());
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.PAYMENT, response.getType());
        
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(AccountBalance.class));
        assertEquals(BALANCE_SERVICE_URL + "/" + ACCOUNT_ID, urlCaptor.getValue());
    }

    @Test
    void createTransaction_WithdrawalWithSufficientFunds_ReturnsCompletedTransaction() {
        // Arrange
        BigDecimal transactionAmount = new BigDecimal("800.00");
        BigDecimal availableBalance = new BigDecimal("1000.00");
        
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, transactionAmount, TransactionType.WITHDRAWAL);
        AccountBalance mockBalance = createAccountBalance(ACCOUNT_ID, availableBalance);
        
        when(restTemplate.getForObject(anyString(), eq(AccountBalance.class))).thenReturn(mockBalance);

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);
        assertEquals(ACCOUNT_ID, response.getAccountId());
        assertEquals(transactionAmount, response.getAmount());
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.WITHDRAWAL, response.getType());
    }

    @Test
    void createTransaction_PaymentWithInsufficientFunds_ThrowsException() {
        // Arrange
        BigDecimal transactionAmount = new BigDecimal("1500.00");
        BigDecimal availableBalance = new BigDecimal("1000.00");
        
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, transactionAmount, TransactionType.PAYMENT);
        AccountBalance mockBalance = createAccountBalance(ACCOUNT_ID, availableBalance);
        
        when(restTemplate.getForObject(anyString(), eq(AccountBalance.class))).thenReturn(mockBalance);

        // Act & Assert
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class, () -> {
            transactionService.createTransaction(request);
        });
        
        assertTrue(exception.getMessage().contains("Insufficient funds for transaction"));
        assertTrue(exception.getMessage().contains("Available balance: 1000.00"));
        assertTrue(exception.getMessage().contains("Transaction amount: 1500.00"));
    }

    @Test
    void createTransaction_Deposit_DoesNotCheckBalance() {
        // Arrange
        BigDecimal transactionAmount = new BigDecimal("500.00");
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, transactionAmount, TransactionType.DEPOSIT);

        // No mock setup for restTemplate - it should not be called for deposits

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);
        assertEquals(ACCOUNT_ID, response.getAccountId());
        assertEquals(transactionAmount, response.getAmount());
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.DEPOSIT, response.getType());
    }

    @Test
    void createTransaction_Refund_DoesNotCheckBalance() {
        // Arrange
        BigDecimal transactionAmount = new BigDecimal("300.00");
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, transactionAmount, TransactionType.REFUND);

        // Act
        TransactionResponse response = transactionService.createTransaction(request);

        // Assert
        assertNotNull(response);
        assertEquals(ACCOUNT_ID, response.getAccountId());
        assertEquals(transactionAmount, response.getAmount());
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(TransactionType.REFUND, response.getType());
    }

    @Test
    void getTransactionById_ExistingTransaction_ReturnsTransaction() {
        // Arrange
        // First create a transaction
        BigDecimal amount = new BigDecimal("500.00");
        TransactionRequest request = createTransactionRequest(ACCOUNT_ID, amount, TransactionType.DEPOSIT);
        TransactionResponse createdTransaction = transactionService.createTransaction(request);
        
        // Act
        TransactionResponse response = transactionService.getTransactionById(createdTransaction.getId());
        
        // Assert
        assertNotNull(response);
        assertEquals(createdTransaction.getId(), response.getId());
        assertEquals(ACCOUNT_ID, response.getAccountId());
        assertEquals(amount, response.getAmount());
    }

    @Test
    void getTransactionById_NonExistingTransaction_ThrowsException() {
        // Arrange
        String nonExistingId = "non-existing-id";
        
        // Act & Assert
        TransactionNotFoundException exception = assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.getTransactionById(nonExistingId);
        });
        
        assertEquals("Transaction not found with id: " + nonExistingId, exception.getMessage());
    }

    @Test
    void getAllTransactions_ReturnsAllTransactions() {
        // Arrange
        // Create several transactions
        transactionService.createTransaction(
                createTransactionRequest(ACCOUNT_ID, new BigDecimal("100.00"), TransactionType.DEPOSIT));
        transactionService.createTransaction(
                createTransactionRequest(ACCOUNT_ID, new BigDecimal("50.00"), TransactionType.PAYMENT));
        transactionService.createTransaction(
                createTransactionRequest("87654321", new BigDecimal("200.00"), TransactionType.WITHDRAWAL));
        
        // Act
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        
        // Assert
        assertNotNull(transactions);
        assertEquals(3, transactions.size());
    }

    @Test
    void getTransactionsByAccountId_ReturnsMatchingTransactions() {
        // Arrange
        // Create several transactions for different accounts
        transactionService.createTransaction(
                createTransactionRequest(ACCOUNT_ID, new BigDecimal("100.00"), TransactionType.DEPOSIT));
        transactionService.createTransaction(
                createTransactionRequest(ACCOUNT_ID, new BigDecimal("50.00"), TransactionType.PAYMENT));
        transactionService.createTransaction(
                createTransactionRequest("87654321", new BigDecimal("200.00"), TransactionType.WITHDRAWAL));
        
        // Act
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(ACCOUNT_ID);
        
        // Assert
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        transactions.forEach(t -> assertEquals(ACCOUNT_ID, t.getAccountId()));
    }

    @Test
    void getTransactionsByAccountId_NoMatchingTransactions_ReturnsEmptyList() {
        // Arrange
        // Create transactions for a different account
        transactionService.createTransaction(
                createTransactionRequest("87654321", new BigDecimal("200.00"), TransactionType.WITHDRAWAL));
        
        // Act
        List<TransactionResponse> transactions = transactionService.getTransactionsByAccountId(ACCOUNT_ID);
        
        // Assert
        assertNotNull(transactions);
        assertTrue(transactions.isEmpty());
    }

    private TransactionRequest createTransactionRequest(String accountId, BigDecimal amount, TransactionType type) {
        return TransactionRequest.builder()
                .accountId(accountId)
                .amount(amount)
                .currency("USD")
                .description("Test transaction")
                .type(type)
                .build();
    }

    private AccountBalance createAccountBalance(String accountNumber, BigDecimal availableBalance) {
        BigDecimal currentBalance = availableBalance.add(new BigDecimal("10.00"));
        return AccountBalance.builder()
                .accountNumber(accountNumber)
                .availableBalance(availableBalance)
                .currentBalance(currentBalance)
                .currency("USD")
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}
