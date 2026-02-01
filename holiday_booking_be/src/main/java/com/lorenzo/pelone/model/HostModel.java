package com.lorenzo.pelone.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class HostModel {
    private UserModel user;
    private int hostCode;
    private boolean superHost;
    private LocalDateTime createdAt;
}
