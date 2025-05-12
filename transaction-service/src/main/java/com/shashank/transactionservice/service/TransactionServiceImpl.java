package com.shashank.transactionservice.service;

import com.shashank.transactionservice.dto.TransactionRequest;
import com.shashank.transactionservice.dto.TransactionResponse;
import com.shashank.transactionservice.exception.InsufficientFundsException;
import com.shashank.transactionservice.exception.TransactionNotFoundException;
import com.shashank.transactionservice.model.AccountBalance;
import com.shashank.transactionservice.model.Transaction;
import com.shashank.transactionservice.model.TransactionStatus;
import com.shashank.transactionservice.model.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {
    
    // In-memory storage for transactions (replace with repository in production)
    private final Map<String, Transaction> transactionStore = new ConcurrentHashMap<>();
    
    private final RestTemplate restTemplate;
    
    @Value("${balances.service.url:http://localhost:8081/api/balances}")
    private String balancesServiceUrl;
    
    @Autowired
    public TransactionServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        // Only check balance for payment or withdrawal transactions
        if (request.getType() == TransactionType.PAYMENT || 
            request.getType() == TransactionType.WITHDRAWAL) {
            
            // Check if account has sufficient balance
            AccountBalance accountBalance = checkAccountBalance(request.getAccountId());
            
            // Verify sufficient funds
            if (accountBalance.getAvailableBalance().compareTo(request.getAmount()) < 0) {
                // Create a failed transaction due to insufficient funds
                Transaction failedTransaction = Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .accountId(request.getAccountId())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .description(request.getDescription())
                        .type(request.getType())
                        .status(TransactionStatus.FAILED)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                
                transactionStore.put(failedTransaction.getId(), failedTransaction);
                
                // Throw exception with the failed transaction ID
                throw new InsufficientFundsException(
                        "Insufficient funds for transaction. Available balance: " + 
                        accountBalance.getAvailableBalance() + " " + accountBalance.getCurrency() + 
                        ", Transaction amount: " + request.getAmount() + " " + request.getCurrency() +
                        ", Transaction ID: " + failedTransaction.getId());
            }
        }
        
        // Process the transaction if balance is sufficient or if it's a deposit/refund
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID().toString())
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .type(request.getType())
                .status(TransactionStatus.COMPLETED) // Mark as completed if we get here
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        transactionStore.put(transaction.getId(), transaction);
        
        return mapToTransactionResponse(transaction);
    }
    
    private AccountBalance checkAccountBalance(String accountId) {
        String balanceUrl = balancesServiceUrl + "/" + accountId;
        return restTemplate.getForObject(balanceUrl, AccountBalance.class);
    }

    @Override
    public TransactionResponse getTransactionById(String id) {
        Transaction transaction = transactionStore.get(id);
        if (transaction == null) {
            throw new TransactionNotFoundException("Transaction not found with id: " + id);
        }
        return mapToTransactionResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions() {
        return transactionStore.values().stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getTransactionsByAccountId(String accountId) {
        return transactionStore.values().stream()
                .filter(transaction -> transaction.getAccountId().equals(accountId))
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }


    
    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .description(transaction.getDescription())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
