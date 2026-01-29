package org.example.restaurantmanagement.model;

import java.util.List;

public class StockService {
    public static double computeFinalStockInKg(Ingredient ingredient) {
        double stockKg = 0.0;

        for (StockMovement m : ingredient.getStockMovementList()) {
            double qtyInKg = UnitConversionService.toKg(
                    ingredient.getName(),
                    m.getValue().getQuantity(),
                    m.getValue().getUnit()
            );

            if (m.getType() == MovementTypeEnum.IN) {
                stockKg += qtyInKg;
            } else if (m.getType() == MovementTypeEnum.OUT) {
                stockKg -= qtyInKg;
            }
        }

        return stockKg;
    }
}
