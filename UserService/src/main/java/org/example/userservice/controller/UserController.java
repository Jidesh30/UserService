package org.example.userservice.controller;

import org.example.userservice.dtos.LoginRequestDto;
import org.example.userservice.dtos.SignUpRequestDto;
import org.example.userservice.dtos.UserResponseDto;
import org.example.userservice.models.Token;
import org.example.userservice.models.User;
import org.example.userservice.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserResponseDto signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        User user = userService.signUp(signUpRequestDto.getName(),
                signUpRequestDto.getEmail(),
                signUpRequestDto.getPassword());
        return UserResponseDto.fromUser(user);
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto loginRequestDto) {
        return userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
    }
}
