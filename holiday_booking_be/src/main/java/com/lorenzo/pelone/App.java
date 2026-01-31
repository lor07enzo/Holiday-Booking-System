package com.lorenzo.pelone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.controller.UserController;
import com.sun.tools.javac.Main;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;


public class App 
{
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static void main( String[] args )
    {
        DatabaseConfig.init("config.properties");

        // Configurazione Jackson per LocalDateTime
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(mapper));
        }).start(7000);

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        app.options("/*", ctx -> { 
            ctx.status(204); 
        });
        logger.info("Server started on port 7000");

        UserController holidayBookingController = new UserController();
        holidayBookingController.registerRoutes(app);
    }
}
