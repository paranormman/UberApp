package com.vestachrono.project.uber.uberApp.strategies.implementation;

import com.vestachrono.project.uber.uberApp.entities.Driver;
import com.vestachrono.project.uber.uberApp.entities.Payment;
import com.vestachrono.project.uber.uberApp.entities.Rider;
import com.vestachrono.project.uber.uberApp.entities.Wallet;
import com.vestachrono.project.uber.uberApp.entities.enums.PaymentStatus;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionMethod;
import com.vestachrono.project.uber.uberApp.repositories.PaymentRepository;
import com.vestachrono.project.uber.uberApp.services.PaymentService;
import com.vestachrono.project.uber.uberApp.services.WalletService;
import com.vestachrono.project.uber.uberApp.strategies.PaymentStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
Rider has 232 and Driver has 500
Ride cost is 100, commission is 30
Rider -> 232-100 = 132
Driver -> 500 - (100 - 30) = 570
* */

@Service
@RequiredArgsConstructor
public class WalletPaymentStrategy implements PaymentStrategy {

    private final WalletService walletService;
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public void processPayment(Payment payment) {
//        get Driver from ride
        Driver driver = payment.getRide().getDriver();
//        get Rider from ride
        Rider rider = payment.getRide().getRider();

//        debit money from riders wallet
        walletService.deductMoneyFromWallet(rider.getUser(),
                payment.getAmount(), null, payment.getRide(), TransactionMethod.RIDE);

//        calculating drivers cut
        double driversCut = payment.getAmount() * (1 - PLATFORM_COMMISSION);

//        credit amount to the driver
        walletService.addMoneyToWallet(driver.getUser(),
                driversCut, null, payment.getRide(), TransactionMethod.RIDE);

//        update payment status to confirmed once the payment is completed
        payment.setPaymentStatus(PaymentStatus.CONFIRMED);
        paymentRepository.save(payment);

    }
}
