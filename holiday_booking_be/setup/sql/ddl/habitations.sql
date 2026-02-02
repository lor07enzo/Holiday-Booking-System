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