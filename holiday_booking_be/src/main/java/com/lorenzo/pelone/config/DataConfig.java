package com.lorenzo.pelone.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lorenzo.pelone.model.FeedbackModel;
import com.lorenzo.pelone.model.HabitationModel;
import com.lorenzo.pelone.model.HostModel;
import com.lorenzo.pelone.model.ReservationModel;
import com.lorenzo.pelone.model.UserModel;
import com.lorenzo.pelone.repository.FeedbackDAO;
import com.lorenzo.pelone.repository.ReservationDAO;
import com.lorenzo.pelone.repository.UserDAO;
import com.lorenzo.pelone.service.HabitationService;
import com.lorenzo.pelone.service.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataConfig {

    private final UserService userService;
    private final HabitationService habitationService;
    private final UserDAO userDAO;
    private final ReservationDAO reservationDAO;
    private final FeedbackDAO feedbackDAO;

    @Bean
    @Transactional
    CommandLineRunner loadData() {
        return args -> {
            if (userDAO.count() > 0) {
                log.info("Database già popolato. Salto il caricamento.");
                return;
            }

            Random random = new Random();
            log.info("Inizio popolamento database con dati incrociati...");

            // --- CREAZIONE UTENTI NORMALI (LISTA) ---
            List<UserModel> otherUsers = new ArrayList<>();
            String[] names = {"Luca", "Elena", "Francesco", "Sofia", "Alessandro"};
            String[] lastNames = {"Bianchi", "Neri", "Ricci", "Marini", "Moretti"};
            
            for (int i = 0; i < 5; i++) {
                UserModel u = new UserModel(0, names[i], lastNames[i], 
                    names[i].toLowerCase() + "." + lastNames[i].toLowerCase() + "@email.it", 
                    "Via " + lastNames[i] + " " + (i + 10), null);
                otherUsers.add((UserModel) userService.createUser(u, false));
            }

            UserModel mario = new UserModel(0, "Mario", "Rossi", "mario@test.it", "Via Roma 1", null);
            UserModel savedMario = (UserModel) userService.createUser(mario, false);

            // ---  CREAZIONE HOST E ABITAZIONI ---
            
            UserModel host1User = new UserModel(0, "Chiara", "Verdi", "chiara@test.it", "Via Milano 2", null);
            HostModel host1 = (HostModel) userService.createUser(host1User, true);
            HabitationModel villa = new HabitationModel(0, host1, "Villa Certosa", "Lussuosa villa", 1, "Costa Smeralda", 500.0, 6, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1), null);
            HabitationModel savedVilla = habitationService.createHabitation(villa, host1.getHostCode());

            UserModel host2User = new UserModel(0, "Giorgio", "Galli", "giorgio.host@email.it", "Via Torino 45", null);
            HostModel host2 = (HostModel) userService.createUser(host2User, true);
            HabitationModel chalet = new HabitationModel(0, host2, "Chalet Mountain View", "Chalet Alpino", 1, "Cortina", 250.0, 3, LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), null);
            HabitationModel savedChalet = habitationService.createHabitation(chalet, host2.getHostCode());

            // --- GENERAZIONE PRENOTAZIONI E FEEDBACK INCROCIATI ---

            log.info("Generazione 105 prenotazioni per Super Host test...");
            for (int i = 0; i < 105; i++) {
                ReservationModel res = new ReservationModel(0, savedVilla, savedMario, "Completed", 
                    LocalDate.now().minusDays(i * 3 + 10), LocalDate.now().minusDays(i * 3 + 8), null);
                reservationDAO.save(res);
            }

            log.info("Generazione prenotazioni e feedback per gli altri utenti...");
            for (UserModel user : otherUsers) {
                
                ReservationModel resVilla = new ReservationModel(0, savedVilla, user, "Completed", 
                    LocalDate.now().minusDays(15), LocalDate.now().minusDays(10), null);
                ReservationModel savedResVilla = reservationDAO.save(resVilla);
                
                feedbackDAO.save(new FeedbackModel(null, user, savedResVilla, random.nextInt(2) + 4, "Bello", "Mi sono trovato bene", null));

                ReservationModel resChalet = new ReservationModel(0, savedChalet, user, "Completed", 
                    LocalDate.now().minusDays(5), LocalDate.now().minusDays(2), null);
                ReservationModel savedResChalet = reservationDAO.save(resChalet);
                
                feedbackDAO.save(new FeedbackModel(null, user, savedResChalet, random.nextInt(3) + 3, "Ok", "Bella vista ma freddo", null));

                reservationDAO.save(new ReservationModel(0, savedChalet, user, "Confirmed", 
                    LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), null));
            }

            // --- AGGIORNAMENTO STATO SUPER HOST ---
            userDAO.updateSuperHostStatus(host1.getHostCode());
            userDAO.updateSuperHostStatus(host2.getHostCode());

            log.info("--- CONFIGURAZIONE COMPLETATA ---");
            log.info("Creati 7 utenti totali (2 Host, 5 Clienti + Mario).");
            log.info("Prenotazioni totali caricate: {}", reservationDAO.count());
            log.info("Feedback totali caricati: {}", feedbackDAO.count());
        };
    }
}