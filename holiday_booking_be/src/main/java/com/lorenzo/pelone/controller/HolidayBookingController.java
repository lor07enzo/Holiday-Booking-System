package com.lorenzo.pelone.controller;

import com.lorenzo.pelone.service.HolidayBookingService;

import io.javalin.Javalin;

public class HolidayBookingController {
    private static final String BASE_PATH = "/api/v1";
    HolidayBookingService service = new HolidayBookingService();

    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/bookings", ctx -> {
            // Logica per ottenere le vacanze
            ctx.result("Elenco delle vacanze");
        });

        app.post(BASE_PATH + "/bookings", ctx -> {
            // Logica per prenotare una vacanza
            ctx.result("Vacanza prenotata");
        });
    }
}
