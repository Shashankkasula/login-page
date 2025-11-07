package com.example.SecuritySpring.Entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "loginaudit")
public class LoginAttemptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    private String ipAddress;
    private String status;   
    private String message;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public LoginAttemptEntity() {}

    public LoginAttemptEntity(String username, String ipAddress, String status, String message, Date timestamp) {
        this.username = username;
        this.ipAddress = ipAddress;
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
