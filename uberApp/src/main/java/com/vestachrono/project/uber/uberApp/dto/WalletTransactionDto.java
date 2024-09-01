package com.vestachrono.project.uber.uberApp.dto;

import com.vestachrono.project.uber.uberApp.entities.Ride;
import com.vestachrono.project.uber.uberApp.entities.Wallet;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionMethod;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletTransactionDto {

    private Long id;

    private Double amount;

    private TransactionType transactionType;

    private TransactionMethod transactionMethod;

    private Ride ride;

    private String transactionId;

    private WalletDto wallet;

    private LocalDateTime timeStamp;

}
