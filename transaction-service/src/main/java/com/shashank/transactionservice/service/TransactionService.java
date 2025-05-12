package com.shashank.transactionservice.service;

import com.shashank.transactionservice.dto.TransactionRequest;
import com.shashank.transactionservice.dto.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
    TransactionResponse getTransactionById(String id);
    List<TransactionResponse> getAllTransactions();
    List<TransactionResponse> getTransactionsByAccountId(String accountId);
}
