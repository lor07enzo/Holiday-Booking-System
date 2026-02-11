package com.lorenzo.pelone.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateUserRequest;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.service.UserService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class UserController {
    private static final String BASE_PATH = "/api/v1";
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/users", ctx -> {
            try {
                List<UserModel> users = userService.getAllUsers();
                ctx.json(users);
            } catch (Exception e) {
                logger.error("Error fetching users", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.get(BASE_PATH + "/users/{id}", ctx -> {
            try {
                UserModel getUser = userService.getUserById(Integer.parseInt(ctx.pathParam("id")));
                ctx.json(getUser);
            } catch (Exception e) {
                logger.error("Error fetching user by ID", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.get(BASE_PATH + "/hosts", ctx -> {
            try {
                List<HostModel> hosts = userService.getAllHosts();
                ctx.json(hosts);
            } catch (Exception e) {
                logger.error("Error fetching hosts", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.get(BASE_PATH + "/hosts/statistics", ctx -> {
            try {
                List<java.util.Map<String, Object>> stats = userService.getResFromHost();
                ctx.json(stats);
            } catch (Exception e) {
                logger.error("Error fetching host statistics", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.get(BASE_PATH + "/hosts/{hostCode}", ctx -> {
            try {
                int hostCode = Integer.parseInt(ctx.pathParam("hostCode"));
                HostModel host = userService.getHostByCode(hostCode);
                ctx.json(host);
            } catch (NumberFormatException e) {
                logger.error("Invalid host code format", e);
                ctx.status(HttpStatus.BAD_REQUEST).result("Invalid host code format");
            } catch (IllegalArgumentException e) {
                logger.error("Host not found", e);
                ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
            } catch (Exception e) {
                logger.error("Error fetching host by code", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });

        app.post(BASE_PATH + "/users", ctx -> {
           try {
                CreateUserRequest requestDTO = ctx.bodyAsClass(CreateUserRequest.class);
                
                if (requestDTO.getUser() == null) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("User data is required");
                    return;
                }
                
                UserModel user = requestDTO.getUser();
                
                if (user.getName() == null || user.getName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Name is required");
                    return;
                }
                
                if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Last name is required");
                    return;
                }
                
                if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                    ctx.status(HttpStatus.BAD_REQUEST).result("Email is required");
                    return;
                }
                Object result = userService.createUser(user, requestDTO.isHost());
                
                if (result instanceof HostModel) {
                    HostModel host = (HostModel) result;
                    ctx.status(HttpStatus.CREATED).result("Host created successfully: " + host);
                } else {
                    UserModel createdUser = (UserModel) result;
                    ctx.status(HttpStatus.CREATED).result("User created successfully: " + createdUser);
                }
                
            } catch (IllegalArgumentException e) {
                logger.warn("Validation error: ", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
                   
            } catch (Exception e) {
                logger.error("Error creating user/host: ", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result(e.getMessage());
            }
        });
    }
}
