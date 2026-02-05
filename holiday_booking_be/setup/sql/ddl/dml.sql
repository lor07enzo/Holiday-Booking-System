INSERT INTO users (name, last_name, email, address)
VALUES ('nome', 'cognome', 'email@example.com', 'Via Roma, 56');

INSERT INTO hosts (user_id )
VALUES 1;

INSERT INTO habitations (host_code, name, description, address, floor, rooms, price, start_available, end_available)
VALUES (304582, 'Casa sul mare', 'Descrizione della casa sul mare', 'Via sette bagni, 45', 1, 4, 50.34, 
2025-12-1, 2026-12-30);

INSERT INTO reservations (habitation_id, user_id, start_date, end_date)
VALUES (1, 2, '2026-01-10', '2026-01-11');

INSERT INTO feedback (reservation_id, user_id, title, text_description, score)
VALUES (1, 1, 'Ambiente molto alla moda', 'Abitazione molto bella', 5);