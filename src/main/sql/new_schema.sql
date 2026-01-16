ALTER TABLE ingredient DROP COLUMN id_dish;

CREATE TYPE unit_type AS ENUM ('PCS', 'KG', 'L');

CREATE TABLE dish_ingredient (
    id serial constraint dish_ingredient_pk primary key,
    id_dish int,
    constraint fk_dish
    foreign key (id_dish) references dish(id),
    id_ingredient int,
    constraint fk_ingredient
    foreign key (id_ingredient) references ingredient(id),
    quantity_required numeric,
    unit unit_type
);

