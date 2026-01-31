package com.lorenzo.pelone.dto;

import com.lorenzo.pelone.model.UserModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {
    private UserModel user;
    private boolean isHost;
}