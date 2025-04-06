package com.crypto.trading.service;

import com.crypto.trading.entity.TradeHistory;
import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.TradeHistoryRespository;
import com.crypto.trading.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private TradeHistoryRespository tradeHistoryRespository;

    public UserService(UserRepository userRepository, TradeHistoryRespository tradeHistoryRespository) {
        this.userRepository = userRepository;
        this.tradeHistoryRespository = tradeHistoryRespository;
    }

    @Transactional
    public User createUser(User user) {
        for (Wallet wallet : user.getWallets()) {
            wallet.setUser(user);
        }
        return userRepository.save(user);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(existingUser -> {
            existingUser.setWallets(updatedUser.getWallets());
            return userRepository.save(existingUser);
        });
    }

    public List<TradeHistory> getUserTradingHistory(long id) {
        return tradeHistoryRespository.findByUserId(id);
    }



    @Transactional
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }


}
