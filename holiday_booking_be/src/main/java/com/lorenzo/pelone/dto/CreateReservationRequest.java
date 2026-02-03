package com.lorenzo.pelone.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReservationRequest {
    private int habitationId;
    private int userId;
    private LocalDate startDate;
    private LocalDate endDate;
}
