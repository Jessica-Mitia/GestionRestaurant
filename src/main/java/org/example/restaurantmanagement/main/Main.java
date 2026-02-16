package org.example.restaurantmanagement.main;

import org.example.restaurantmanagement.db.DBConnection;
import org.example.restaurantmanagement.model.*;
import org.example.restaurantmanagement.service.DataRetriever;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever dr = new DataRetriever();

        Dish dish = dr.findDishById(1);

        System.out.println(dish.getDishCost());
        System.out.println(dish.getGrossMargin());
        System.out.println(dr.getGrossMargin(1));
    }
}