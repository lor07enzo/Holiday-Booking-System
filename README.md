# 🏖️ Turista Facoltoso - Sistema Gestione Affitti Vacanze

Sistema Full-Stack per la gestione di una piattaforma di affitto case vacanze con **Java/Javalin** (Backend) e **React/TypeScript** (Frontend).

## 📋 Indice

- [Panoramica](#-panoramica)
- [Tecnologie](#-tecnologie)
- [Architettura](#-architettura)
- [Modello Dati](#-modello-dati)
- [API Endpoints](#-api-endpoints)
- [Installazione](#-installazione)
- [Requisiti](#-requisiti)

---

## 🎯 Panoramica

Piattaforma backoffice per gestire utenti, host, abitazioni, prenotazioni e feedback senza autenticazione. Focus sulla logica di business e gestione dati.

### Caratteristiche

- ✅ CRUD completo per tutte le entità
- ✅ Prenotazioni con controllo disponibilità real-time
- ✅ Dashboard statistiche
- ✅ Interfaccia responsive
- ✅ Validazione client + server
- ✅ Calcolo automatico costi

---

## 🛠️ Tecnologie

**Backend:** Java 17, Javalin 6, PostgreSQL, Lombok, JDBC  
**Frontend:** React 18, TypeScript, Vite, Tailwind CSS, shadcn/ui, date-fns  
**Tools:** Maven, npm, Git

---

## 🏗️ Architettura

### Backend - Layered Architecture
```
Controller → Service (Business Logic) → Repository (Data Access) → Database
```

**Package Structure:**
```
com.lorenzo.pelone/
├── config/         # Database configuration
├── controller/     # HTTP handlers
├── dto/            # Request/Response objects
├── model/          # Domain entities
├── repository/     # Data access layer
├── service/        # Business logic
└── Main.java
```

**Frontend Structure:**
```
src/
├── components/     # UI components
├── context/        # Global state
├── pages/          # Route components
├── types/          # TypeScript interfaces
└── App.tsx
```

---

## 📊 Modello Dati

### Entità Principali

**User:** id, name, last_name, email, address  
**Host:** user_id (FK), host_code (unique), super_host  
**Habitation:** id, host_code (FK), name, description, address, floor, rooms, price, start_available, end_available  
**Reservation:** id, habitation_id (FK), user_id (FK), status, start_date, end_date  
**Feedback:** id, reservation_id (FK), user_id (FK), host_user_id (FK), title, text, rating (1-5)

### Relazioni
```
Users ←→ Hosts → Habitations → Reservations → Feedback
  ↓                                    ↓
  └─────────────────────────────────────┘
```

---

## 🔌 API Endpoints

**Base URL:** `http://localhost:7070/api/v1`

### Users
- `GET /users` - Lista tutti
- `GET /users/{id}` - Dettagli
- `POST /users` - Crea (body: `{user: {...}, host: boolean}`)

### Hosts
- `GET /hosts` - Lista tutti
- `GET /hosts/{hostCode}` - Per codice

### Habitations
- `GET /habitations` - Lista tutte
- `POST /habitations` - Crea (body: `{hostCode: int, habitation: {...}}`)

### Reservations
- `GET /reservations` - Lista tutte
- `POST /reservations` - Crea (body: `{habitationId, userId, startDate, endDate}`)

### Feedback
- `GET /feedback` - Lista tutti
- `POST /feedback` - Crea (body: `{reservationId, userId, hostUserId, title, text, rating}`)

**Response Codes:** 200 (OK), 201 (Created), 400 (Bad Request), 404 (Not Found), 500 (Server Error)

---

## 🗺️ Rotte Frontend

| Route | Descrizione |
|-------|-------------|
| `/` | Dashboard con statistiche |
| `/create-user` | Form creazione utente/host |
| `/new-habitation` | Form creazione abitazione |
| `/new-reservation` | Form prenotazione con calendario |
| `/feedback/:id` | Visualizzazione feedback |

---

## 📦 Installazione

### Prerequisiti
- Java 17+, Node.js 18+, PostgreSQL 14+, Git

### Setup Database
```bash
createdb holiday_booking
psql -d holiday_booking -f database/schema.sql
```

### Backend
```bash
cd backend
# Configura src/main/resources/database.properties:
# DB_URL=jdbc:postgresql://localhost:5432/holiday_booking
# DB_USER=postgres
# DB_PASSWORD=your_password

mvn clean install
java -jar target/holiday-booking-1.0.jar
```
✅ Backend: `http://localhost:7070`

### Frontend
```bash
cd frontend
npm install
echo "VITE_API_URL=http://localhost:7070" > .env
npm run dev
```
✅ Frontend: `http://localhost:5173`

---

## 📋 Requisiti

### Funzionali
- **Utenti:** CRUD, validazione email, creazione host
- **Host:** Generazione codice univoco, super-host (≥100 prenotazioni)
- **Abitazioni:** CRUD, validazione host, periodo disponibilità
- **Prenotazioni:** Controllo disponibilità, validazione date, calcolo costi, stati (Confirmed/Annulled/Completed)
- **Feedback:** Rating 1-5, associazione a prenotazione/host
- **Statistiche:** Media camere, ricavi totali, dashboard

### Non Funzionali
- **Performance:** API < 500ms (semplici), < 2s (complesse)
- **Scalabilità:** 10K+ utenti, query ottimizzate, indici DB
- **Affidabilità:** Transazioni, rollback automatico, logging completo
- **Usabilità:** Responsive, toast notifications, validazione real-time
- **Sicurezza:** PreparedStatement, CORS, constraint DB
- **Manutenibilità:** Pattern MVC/Repository, separazione concerns

---

## 🚀 Utilizzo

**Crea Utente/Host:** `/create-user` → compila form → checkbox "is Host" genera `host_code`  
**Crea Abitazione:** `/new-habitation` → seleziona host → dettagli → periodo disponibilità  
**Crea Prenotazione:** Homepage → click abitazione → calendario (date grigie = occupate) → utente → submit  
**Dashboard:** Homepage mostra totali + click card per dettagli in dialog

---

## 👤 Autore

**Lorenzo Pelone** - Progetto finale Full-Stack Development

---

## 📄 Licenza

Progetto educativo - © 2026

<div align="center">

**Made with ❤️ and ☕**

</div>