package com.example.account.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
