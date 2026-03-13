package com.lorenzo.pelone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lorenzo.pelone.dto.CreateFeedbackRequest;
import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.service.FeedbackService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/feedback")
@RequiredArgsConstructor
@Slf4j
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public List<FeedbackModel> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createFeedback(@Valid @RequestBody CreateFeedbackRequest request) {
        log.info("Creating feedback for reservation ID: {}", request.getReservationId());
        
        String newId = feedbackService.createFeedback(
            request.getReservationId(),
            request.getUserId(),
            request.getTitle(),
            request.getText(),
            request.getScore()
        );
        
        return "Feedback created successfully with ID: " + newId;
    }
    
}
