package com.lorenzo.pelone.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel {
    UUID id;
    String name;
    String email;
    String lastName;
    String address;
}
