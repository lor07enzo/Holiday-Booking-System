package com.lorenzo.pelone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class HabitationModel {
    private String id;
    private HostModel host;
    private String name;
    private String description;
    private int floor;
    private String address;
    private double price;
    private int rooms;
    private LocalDate startAvailable;
    private LocalDate endAvailable;
    private LocalDateTime createdAt;
}
