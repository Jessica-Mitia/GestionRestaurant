package org.example.restaurantmanagement.model;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;
    private Double price;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.price = price;
    }

    public Dish (String name, DishTypeEnum dishType, List<Ingredient> ingredients) {
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
    }

    public Dish (String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double price) {
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDishCost() {
        Double cost = 0.0;
        for (Ingredient ingredient : ingredients) {
            cost += ingredient.getPrice() * ingredient.getQuantity();
        }
        return cost;
    }

    public Double getGrossMargin() {
        if (price == null) {
            throw new RuntimeException(
                    "Le prix de vente n'est pas défini, impossible de calculer la marge"
            );
        }

        return price - this.getDishCost();
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id == dish.id && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", price=" + price +
                ", ingredients=" + ingredients +
                '}';
    }
}
