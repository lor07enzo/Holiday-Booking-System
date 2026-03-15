package com.lorenzo.pelone.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.repository.HabitationDAO;
import com.lorenzo.pelone.repository.UserDAO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitationService {
    private final HabitationDAO habitationDAO;
    private final UserDAO userDAO;

    
    public List<HabitationModel> getAllHabitations() {
        return habitationDAO.findAll();
    }

    public List<HabitationModel> getAllHabitationsByHostCode(int hostCode) {
        if (!habitationDAO.existsByHostCode(hostCode)) {
            throw new IllegalArgumentException("Host code does not exist: " + hostCode);
        }
        return habitationDAO.findAllByHostHostCode(hostCode);
    }

    @Transactional
    public HabitationModel createHabitation(HabitationModel habitation, int hostCode) {
        
        if (habitation.getStartAvailable().isAfter(habitation.getEndAvailable())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        HostModel host = userDAO.findHostByCode(hostCode)
            .orElseThrow(() -> new IllegalArgumentException("Host code does not exist: " + hostCode));

        habitation.setHost(host);
        
        HabitationModel saved = habitationDAO.save(habitation);
        
        log.info("Habitation created successfully! ID: {}", saved.getId());
        return saved;
    }
}
