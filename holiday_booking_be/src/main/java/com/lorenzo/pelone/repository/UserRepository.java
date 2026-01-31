package com.lorenzo.pelone.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lorenzo.pelone.config.DatabaseConfig;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;

public class UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    

    public UserModel insertUser(Connection conn, UserModel user) throws SQLException {
        String sql = "INSERT INTO users (name, last_name, email, address) VALUES (?, ?, ?, ?) RETURNING id, created_at";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            }
            
            logger.debug("User inserted with ID: {}", user.getId());
            return user;
        }
    }

     public HostModel insertHost(Connection conn, UserModel user, int hostCode) throws SQLException {
        String sql = "INSERT INTO hosts (user_id, host_code, super_host) VALUES (?, ?, ?) RETURNING host_code";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            ps.setInt(2, hostCode);
            ps.setBoolean(3, false);
            
            ResultSet rs = ps.executeQuery();
            
            HostModel host = new HostModel();
            host.setUser(user);
            host.setSuperHost(false);
            
            if (rs.next()) {
                host.setHostCode(rs.getInt("host_code"));
            }
            
            logger.debug("Host inserted with code: {}", host.getHostCode());
            return host;
        }
    }

    // Controllo se l'utente già esiste
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
