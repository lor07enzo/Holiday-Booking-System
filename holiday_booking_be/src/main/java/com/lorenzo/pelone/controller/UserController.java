package com.lorenzo.pelone.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateUserRequest;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.service.HolidayBookingService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class UserController {
    private static final String BASE_PATH = "/api/v1";
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final HolidayBookingService userService;

    public UserController() {
        this.userService = new HolidayBookingService();
    }

    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/users", ctx -> {
            
        });

        app.post(BASE_PATH + "/users", ctx -> {
           try {
                CreateUserRequest requestDTO = ctx.bodyAsClass(CreateUserRequest.class);
                
                // Validazione input
                if (requestDTO.getUser() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("User data is required"));
                    return;
                }
                
                UserModel user = requestDTO.getUser();
                
                // Validazioni specifiche
                if (user.getName() == null || user.getName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Name is required"));
                    return;
                }
                
                if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Last name is required"));
                    return;
                }
                
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse("Email is required"));
                    return;
                }
                
                // Chiama il service (un solo metodo che gestisce entrambi i casi)
                Object result = userService.createUser(user, requestDTO.isHost());
                
                // Prepara la risposta in base al tipo
                if (result instanceof HostModel) {
                    HostModel host = (HostModel) result;
                    ctx.status(HttpStatus.CREATED).json(new SuccessResponse("Host created successfully", host.getUser(), host));
                } else {
                    UserModel createdUser = (UserModel) result;
                    ctx.status(HttpStatus.CREATED).json(new SuccessResponse("User created successfully", createdUser, null));
                }
                
            } catch (IllegalArgumentException e) {
                // Errori di validazione business (es: email già esistente)
                logger.warn("Validation error: {}", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST).json(new ErrorResponse(e.getMessage()));
                   
            } catch (Exception e) {
                // Errori del server
                logger.error("Error creating user/host", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(new ErrorResponse("Internal server error"));
            }
        });
    }


    private static class SuccessResponse {
        public String message;
        public UserModel user;
        public HostModel host;
        
        public SuccessResponse(String message, UserModel user, HostModel host) {
            this.message = message;
            this.user = user;
            this.host = host;
        }
    }
    
    // Classe per risposta di errore
    private static class ErrorResponse {
        public String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
