package com.lorenzo.pelone.config;

import com.lorenzo.pelone.model.*;
import com.lorenzo.pelone.repository.*;
import com.lorenzo.pelone.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

            // --- 1. CREAZIONE UTENTI NORMALI (LISTA) ---
            List<UserModel> otherUsers = new ArrayList<>();
            String[] names = {"Luca", "Elena", "Francesco", "Sofia", "Alessandro"};
            String[] lastNames = {"Bianchi", "Neri", "Ricci", "Marini", "Moretti"};
            
            for (int i = 0; i < 5; i++) {
                UserModel u = new UserModel(0, names[i], lastNames[i], 
                    names[i].toLowerCase() + "." + lastNames[i].toLowerCase() + "@email.it", 
                    "Via " + lastNames[i] + " " + (i + 10), null);
                otherUsers.add((UserModel) userService.createUser(u, false));
            }

            // Utente storico per il test Super Host
            UserModel mario = new UserModel(0, "Mario", "Rossi", "mario@test.it", "Via Roma 1", null);
            UserModel savedMario = (UserModel) userService.createUser(mario, false);

            // --- 2. CREAZIONE HOST E ABITAZIONI ---
            
            // Host 1 (Chiara) -> Villa Certosa
            UserModel host1User = new UserModel(0, "Chiara", "Verdi", "chiara@test.it", "Via Milano 2", null);
            HostModel host1 = (HostModel) userService.createUser(host1User, true);
            HabitationModel villa = new HabitationModel(0, host1, "Villa Certosa", "Lussuosa villa", 1, "Costa Smeralda", 500.0, 6, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1), null);
            HabitationModel savedVilla = habitationService.createHabitation(villa, host1.getHostCode());

            // Host 2 (Giorgio) -> Chalet Mountain View
            UserModel host2User = new UserModel(0, "Giorgio", "Galli", "giorgio.host@email.it", "Via Torino 45", null);
            HostModel host2 = (HostModel) userService.createUser(host2User, true);
            HabitationModel chalet = new HabitationModel(0, host2, "Chalet Mountain View", "Chalet Alpino", 1, "Cortina", 250.0, 3, LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6), null);
            HabitationModel savedChalet = habitationService.createHabitation(chalet, host2.getHostCode());

            // --- 3. GENERAZIONE PRENOTAZIONI E FEEDBACK INCROCIATI ---

            // A. Mario prenota 105 volte la Villa (per attivare Super Host)
            log.info("Generazione 105 prenotazioni per Super Host test...");
            for (int i = 0; i < 105; i++) {
                ReservationModel res = new ReservationModel(0, savedVilla, savedMario, "Completed", 
                    LocalDate.now().minusDays(i * 3 + 10), LocalDate.now().minusDays(i * 3 + 8), null);
                reservationDAO.save(res);
            }

            // B. Gli altri 5 utenti prenotano entrambe le case
            log.info("Generazione prenotazioni e feedback per gli altri utenti...");
            for (UserModel user : otherUsers) {
                // Ogni utente fa 2 prenotazioni completate e 1 confermata
                
                // Prenotazione sulla Villa (Host 1)
                ReservationModel resVilla = new ReservationModel(0, savedVilla, user, "Completed", 
                    LocalDate.now().minusDays(15), LocalDate.now().minusDays(10), null);
                ReservationModel savedResVilla = reservationDAO.save(resVilla);
                
                // Feedback sulla Villa
                feedbackDAO.save(new FeedbackModel(null, user, savedResVilla, random.nextInt(2) + 4, "Bello", "Mi sono trovato bene", null));

                // Prenotazione sullo Chalet (Host 2)
                ReservationModel resChalet = new ReservationModel(0, savedChalet, user, "Completed", 
                    LocalDate.now().minusDays(5), LocalDate.now().minusDays(2), null);
                ReservationModel savedResChalet = reservationDAO.save(resChalet);
                
                // Feedback sullo Chalet
                feedbackDAO.save(new FeedbackModel(null, user, savedResChalet, random.nextInt(3) + 3, "Ok", "Bella vista ma freddo", null));

                // Una prenotazione futura (Confirmed) per variare
                reservationDAO.save(new ReservationModel(0, savedChalet, user, "Confirmed", 
                    LocalDate.now().plusDays(10), LocalDate.now().plusDays(15), null));
            }

            // --- 4. AGGIORNAMENTO STATO SUPER HOST ---
            userDAO.updateSuperHostStatus(host1.getHostCode());
            userDAO.updateSuperHostStatus(host2.getHostCode());

            log.info("--- CONFIGURAZIONE COMPLETATA ---");
            log.info("Creati 7 utenti totali (2 Host, 5 Clienti + Mario).");
            log.info("Prenotazioni totali caricate: {}", reservationDAO.count());
            log.info("Feedback totali caricati: {}", feedbackDAO.count());
        };
    }
}