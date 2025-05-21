CREATE TABLE commands (
    id SERIAL PRIMARY KEY,
    command_date TIMESTAMP NOT NULL,
    map_id TEXT NOT NULL,
    map_key TEXT NOT NULL,
    map_value TEXT NOT NULL
);