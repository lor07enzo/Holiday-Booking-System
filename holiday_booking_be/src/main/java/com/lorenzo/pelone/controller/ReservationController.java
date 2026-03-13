package com.lorenzo.pelone.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lorenzo.pelone.dto.CreateReservationRequest;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.service.ReservationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/reservations")
    public List<ReservationModel> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/reservations/last-month")
    public List<ReservationModel> getReservationsLastMonth() {
        return reservationService.getReservationsLastMonth();
    }

    @GetMapping("/reservations/statistics")
    public Map<String, Object> getDashboardStats() {
        return reservationService.getDashboardStats();
    }

    @GetMapping("/users/{userId}/reservations")
    public ReservationModel getLastReservationByUser(@PathVariable Integer userId) {
        return reservationService.lastReservationByUser(userId);
    }

    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationModel createReservation(@Valid @RequestBody CreateReservationRequest requestDTO) {
        return reservationService.createReservation(
            requestDTO.getHabitationId(),
            requestDTO.getUserId(),
            requestDTO.getStartDate(),
            requestDTO.getEndDate()
        );
    }
    
}