# 🏖️ Turista Facoltoso - Sistema Gestione Affitti Vacanze

[![Java](https://img.shields.io/badge/Backend-Java%2021-orange?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Javalin](https://img.shields.io/badge/Framework-Javalin%206-blue?style=flat-square)](https://javalin.io/)
[![React](https://img.shields.io/badge/Frontend-React%2019-61DAFB?style=flat-square&logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/Language-TypeScript-3178C6?style=flat-square&logo=typescript)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/DB-PostgreSQL-336791?style=flat-square&logo=postgresql)](https://www.postgresql.org/)

**Turista Facoltoso** è una piattaforma Full-Stack per la gestione completa di un ecosistema di affitto case vacanze. Il progetto si focalizza sulla solidità della logica di business, l'integrità dei dati e un'esperienza utente fluida per il backoffice.

---

## Panoramica

Piattaforma gestionale progettata per amministrare utenti, host, abitazioni e prenotazioni. Il sistema gestisce l'intero ciclo di vita del soggiorno, dal controllo disponibilità al feedback post-permanenza.

### Caratteristiche Principali
- **CRUD Completo:** Gestione totale di tutte le entità del dominio.
- **Smart Booking:** Prenotazioni con controllo disponibilità in tempo reale e calcolo costi automatico.
- **Status Host:** Logica automatizzata per lo stato di "Super-Host" (basata su ≥100 prenotazioni).
- **Dashboard Statistiche:** Monitoraggio ricavi, media camere e metriche di piattaforma.
- **Validazione Dual-Layer:** Controlli rigorosi sia lato Client (React) che lato Server (Java/DB).
- **UX Moderna:** Interfaccia responsive con notifiche toast e componenti interattivi.

---

## Tecnologie Utilizzate

| Layer | Tecnologie |
| :--- | :--- |
| **Backend** | Java 21, Javalin 6, PostgreSQL, Lombok, JDBC, Maven |
| **Frontend** | React 19, TypeScript, Vite, Tailwind CSS, shadcn/ui, date-fns |
| **Tools** | Git, Postman, npm |

---

## Architettura del Sistema

Il progetto adotta una **Layered Architecture** per garantire manutenibilità e separazione delle responsabilità.

### Backend Structure (`com.lorenzo.pelone`)
- `config/`: Configurazione database e pool di connessioni.
- `controller/`: Gestione delle richieste HTTP e routing API.
- `service/`: Logica di business e regole di validazione.
- `repository/`: Layer di persistenza (Query SQL via JDBC).
- `model/`: Entità del dominio (POJO).
- `dto/`: Oggetti per il trasferimento dati ottimizzato.

### Frontend Structure (`/src`)
- `components/`: UI components riutilizzabili.
- `context/`: Gestione dello stato globale.
- `pages/`: Componenti di pagina e routing.
- `types/`: Interfacce e definizioni TypeScript.

---

## Modello Dati

### Entità e Relazioni
- **User:** Anagrafica utente (Name, LastName, Address, Email).
- **Host:** Estensione dell'utente con `host_code` PK  e flag `super_host`.
- **Habitation:** Dettagli immobile, costo e range di disponibilità.
- **Reservation:** Collegamento utente-abitazione con stato (Confirmed, Annulled, Completed).
- **Feedback:** Recensioni legate a specifiche prenotazioni (Rating 1-5).

---

## API Endpoints (v1)

**Base URL:** `http://localhost:7000/api/v1`

### Users & Hosts
| Metodo | Endpoint | Descrizione |
| :--- | :--- | :--- |
| `GET` | `/users` | Lista tutti gli utenti registrati |
| `GET` | `/users/{id}` | Dettagli di un singolo utente |
| `POST` | `/users` | Registra nuovo Utente o Host |
| `GET` | `/hosts` | Lista tutti gli Host |
| `GET` | `/hosts/{hostCode}` | Recupera Host tramite codice univoco |

### Habitations
| Metodo | Endpoint | Descrizione |
| :--- | :--- | :--- |
| `GET` | `/habitations` | Lista globale delle abitazioni |
| `GET` | `/hosts/{hostCode}/habitations` | Lista abitazioni gestite da un Host specifico |
| `POST` | `/habitations` | Crea una nuova abitazione |

### Reservations
| Metodo | Endpoint | Descrizione |
| :--- | :--- | :--- |
| `GET` | `/reservations` | Lista completa delle prenotazioni |
| `GET` | `/reservations/last-month` | Prenotazioni effettuate nell'ultimo mese |
| `GET` | `/reservations/statistics` | Statistiche aggregate (es. ricavi, volumi) |
| `GET` | `/users/{userId}/reservations` | Storico prenotazioni di un utente specifico |
| `POST` | `/reservations` | Crea prenotazione con validazione disponibilità |

### Feedback
| Metodo | Endpoint | Descrizione |
| :--- | :--- | :--- |
| `GET` | `/feedback` | Lista di tutte le recensioni |
| `POST` | `/feedback` | Invia un nuovo feedback |

---

## Requisiti & Qualità

- **Performance:** Risposte API < 500ms per operazioni standard.
- **Affidabilità:** Gestione transazionale per evitare dati inconsistenti.
- **Sicurezza:** Protezione da SQL Injection tramite `PreparedStatement`.
- **Scalabilità:** Database ottimizzato con indici per query su grandi volumi di dati.
- **Usabilità:** Interfaccia responsive testata su diversi dispositivi.

---

## Utilizzo Rapido

1. **Crea Utente/Host:** Accedi a `/create-user` e registra un profilo (opzione Host per affittare).
2. **Pubblica Abitazione:** Vai su `/new-habitation` e inserisci i dettagli dell'immobile.
3. **Prenota:** Dalla Dashboard, seleziona un abitazione, scegli le date dal calendario e l'utente che effettua la prenotazione, poi conferma.
4. **Dashboard:** Monitora i totali e i dettagli delle prenotazioni direttamente dalla Home.

---

## Autore

**Lorenzo Pelone** *Progetto Full-Stack Development - 2026*

