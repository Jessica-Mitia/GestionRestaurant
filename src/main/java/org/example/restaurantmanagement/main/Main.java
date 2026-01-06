package org.example.restaurantmanagement.main;

import org.example.restaurantmanagement.model.CategoryEnum;
import org.example.restaurantmanagement.model.Dish;
import org.example.restaurantmanagement.model.DishTypeEnum;
import org.example.restaurantmanagement.model.Ingredient;
import org.example.restaurantmanagement.service.DataRetriever;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever data = new DataRetriever();

        System.out.println("Find dish by ID");
        System.out.println(data.findDishById(1));
        System.out.println(data.findDishById(199));

        System.out.println("Find ingredients");
        System.out.println(data.findIngredients(2,2));
        System.out.println(data.findIngredients(3,5));

        System.out.println("Find dish by ingredient name");
        System.out.println(data.findIngredientByName("eur"));
    }
}
