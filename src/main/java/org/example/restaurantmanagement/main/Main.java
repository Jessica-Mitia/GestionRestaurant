package org.example.restaurantmanagement.main;

import org.example.restaurantmanagement.db.DBConnection;
import org.example.restaurantmanagement.model.*;
import org.example.restaurantmanagement.service.DataRetriever;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever dr = new DataRetriever();

        try {
            Table table = new Table();
            table.setId(2);
            table.setNumber(2);


            TableOrder tableOrder = new TableOrder();
            tableOrder.setTable(table);
            tableOrder.setArrivalDateTime(Instant.now());
            tableOrder.setDepartureDateTime(Instant.now().plus(90, ChronoUnit.MINUTES));

            Order order = new Order();
            order.setId(54);
            order.setReference("ORD00015");
            order.setCreationDateTime(Instant.now());
            order.setTable(tableOrder);

            Dish dish = dr.findDishById(1);

            DishOrder dishOrder = new DishOrder();
            dishOrder.setId(36);
            dishOrder.setDish(dish);
            dishOrder.setQuantity(2);

            order.setDishOrders(List.of(dishOrder));

            System.out.println(dr.saveOrder(order));;

            System.out.println("Commande enregistrée avec succès !");

        } catch (Exception e) {
            System.out.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

}