package com.lorenzo.pelone.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.repository.HabitationDAO;

public class HabitationService {
    private static final Logger logger = LoggerFactory.getLogger(HabitationService.class);
    private final HabitationDAO habitationRepository;

    public HabitationService() {
        this.habitationRepository = new HabitationDAO();
    }


    public List<HabitationModel> getAllHabitations() {
        try {
            return habitationRepository.allHabitations();
        } catch (SQLException e) {
            logger.error("Error fetching habitations", e);
            throw new RuntimeException("Error fetching habitations", e);
        }
    }

    public List<HabitationModel> getAllHabitationsByHostCode(int hostCode) {
        try {
            if (!habitationRepository.hostCodeExists(hostCode)) {
                throw new IllegalArgumentException("Host code does not exist");
            }
            return habitationRepository.allHabitationsByHostCode(hostCode);

        } catch (SQLException e) {
            logger.error("Error checking if host code exists: ", e);
            throw new RuntimeException("Error validating host: ", e);
        }
    }

    public HabitationModel createHabitation(HabitationModel habitation, int hostCode) {
        try {
            if (!habitationRepository.hostCodeExists(hostCode)) {
                throw new IllegalArgumentException("Host code does not exist");
            }
        } catch (SQLException e) {
            logger.error("Error checking if host code exists", e);
            throw new RuntimeException("Error validating host", e);
        }
        
        if (habitation.getStartAvailable().isAfter(habitation.getEndAvailable())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            HabitationModel created = habitationRepository.insertHabitation(conn, habitation, hostCode);
            
            HostModel host = habitationRepository.getHostByCode(hostCode);
            created.setHost(host);
            
            conn.commit();
            logger.info("Habitation created successfully! ID: ", created.getId());
            return created;
            
        } catch (SQLException e) {
            logger.error("Error creating habitation", e);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    logger.error("Error rolling back", ex);
                }
            }
            throw new RuntimeException("Error creating habitation", e);
            
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }
}
