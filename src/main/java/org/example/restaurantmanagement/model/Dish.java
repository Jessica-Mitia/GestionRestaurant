package org.example.restaurantmanagement.model;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

public class Dish {
    private Integer id;
    private String name;
    private DishTypeEnum dishType;
    private List<DishIngredient> dishIngredients;
    private Double price;

    public Dish() {}

    public Dish(Integer id, String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients, Double price) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
        this.price = price;
    }

    public Dish (String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients) {
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
    }

    public Dish (String name, DishTypeEnum dishType, List<DishIngredient> dishIngredients, Double price) {
        this.name = name;
        this.dishType = dishType;
        this.dishIngredients = dishIngredients;
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

    public List<DishIngredient> getDishIngredients() {
        return dishIngredients;
    }

    public void setIngredients(List<DishIngredient> dishIngredients) {
        this.dishIngredients = dishIngredients;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDishCost()  {
        Double dishCost = 0.0;
        for (DishIngredient dishIngredient : dishIngredients) {
            dishCost += dishIngredient.getIngredient().getPrice() * dishIngredient.getQuantity();
        }
        return dishCost;
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
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(dishIngredients, dish.dishIngredients) && Objects.equals(price, dish.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, dishIngredients, price);
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dishType=" + dishType +
                ", dishIngredients=" + dishIngredients +
                ", price=" + price +
                '}';
    }
}
