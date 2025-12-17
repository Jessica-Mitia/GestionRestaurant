create user mini_dish_db_manager with password '123456';

create database mini_dish_db owner mini_dish_db_manager;

\c mini_dish_db;

grant create on schema public to mini_dish_db_manager;

alter default privileges in schema public
grant select, insert, update, delete on tables to mini_dish_db_manager;

alter default privileges in schema public
grant usage, select, update on sequences to mini_dish_db_manager;