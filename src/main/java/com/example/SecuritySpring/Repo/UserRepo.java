package com.example.SecuritySpring.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.SecuritySpring.Entity.UserEntity;

public interface UserRepo extends JpaRepository<UserEntity,Long> {
	 Optional<UserEntity> findByUsername(String username);

}
