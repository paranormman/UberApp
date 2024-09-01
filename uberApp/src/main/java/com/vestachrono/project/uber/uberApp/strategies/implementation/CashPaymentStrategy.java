package com.vestachrono.project.uber.uberApp.strategies.implementation;

import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.Payment;
import com.vestachrono.project.uber.uberApp.entities.enums.PaymentStatus;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionMethod;
import com.vestachrono.project.uber.uberApp.repositories.PaymentRepository;
import com.vestachrono.project.uber.uberApp.services.PaymentService;
import com.vestachrono.project.uber.uberApp.services.WalletService;
import com.vestachrono.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/* Rider -> 100
*  Driver -> 70rs and 30rs will be deducted from diver's wallet */

@Service
@RequiredArgsConstructor
public class CashPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(Payment payment) {
//        get the driver details from the payment method
        Driver driver = payment.getRide().getDriver();
//        calculate the amount to be deducted
        double platformCommission = payment.getAmount() + PLATFORM_COMMISSION;
//        deduct amount from drivers wallet
        walletService.deductMoneyFromWallet(driver.getUser(), platformCommission, null,
                payment.getRide(), TransactionMethod.RIDE);

//        update payment status to confirmed once the payment is completed
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);
    }
}
