package com.lorenzo.pelone.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.dto.CreateFeedbackRequest;
import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.service.FeedbackService;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class FeedbackController {
    private static final String BASE_PATH = "/api/v1";
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    private final FeedbackService feedbackService;

    public FeedbackController() {
        this.feedbackService = new FeedbackService();
    }


    public void registerRoutes(Javalin app) {
        app.get(BASE_PATH + "/feedback", ctx -> {
            List<FeedbackModel> feedbacks = feedbackService.getAllFeedback();
            ctx.json(feedbacks);
        });

        app.post(BASE_PATH + "/feedback", ctx -> {
            try {
                CreateFeedbackRequest request = ctx.bodyAsClass(CreateFeedbackRequest.class);
                
                String newId = feedbackService.createFeedback(
                    request.getReservationId(),
                    request.getUserId(),
                    request.getTitle(),
                    request.getText(),
                    request.getScore()
                );

                ctx.status(HttpStatus.CREATED).result("Feedback crated successfully with ID: " + newId);

            } catch (IllegalArgumentException e) {
                logger.warn("Validation error feedback: {}", e.getMessage());
                ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
                
            } catch (Exception e) {
                logger.error("Generic error for creating feedback", e);
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Internal Server error");
            }
        });
    }
}
