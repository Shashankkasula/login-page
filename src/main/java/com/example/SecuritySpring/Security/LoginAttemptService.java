package com.example.SecuritySpring.Security;

import com.example.SecuritySpring.Entity.UserEntity;
import com.example.SecuritySpring.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 3;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 minutes

    @Autowired
    private UserRepo userRepo;

    public void increaseFailedAttempts(UserEntity user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepo.save(user);
    }

    public void resetFailedAttempts(String username) {
        Optional<UserEntity> userOpt = userRepo.findByUsername(username);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            user.setFailedAttempt(0);
            user.setAccountNonLocked(true);
            userRepo.save(user);
        }
    }

    public void lock(UserEntity user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepo.save(user);
    }

    public boolean unlockWhenTimeExpired(UserEntity user) {
        long lockTimeInMillis = user.getLockTime().getTime();
        long currentTime = System.currentTimeMillis();

        if (lockTimeInMillis + LOCK_TIME_DURATION < currentTime) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepo.save(user);
            return true;
        }
        return false;
    }
}