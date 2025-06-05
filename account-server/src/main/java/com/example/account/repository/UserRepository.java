package com.example.account.repository;

import com.example.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);  // 전화번호로 사용자 찾기

}