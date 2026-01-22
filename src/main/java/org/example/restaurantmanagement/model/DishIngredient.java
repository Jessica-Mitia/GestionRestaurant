package org.example.restaurantmanagement.model;

import java.util.Objects;

public class DishIngredient {
    private int id;
    private Dish dish;
    private Ingredient ingredient;
    private Double quantity;
    private UnitTypeEnum unitType;

    public DishIngredient() {}

    public DishIngredient(int id, Dish dish, Ingredient ingredient, Double quantity, UnitTypeEnum unitType) {
        this.id = id;
        this.dish = dish;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.unitType = unitType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public UnitTypeEnum getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitTypeEnum unitType) {
        this.unitType = unitType;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DishIngredient that = (DishIngredient) o;
        return id == that.id && Objects.equals(dish, that.dish) && Objects.equals(ingredient, that.ingredient) && Objects.equals(quantity, that.quantity) && unitType == that.unitType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dish, ingredient, quantity, unitType);
    }

    @Override
    public String toString() {
        return "DishIngredient{" +
                "id=" + id +
                ", dish=" + dish +
                ", ingredient=" + ingredient +
                ", quantity=" + quantity +
                ", unitType=" + unitType +
                '}';
    }
}
