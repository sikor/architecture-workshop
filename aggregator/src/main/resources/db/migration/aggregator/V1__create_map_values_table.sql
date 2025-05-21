CREATE TABLE map_values (
    command_date TIMESTAMP NOT NULL,
    map_id TEXT NOT NULL,
    map_key TEXT NOT NULL,
    map_value TEXT NOT NULL,
    CONSTRAINT uq_map_id_map_key UNIQUE (map_id, map_key)
);