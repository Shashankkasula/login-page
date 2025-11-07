package com.example.SecuritySpring.Service;

import com.example.SecuritySpring.Entity.LoginAttemptEntity;
import com.example.SecuritySpring.Repo.LoginAttemptRepo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginAuditService {

    @Autowired
    private LoginAttemptRepo loginAttemptRepo;

    @Autowired
    private HttpServletRequest request;

    public void log(String username, String status, String message) {
        String ip = getClientIpAddress();
        LoginAttemptEntity log = new LoginAttemptEntity();
        log.setUsername(username);
        log.setStatus(status);
        log.setMessage(message);
        log.setIpAddress(ip);
        log.setTimestamp(new Date());

        loginAttemptRepo.save(log);
    }

    private String getClientIpAddress() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
