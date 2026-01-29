CREATE TABLE restaurant_table
(
    id     SERIAL PRIMARY KEY,
    number INTEGER NOT NULL UNIQUE
);

CREATE TABLE table_order
(
    id                 SERIAL PRIMARY KEY,

    id_table           INTEGER   NOT NULL,
    id_order           INTEGER   NOT NULL,

    arrival_datetime   TIMESTAMP NOT NULL,
    departure_datetime TIMESTAMP NOT NULL,

    CONSTRAINT fk_table
        FOREIGN KEY (id_table)
            REFERENCES restaurant_table (id)
            ON DELETE CASCADE,

    CONSTRAINT fk_order
        FOREIGN KEY (id_order)
            REFERENCES "order" (id)
            ON DELETE CASCADE
);
