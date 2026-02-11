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
        DataRetriever data = new DataRetriever();

        Dish dish = data.findDishById(1);
        System.out.println(dish);

        Order order = data.findOrderByReference("ORD00001");
        System.out.println(order);
    }

}