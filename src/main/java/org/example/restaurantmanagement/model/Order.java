package org.example.restaurantmanagement.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class Order {
    private Integer id;
    private String reference;
    private Instant creationDateTime;
    private List<DishOrder> dishOrders;
    private TableOrder table;

    public Order() {}

    public Order(List<DishOrder> dishOrders, Instant creationDateTime, String reference, Integer id, TableOrder table) {
        this.dishOrders = dishOrders;
        this.creationDateTime = creationDateTime;
        this.reference = reference;
        this.id = id;
        this.table = table;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(Instant creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }

    public TableOrder getTableOrder() {
        return table;
    }

    public void setTable(TableOrder table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(reference, order.reference) && Objects.equals(creationDateTime, order.creationDateTime) && Objects.equals(dishOrders, order.dishOrders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, creationDateTime, dishOrders);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", creationDateTime=" + creationDateTime +
                ", dishOrders=" + dishOrders +
                '}';
    }

    public Double getTotalAmountWithoutVAT() {
        Double totalAmount = 0.0;
        for (DishOrder dishOrder : dishOrders) {
            totalAmount += dishOrder.getDish().getDishCost() * dishOrder.getQuantity();
        }
        return totalAmount;
    }

    public Double getTotalAmountWithVAT() {
        return getTotalAmountWithoutVAT() + ((getTotalAmountWithoutVAT() * 20) / 100);
    }

    public Double getIngredientQuantity(Ingredient ingredient) {
        Double quantity = 0.0;
        for (DishOrder dishOrder : dishOrders) {
            dishOrder.getDish().getDishIngredients().stream().filter(dishIngredient -> dishIngredient.getIngredient().equals(ingredient));{
                quantity += dishOrder.getQuantity();
            }
        }
        return quantity;
    }
}
