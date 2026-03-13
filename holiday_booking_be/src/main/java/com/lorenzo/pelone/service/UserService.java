package com.lorenzo.pelone.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.repository.UserDAO;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDAO userDAO;

    public List<UserModel> getAllUsers() {
        return userDAO.findAll();
    }

    public UserModel getUserById(int id) {
        return userDAO.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));
    }

    public List<HostModel> getAllHosts() {
        return userDAO.findAllHosts();
    }

    public HostModel getHostByCode(int hostCode) {
        // Estraiamo il valore dall'Optional o lanciamo l'eccezione in una riga sola
        return userDAO.findHostByCode(hostCode)
                .orElseThrow(() -> new IllegalArgumentException("Host not found with code: " + hostCode));
    }

    @Transactional 
    public Object createUser(UserModel user, boolean isHost) {
        
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserModel createdUser = userDAO.save(user);

        if (isHost) {
            int hostCode = generateUniqueHostCode();
            userDAO.insertHostNative(createdUser.getId(), hostCode, false);
            log.info("Host created successfully! User ID: {}, Host Code: {}", createdUser.getId(), hostCode);
            return userDAO.findHostByCode(hostCode).get();
        }

        log.info("User created successfully! ID: {}", createdUser.getId());
        return createdUser;
    }

    private int generateUniqueHostCode() {
        return new Random().nextInt(900000) + 100000;
    }
}
