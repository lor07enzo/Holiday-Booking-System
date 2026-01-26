package com.lorenzo.pelone.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
public class HabitationModel {
    int id;
    HostModel host;
    String name;
    String floor;
    String address;
    double price;
    String numberRooms;
    LocalDate startAvailable;
    LocalDate endAvailable;
}
