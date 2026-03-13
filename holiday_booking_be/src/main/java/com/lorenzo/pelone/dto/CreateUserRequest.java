package com.lorenzo.pelone.dto;

import com.lorenzo.pelone.model.UserModel;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    @NotNull(message = "Username is required")
    @Valid
    private UserModel user;
    private boolean host;
}