package com.lorenzo.pelone.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateHabitationRequest;
import com.lorenzo.pelone.dto.CreateReservationRequest;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.service.ReservationService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class ReservationController {
    private static final String BASE_PATH = "/api/v1";
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/reservations", ctx -> {
            try {
                List<ReservationModel> reservations = new ArrayList<>();
                ctx.json(reservations);
            } catch (Exception e) {
                logger.error("Error fetching reservations: ", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.post(BASE_PATH + "/reservations", ctx -> {
            try {
                CreateReservationRequest requestDTO = ctx.bodyAsClass(CreateReservationRequest.class);
                
                // Validazione input
                if (requestDTO.getHabitation() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Habitation data is required");
                    return;
                }
                
                HabitationModel reservation = requestDTO.getHabitation();
                
                // Validazioni campi obbligatori
                if (reservation.getName() == null || reservation.getName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Name is required");
                    return;
                }
                
                if (reservation.getAddress() == null || reservation.getAddress().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Address is required");
                    return;
                }
                
                if (reservation.getPrice() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Price must be greater than 0");
                    return;
                }
                
                if (reservation.getRooms() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Rooms must be greater than 0");
                    return;
                }
                
                if (reservation.getStartAvailable() == null || reservation.getEndAvailable() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Start and end dates are required");
                    return;
                }
                
                // Crea l'abitazione
                HabitationModel created = habitationService.createHabitation(reservation, 
                    requestDTO.getHostCode()
                );
                
                ctx.status(HttpStatus.CREATED).json(created);
                
            } catch (IllegalArgumentException e) {
                logger.warn("Validation error: ", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST)
                   .result(e.getMessage());
                   
            } catch (Exception e) {
                logger.error("Error creating reservations", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });
    }
}
