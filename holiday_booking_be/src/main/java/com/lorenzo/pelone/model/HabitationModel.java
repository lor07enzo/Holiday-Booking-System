package com.lorenzo.pelone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "habitations")
public class HabitationModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne 
    @JoinColumn(name = "host_code", referencedColumnName = "host_code", nullable = false)
    private HostModel host;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @Min(value = 1, message = "Floor must be at least 1")
    private int floor;

    @NotBlank(message = "Address is required")
    private String address;
    
    @Min(value = 1, message = "Price must be at least 1")
    private double price;

    @Min(value = 1, message = "Rooms must be at least 1")
    private int rooms;

    @NotNull(message = "Start date is required")
    @Column(name = "start_available", nullable = false)
    private LocalDate startAvailable;

    @NotNull(message = "End date is required")
    @Column(name = "end_available", nullable = false)
    private LocalDate endAvailable;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
