package com.example.account.controller;

import com.example.account.model.User;
import com.example.account.repository.UserRepository;
import com.example.account.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null || !user.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }

        return ResponseEntity.ok(user);
    }

    // 전화번호로 사용자 프로필 조회
    @GetMapping("/profile/{phoneNumber}")
    public ResponseEntity<User> getUserProfileByPhoneNumber(@PathVariable String phoneNumber) {
        User user = userService.getUserProfileByPhoneNumber(phoneNumber);
        if (user != null) {
            return ResponseEntity.ok(user);  // 사용자 정보 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 사용자 미발견 시 404 응답
        }
    }

    // 전화번호로 사용자 프로필 수정
    @PutMapping("/profile/{phoneNumber}")
    public ResponseEntity<User> updateProfileByPhoneNumber(@PathVariable String phoneNumber, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUserProfile(phoneNumber, updatedUser);
            return ResponseEntity.ok(user);  // 수정된 사용자 정보 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // 사용자가 존재하지 않으면 404 반환
        }
    }
}
