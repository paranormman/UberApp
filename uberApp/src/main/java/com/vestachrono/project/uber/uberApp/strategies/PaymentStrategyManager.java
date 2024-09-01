package com.vestachrono.project.uber.uberApp.strategies;

import com.vestachrono.project.uber.uberApp.entities.enums.PaymentMethod;
import com.vestachrono.project.uber.uberApp.strategies.implementation.CashPaymentStrategy;
import com.vestachrono.project.uber.uberApp.strategies.implementation.WalletPaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStrategyManager {

    private final CashPaymentStrategy cashPaymentStrategy;
    private final WalletPaymentStrategy walletPaymentStrategy;

    public PaymentStrategy paymentStrategy(PaymentMethod paymentMethod) {
        return switch (paymentMethod) {
            case WALLET -> walletPaymentStrategy;
            case CASH -> cashPaymentStrategy;
        };
    }

}
