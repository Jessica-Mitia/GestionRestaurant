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
        Dish dish = new Dish();
        Ingredient ingredient = new Ingredient();
        StockValue stockValue = new StockValue();
        StockMovement stockMovement = new StockMovement();

        List<DishIngredient> dishIngredients = new ArrayList<>();
        List<StockMovement> stockMovements = new ArrayList<>();

        stockValue.setUnit(UnitTypeEnum.KG);
        stockValue.setQuantity(4.0);

        stockMovement.setId(11);
        stockMovement.setType(MovementTypeEnum.IN);
        stockMovement.setValue(stockValue);
        stockMovement.setCreationDateTime(Instant.now());

        stockMovements.add(stockMovement);

        dish.setId(12);
        dish.setName("Vary misy loka");
        dish.setPrice(1800.00);
        dish.setDishType(DishTypeEnum.MAIN);
        dish.setDishIngredients(dishIngredients);

        ingredient.setId(8);
        ingredient.setName("Sel de mer");
        ingredient.setPrice(300.00);
        ingredient.setCategory(CategoryEnum.DAIRY);
        ingredient.setStockMovementList(stockMovements);

        DishOrder dishOrder = new DishOrder();
        dishOrder.setId(2);
        dishOrder.setDish(dr.findDishById(2));
        dishOrder.setQuantity(2);

        List<DishOrder> dishOrders = new ArrayList<>();
        dishOrders.add(dishOrder);

        Order order = new Order();
        order.setId(2);
        order.setReference("ORD00003");
        order.setCreationDateTime(Instant.now());
        order.setDishOrders(dishOrders);


        System.out.println(dr.findOrderByReference("ORD00001"));
    }

}