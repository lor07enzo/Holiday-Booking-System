CREATE TABLE users (
    id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL,
	last_name VARCHAR(50) NOT NULL,
	email VARCHAR(50) UNIQUE NOT NULL,
	address VARCHAR(150) NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hosts (
    user_id INTEGER PRIMARY KEY REFERENCES users(id),
	host_code INTEGER UNIQUE NOT NULL,
	super_host BOOLEAN DEFAULT FALSE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE habitations (
    id SERIAL PRIMARY KEY,
    host_code INTEGER NOT NULL REFERENCES hosts(host_code),
    name VARCHAR(50) NOT NULL,
	description VARCHAR(250) NOT NULL,
    address VARCHAR(150) NOT NULL,
    floor INTEGER NOT NULL,
    rooms INTEGER NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    start_available DATE NOT NULL,
    end_available DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reservations (
    id SERIAL PRIMARY KEY,
    habitation_id INTEGER NOT NULL REFERENCES habitations(id),
    user_id INTEGER NOT NULL REFERENCES users(id),
    status VARCHAR(50) NOT NULL DEFAULT 'Confirmed',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT valid_status CHECK (status IN ('Confirmed', 'Annulled', 'Completed')),
    CONSTRAINT valid_dates CHECK (end_date > start_date)
);