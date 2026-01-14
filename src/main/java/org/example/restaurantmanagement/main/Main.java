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

        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient i1 = data.findIngredientByName("Chocolat");
        Ingredient i2 = data.findIngredientByName("Beure");
        ingredients.add(i2);

        Dish dish = new Dish(10,"Pain au raisin", DishTypeEnum.DESSERT, ingredients, 4000.0);

        System.out.println("Find dish by id");
        System.out.println(data.findDishById(1));
        System.out.println(data.findDishById(1).getGrossMargin());
        System.out.println(data.findDishById(5).getGrossMargin());

        //System.out.println("Save dish");
        //System.out.println(data.saveDish(dish));
    }
}
