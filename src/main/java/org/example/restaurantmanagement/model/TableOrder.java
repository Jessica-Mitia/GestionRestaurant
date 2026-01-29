package org.example.restaurantmanagement.model;

import java.time.Instant;
import java.util.Objects;

public class TableOrder {
    Table table;
    Instant arrivalDateTime;
    Instant departureDateTime;

    public TableOrder() {}

    public TableOrder(Table table, Instant arrivalDateTime, Instant departureDateTime) {
        this.table = table;
        this.arrivalDateTime = arrivalDateTime;
        this.departureDateTime = departureDateTime;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Instant getArrivalDateTime() {
        return arrivalDateTime;
    }

    public void setArrivalDateTime(Instant arrivalDateTime) {
        this.arrivalDateTime = arrivalDateTime;
    }

    public Instant getDepartureDateTime() {
        return departureDateTime;
    }

    public void setDepartureDateTime(Instant departureDateTime) {
        this.departureDateTime = departureDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TableOrder that = (TableOrder) o;
        return Objects.equals(table, that.table) && Objects.equals(arrivalDateTime, that.arrivalDateTime) && Objects.equals(departureDateTime, that.departureDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, arrivalDateTime, departureDateTime);
    }

    @Override
    public String toString() {
        return "TableOrder{" +
                "table=" + table +
                ", arrivalDateTime=" + arrivalDateTime +
                ", departureDateTime=" + departureDateTime +
                '}';
    }
}
