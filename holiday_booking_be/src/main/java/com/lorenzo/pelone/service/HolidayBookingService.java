package com.lorenzo.pelone.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.repository.UserRepository;

public class HolidayBookingService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayBookingService.class);
    private final UserRepository userRepository;

    public HolidayBookingService() {
        this.userRepository = new UserRepository();
    }

    
    public List<UserModel> getAllUsers() {
        return userRepository.allUsers();
    }

    public UserModel getUserById(int id) {
        return userRepository.userById(id);
    }

    public List<HostModel> getAllHosts() {
        return userRepository.allHosts();
    }

    public HostModel getHostByCode(int hostCode) {
        HostModel host = userRepository.hostbyCode(hostCode);
        if (host == null) {
            throw new RuntimeException("Host not found with code: " + hostCode);
        } else {
            return host;
        }
    }
    
    public Object createUser(UserModel user, boolean isHost) {
        // Validazione business
        try {
            if (userRepository.emailExists(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
        } catch (SQLException e) {
            logger.error("Error checking email existence", e);
            throw new RuntimeException("Error validating email", e);
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Inserisci utente
            UserModel createdUser = userRepository.insertUser(conn, user);
            
            // 2. Se è host, inserisci anche l'host
            if (isHost) {
                // Logica di business: genera hostCode
                int hostCode = generateUniqueHostCode();
                
                HostModel host = userRepository.insertHost(conn, createdUser, hostCode);
                
                conn.commit();
                logger.info("Host created successfully! User ID: {}, Host Code: {}", createdUser.getId(), host.getHostCode());
                return host;
            } else {
                conn.commit();
                logger.info("User created successfully! ID: {}", createdUser.getId());
                return createdUser;
            }
            
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            if (conn != null) {
                try {
                    conn.rollback();
                    logger.info("Transaction rolled back");
                } catch (SQLException ex) {
                    logger.error("Error rolling back", ex);
                }
            }
            throw new RuntimeException("Error creating user", e);
            
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
    
    
    private int generateUniqueHostCode() {
        return (int) (Math.random() * 900000) + 100000;
    }
}
