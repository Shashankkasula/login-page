package com.example.SecuritySpring.Repo;

import com.example.SecuritySpring.Entity.LoginAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginAttemptRepo extends JpaRepository<LoginAttemptEntity, Long> {
    List<LoginAttemptEntity> findByUsernameOrderByTimestampDesc(String username);
}
