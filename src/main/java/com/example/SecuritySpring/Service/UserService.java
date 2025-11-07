package com.example.SecuritySpring.Service;

import com.example.SecuritySpring.Entity.UserEntity;
import com.example.SecuritySpring.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserEntity registerUser(String username, String password, String role) {
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);

        return userRepository.save(user);
    }
}
