package com.lorenzo.pelone.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateReservationRequest;
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
                List<ReservationModel> reservations =
                        reservationService.getAllReservations();

                ctx.status(HttpStatus.OK).json(reservations);

            } catch (Exception e) {
                logger.error("Error fetching reservations", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error fetching reservations");
            }
        });

        app.post(BASE_PATH + "/reservations", ctx -> {
            try {
                CreateReservationRequest request =
                        ctx.bodyAsClass(CreateReservationRequest.class);

                if (request == null) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Request body is required");
                    return;
                }

                if (request.getHabitationId() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Valid habitationId is required");
                    return;
                }

                if (request.getUserId() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Valid userId is required");
                    return;
                }

                if (request.getStartDate() == null ||
                    request.getEndDate() == null) {

                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("StartDate and EndDate are required");
                    return;
                }

                ReservationModel created = reservationService.createReservation(
                                request.getHabitationId(),
                                request.getUserId(),
                                request.getStartDate(),
                                request.getEndDate()
                            );

                ctx.status(HttpStatus.CREATED).json(created);

            } catch (IllegalArgumentException e) {
                logger.warn("Reservation validation error: ", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST)
                   .result(e.getMessage());

            } catch (Exception e) {
                logger.error("Error creating reservation", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .result("Error creating reservation");
            }
        });
    
    }
}
