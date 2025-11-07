package com.example.SecuritySpring.Controller;

import com.example.SecuritySpring.Entity.LoginAttemptEntity;
import com.example.SecuritySpring.Entity.UserEntity;
import com.example.SecuritySpring.Repo.LoginAttemptRepo;
import com.example.SecuritySpring.Repo.UserRepo;
import com.example.SecuritySpring.Security.JwtUtils;
import com.example.SecuritySpring.Security.LoginAttemptService;
import com.example.SecuritySpring.Security.TokenBlacklistService;
import com.example.SecuritySpring.Service.LoginAuditService;
import com.example.SecuritySpring.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private UserRepo userRepo;
    
    @Autowired
    private LoginAuditService loginAuditService;
    
    @Autowired
    private LoginAttemptRepo loginAttemptRepo;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/register")
    public UserEntity register(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String role) {
        return userService.registerUser(username, password, role);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestParam String username, @RequestParam String password) {
        Map<String, String> response = new HashMap<>();
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if locked
        if (!user.isAccountNonLocked()) {
            if (loginAttemptService.unlockWhenTimeExpired(user)) {
                loginAuditService.log(username, "UNLOCKED", "Account auto-unlocked after lock duration expired");
                response.put("message", "Your account was unlocked. Please try to login again.");
            } else {
                loginAuditService.log(username, "LOCKED", "Login attempt while account locked");
                throw new RuntimeException("Your account is locked. Try again later.");
            }
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            // ✅ Reset attempts on success
            loginAttemptService.resetFailedAttempts(username);
            loginAuditService.log(username, "SUCCESS", "User logged in successfully");

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", auth.getAuthorities().iterator().next().getAuthority());
            String token = jwtUtils.generateToken(username, claims);

            response.put("token", token);
            response.put("message", "Login successful!");
            return response;

        } catch (Exception ex) {
            // ❌ Increase attempt on failure
            loginAttemptService.increaseFailedAttempts(user);
            if (user.getFailedAttempt() >= 3) {
                loginAttemptService.lock(user);
                loginAuditService.log(username, "LOCKED", "User account locked due to 3 failed attempts");
                throw new RuntimeException("Your account has been locked due to 3 failed login attempts. Try again after 15 minutes.");
            } else {
                loginAuditService.log(username, "FAILURE", "Invalid credentials (Attempt " + user.getFailedAttempt() + ")");
                throw new RuntimeException("Invalid username or password. Attempt " + user.getFailedAttempt() + " of 3.");
            }
        }
    }


    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklistToken(token);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful!");
        return response;
    }

    @GetMapping("/status")
    public Map<String, Object> userStatus(@RequestParam String username) {
        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("locked", !user.isAccountNonLocked());
        response.put("failedAttempt", user.getFailedAttempt());
        response.put("lockTime", user.getLockTime());
        return response;
    }


@GetMapping("/login-history")
public List<LoginAttemptEntity> loginHistory(@RequestParam String username) {
    return loginAttemptRepo.findByUsernameOrderByTimestampDesc(username);
}
}
