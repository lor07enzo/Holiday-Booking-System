package com.lorenzo.pelone.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    private int id;
    private String name;
    private String email;
    private String lastName;
    private String address;
    private LocalDateTime createdAt;
}
