package com.crypto.trading.controller;

import com.crypto.trading.entity.TradeHistory;
import com.crypto.trading.entity.User;
import com.crypto.trading.entity.Wallet;
import com.crypto.trading.repository.TradeHistoryRespository;
import com.crypto.trading.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, TradeHistoryRespository tradeHistoryRespository) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User newCreatedUser = userService.createUser(user);
        URI location = URI.create("/api/users/" + newCreatedUser.getID());
        return ResponseEntity.created(location).body(newCreatedUser);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build(); // or return 200 with empty list
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/wallets")
    public ResponseEntity<?> createUserWallet(@RequestHeader("X-USER-ID") long userId) {
        Optional<User> userOptional = userService.getUserById(userId);
        List<Wallet> wallets = null;
        if(userOptional.isPresent()) {
            wallets = userOptional.get().getWallets();
            return ResponseEntity.ok(wallets);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/tradehistory")
    public ResponseEntity<?> getUserTradeHistory(@RequestHeader("X-USER-ID") long userId) {
        List<TradeHistory> userTradingHistory = userService.getUserTradingHistory(userId);

        if(userTradingHistory == null ) {

        } else {

        }

        return ResponseEntity.ok(userTradingHistory);
    }

}
