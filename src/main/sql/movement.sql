CREATE TYPE mouvement_type AS ENUM ('IN', 'OUT');

CREATE TABLE stock_movement (
    id serial
    constraint stock_movement_pk primary key,
    id_ingredient int,
    constraint fk_ingredient foreign key (id_ingredient) references ingredient(id),
    quantity numeric,
    type mouvement_type,
    unit unit_type,
    creation_datetime timestamp
);


insert into stock_movement (id, id_ingredient, quantity, type, unit, creation_datetime)
values
    (1, 1, 5.0, 'IN', 'KG', '2024-01-05 08:00'),
    (2, 1, 0.2, 'OUT', 'KG', '2024-01-06 12:00'),
	(3, 2, 4.0, 'IN', 'KG', '2024-01-05 08:00'),
	(4, 2, 0.15, 'OUT', 'KG', '2024-01-06 12:00'),
	(5, 3, 10.0, 'IN', 'KG', '2024-01-04 09:00'),
	(6, 3, 1.0, 'OUT', 'KG', '2024-01-06 13:00'),
	(7, 4, 3.0, 'IN', 'KG', '2024-01-05 10:00'),
	(8, 4, 0.3, 'OUT', 'KG', '2024-01-06 14:00'),
	(9, 5, 2.5, 'IN', 'KG', '2024-01-05 10:00'),
	(10, 5, 0.2, 'OUT', 'KG', '2024-01-06 14:00');