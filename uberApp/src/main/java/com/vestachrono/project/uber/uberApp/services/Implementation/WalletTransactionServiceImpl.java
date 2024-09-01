package com.vestachrono.project.uber.uberApp.services.Implementation;

import com.vestachrono.project.uber.uberApp.dto.WalletTransactionDto;
import com.vestachrono.project.uber.uberApp.entities.WalletTransaction;
import com.vestachrono.project.uber.uberApp.repositories.WalletTransactionRepository;
import com.vestachrono.project.uber.uberApp.services.WalletTransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

@Service
@RequiredArgsConstructor
public class WalletTransactionServiceImpl implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public void createNewWalletTransaction(WalletTransaction walletTransaction) {
//        Save the new walletTransaction to repository
        walletTransactionRepository.save(walletTransaction);
    }
}
