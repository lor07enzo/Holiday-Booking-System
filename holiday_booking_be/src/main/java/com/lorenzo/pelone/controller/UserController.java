package com.lorenzo.pelone.controller;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lorenzo.pelone.dto.CreateUserRequest;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserModel> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{id}")
    public UserModel getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping("/hosts")
    public List<HostModel> getAllHosts() {
        return userService.getAllHosts();
    }

    @GetMapping("/hosts/{hostCode}")
    public HostModel getHostByCode(@PathVariable Integer hostCode) {
        return userService.getHostByCode(hostCode);
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest requestDTO) {
        Object result = userService.createUser(requestDTO.getUser(), requestDTO.isHost());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
}
