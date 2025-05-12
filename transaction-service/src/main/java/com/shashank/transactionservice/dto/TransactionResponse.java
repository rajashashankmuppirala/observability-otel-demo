package com.shashank.transactionservice.dto;

import com.shashank.transactionservice.model.TransactionStatus;
import com.shashank.transactionservice.model.TransactionType;
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
public class TransactionResponse {
    private String id;
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
