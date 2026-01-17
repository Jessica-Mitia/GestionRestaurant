package org.example.restaurantmanagement.model;

import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private Dish dish;
    private Double quantity;

    public Ingredient() {}

    public Ingredient(Integer id, String name, Double price, CategoryEnum category, Dish dish, Double quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.dish = dish;
        this.quantity = quantity;
    }

    public Ingredient(String name, double price, CategoryEnum category) {
        this.name = name;
        this.price = price;
        this.category = category;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        this.category = category;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Double getQuantity() { return quantity; }

    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public String getDishName() {
        if (dish == null) {
            return "";
        }
        return dish.getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(price, that.price) && category == that.category && Objects.equals(dish, that.dish);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, dish);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", quantity=" + quantity +
                ", dishName=" + this.getDishName()+
                '}';
    }
}
