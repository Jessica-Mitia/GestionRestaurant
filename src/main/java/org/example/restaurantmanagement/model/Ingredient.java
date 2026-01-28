package org.example.restaurantmanagement.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ingredient {
    private Integer id;
    private String name;
    private Double price;
    private CategoryEnum category;
    private List<StockMovement> stockMovementList;

    public Ingredient() {}

    public Ingredient(Integer id, String name, Double price, CategoryEnum category, List<StockMovement> stockMovementList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockMovementList = stockMovementList;
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

    public List<StockMovement> getStockMovementList() {
        if (stockMovementList == null) {
            stockMovementList = new ArrayList<>();
        }
        return stockMovementList;
    }

    public void setStockMovementList(List<StockMovement> stockMovementList) {
        this.stockMovementList = stockMovementList;
    }

    public double getStockValueAt(Instant t) {
        double stock = 0.0;
        for (StockMovement movement : stockMovementList) {
            if (!movement.getCreationDateTime().isAfter(t)) {
                if (movement.getType() == MovementTypeEnum.IN) {
                    stock += movement.getValue().getQuantity();
                }
                if (movement.getType() == MovementTypeEnum.OUT) {
                    stock -= movement.getValue().getQuantity();
                }
            }
        }
        return stock;
    }




    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(price, that.price) && category == that.category && Objects.equals(stockMovementList, that.stockMovementList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, category, stockMovementList);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", stockMovementList=" + stockMovementList +
                '}';
    }
}
