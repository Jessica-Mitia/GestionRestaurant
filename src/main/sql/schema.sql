CREATE TYPE dish_type_enum AS ENUM ('START', 'MAIN', 'DESSERT');

CREATE TABLE dish (
    id serial
        constraint dish_pk primary key,
    name varchar(100) not null,
    dish_type dish_type_enum
);

CREATE TYPE category_enum AS ENUM ('VEGETABLE', 'ANIMAL', 'MARINE', 'DAIRY', 'OTHER');

CREATE TABLE ingredient (
    id serial
        constraint ingredient_pk primary key,
    name varchar(100) not null,
    price numeric,
    category category_enum,
    constraint fk_dish
        foreign key (id_dish)
        references dish(id)
);

ALTER TABLE dish
    ADD COLUMN IF NOT EXISTS price DOUBLE PRECISION;


-- SELECT setval('id_dish_seq', (SELECT MAX(id) FROM dish));
-- SELECT setval('ingredient_id_seq', (SELECT MAX(id)) FROM ingredient));