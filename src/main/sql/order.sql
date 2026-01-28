CREATE TABLE orde (
                      id serial constraint order_pk primary key,
                      reference varchar(10),
                      creation_datetime timestamp
);

CREATE TABLE dish_order (
                            id serial constraint dish_order_pk primary key,
                            id_order int,
                            constraint order_fk foreign key (id_order) references orde(id),
                            id_dish int,
                            constraint dish_fk foreign key (id_dish) references dish(id),
                            quantity int
);