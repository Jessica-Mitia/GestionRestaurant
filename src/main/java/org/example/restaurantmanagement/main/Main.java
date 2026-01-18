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
        DataRetriever dr = new DataRetriever();

        System.out.println(dr.findDishById(1).getGrossMargin());
        System.out.println(dr.findDishById(2).getGrossMargin());
        //System.out.println(dr.findDishById(3).getGrossMargin());
        System.out.println(dr.findDishById(4).getGrossMargin());
        //System.out.println(dr.findDishById(5).getGrossMargin());
    }
}
