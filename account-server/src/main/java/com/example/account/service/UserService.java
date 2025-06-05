package com.example.account.service;


import com.example.account.model.User;
import com.example.account.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        user.setUsername(user.getFirstName() + user.getLastName());
        return userRepository.save(user);
    }

    // 전화번호를 기준으로 사용자 프로필 업데이트
    public User updateUserProfile(String phoneNumber, User updatedUser) {
        // 전화번호를 기준으로 사용자 조회
        User user = userRepository.findByPhoneNumber(phoneNumber);

        if (user != null) {
            // 기존 정보 업데이트
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            return userRepository.save(user);  // 업데이트된 사용자 저장
        } else {
            throw new IllegalArgumentException("User not found with phone number: " + phoneNumber);
        }
    }

    public User getUserProfileByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);  // 전화번호로 사용자 조회
    }
    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
