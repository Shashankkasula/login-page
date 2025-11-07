package com.example.SecuritySpring.Security;


import com.example.SecuritySpring.Repo.UserRepo;
//import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.stereotype.Component;

@Component
//@RequiredArgsConstructor
public class AuthenticationEventListener {

	@Autowired
    private UserRepo userRepository;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedAttempt(0);
            userRepository.save(user);
            System.out.println("✅ LOGIN SUCCESS for user: " + username);
        });
    }

    @EventListener
    public void onFailure(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            int attempts = user.getFailedAttempt() + 1;
            user.setFailedAttempt(attempts);
            if (attempts >= 3) {
                user.setAccountNonLocked(false);
                System.out.println("❌ Account locked for user: " + username);
            }
            userRepository.save(user);
        });
    }

    @EventListener
    public void onLogout(LogoutSuccessEvent event) {
        System.out.println("👋 User logged out: " + event.getAuthentication().getName());
    }
}
