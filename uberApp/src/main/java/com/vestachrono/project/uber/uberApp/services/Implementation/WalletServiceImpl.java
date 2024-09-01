package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.entities.Ride;
import com.vestachrono.project.uber.uberApp.entities.User;
import com.vestachrono.project.uber.uberApp.entities.Wallet;
import com.vestachrono.project.uber.uberApp.entities.WalletTransaction;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionMethod;
import com.vestachrono.project.uber.uberApp.entities.enums.TransactionType;
import com.vestachrono.project.uber.uberApp.exceptions.ResourceNotFoundException;
import com.vestachrono.project.uber.uberApp.repositories.WalletRepository;
import com.vestachrono.project.uber.uberApp.services.WalletService;
import com.vestachrono.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionService walletTransactionService;

    @Override
    @Transactional
    public Wallet addMoneyToWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
//        get the wallet assigned to user
        Wallet wallet = findByUser(user);
//        set the balance by adding the amount
        wallet.setBalance(wallet.getBalance() + amount);

//        Create a Transaction object
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .transactionMethod(transactionMethod)
                .build();

        walletTransactionService.createNewWalletTransaction(walletTransaction);


//        save the wallet transaction
        return walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public Wallet deductMoneyFromWallet(User user, Double amount, String transactionId, Ride ride, TransactionMethod transactionMethod) {
//        get the wallet assigned to the user
        Wallet wallet = findByUser(user);
//        set the balance by deducting the amount from drivers wallet
        wallet.setBalance(wallet.getBalance() - amount);

//        Create a Transactional object
        WalletTransaction walletTransaction = WalletTransaction.builder()
                .transactionId(transactionId)
                .ride(ride)
                .wallet(wallet)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .transactionMethod(transactionMethod)
                .build();

//        walletTransactionService.createNewWalletTransaction(walletTransaction);

        wallet.getTransactions().add(walletTransaction);

//        save the wallet transaction
        return walletRepository.save(wallet);
    }

    @Override
    public void withdrawAllMyMoneyFromWallet() {

    }

    @Override
    public Wallet findWalletById(Long walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with Id: " + walletId));
    }

    @Override
    public Wallet createNewWallet(User user) {
//        Create a new wallet for the user
        Wallet wallet = new Wallet();
//        set the wallet to new user
        wallet.setUser(user);
//        save the new wallet in walletRepository
        return walletRepository.save(wallet);

    }

    @Override
    public Wallet findByUser(User user) {
//        get the wallet assigned to the user
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Id: "+ user.getId()));
    }
}
