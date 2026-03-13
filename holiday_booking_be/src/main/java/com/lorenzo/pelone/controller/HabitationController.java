package com.lorenzo.pelone.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.lorenzo.pelone.dto.CreateHabitationRequest;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.service.HabitationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class HabitationController {

    private final HabitationService habitationService;


    @GetMapping("/habitations")
    public List<HabitationModel> getAllHabitations() {
        return habitationService.getAllHabitations();
    }
   
    @GetMapping("/hosts/{hostCode}/habitations")
    public List<HabitationModel> getAllHabitationsByHostCode(@PathVariable int hostCode) {
        return habitationService.getAllHabitationsByHostCode(hostCode);
    }

    @PostMapping("/habitations")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitationModel createHabitation(@Valid @RequestBody CreateHabitationRequest requestDTO) {
        
        log.info("Creating new habitation for host: {}", requestDTO.getHostCode());
        
        return habitationService.createHabitation(
            requestDTO.getHabitation(), 
            requestDTO.getHostCode()
        );
    }
    
}
