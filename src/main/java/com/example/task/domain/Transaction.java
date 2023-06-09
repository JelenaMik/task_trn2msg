package com.example.task.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class Transaction {
    private String type;
    private String pan;
    private Double amount;
    private LocalDateTime transactionTime;
    private String currency;
}
