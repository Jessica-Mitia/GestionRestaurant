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

        DishOrder dishOrder = new DishOrder();
        dishOrder.setId(21);
        dishOrder.setQuantity(2);
        dishOrder.setDish(dr.findDishById(2));

        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(dishOrder);

        Order order = new Order();
        order.setId(21);
        order.setReference("ORD0002");
        order.setCreationDateTime(Instant.now());
        order.setDishOrders(dishOrders);

        System.out.println(dr.saveOrder(order));

    }

}