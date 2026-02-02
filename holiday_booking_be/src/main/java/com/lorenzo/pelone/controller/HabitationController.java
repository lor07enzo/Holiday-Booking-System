package com.lorenzo.pelone.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateHabitationRequest;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.service.HabitationService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class HabitationController {
    private static final String BASE_PATH = "/api/v1";
    private static final Logger logger = LoggerFactory.getLogger(HabitationController.class);
    private final HabitationService habitationService;

    public HabitationController() {
        this.habitationService = new HabitationService();
    }


    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/habitations", ctx -> {
            try {
                List<HabitationModel> habitations = habitationService.getAllHabitations();
                ctx.json(habitations);
            } catch (Exception e) {
                logger.error("Error fetching habitations", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.post(BASE_PATH + "/habitations", ctx -> {
            try {
                CreateHabitationRequest requestDTO = ctx.bodyAsClass(CreateHabitationRequest.class);
                
                // Validazione input
                if (requestDTO.getHabitation() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Habitation data is required");
                    return;
                }
                
                HabitationModel habitation = requestDTO.getHabitation();
                
                // Validazioni campi obbligatori
                if (habitation.getName() == null || habitation.getName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Name is required");
                    return;
                }
                
                if (habitation.getAddress() == null || habitation.getAddress().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Address is required");
                    return;
                }
                
                if (habitation.getPrice() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Price must be greater than 0");
                    return;
                }
                
                if (habitation.getRooms() <= 0) {
                    ctx.status(HttpStatus.BAD_REQUEST)
                       .result("Rooms must be greater than 0");
                    return;
                }
                
                if (habitation.getStartAvailable() == null || habitation.getEndAvailable() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Start and end dates are required");
                    return;
                }
                
                // Crea l'abitazione
                HabitationModel created = habitationService.createHabitation(
                    habitation, 
                    requestDTO.getHostCode()
                );
                
                ctx.status(HttpStatus.CREATED).json(created);
                
            } catch (IllegalArgumentException e) {
                logger.warn("Validation error: ", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST)
                   .result(e.getMessage());
                   
            } catch (Exception e) {
                logger.error("Error creating habitation", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });
    }
}
