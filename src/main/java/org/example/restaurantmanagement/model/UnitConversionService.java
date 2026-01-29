package org.example.restaurantmanagement.model;

import java.util.HashMap;
import java.util.Map;

public class UnitConversionService {
    private static final Map<String, Map<UnitTypeEnum, Double>> CONVERSIONS = new HashMap<>();

    static {
        Map<UnitTypeEnum, Double> tomate = new HashMap<>();
        tomate.put(UnitTypeEnum.KG, 1.0);
        tomate.put(UnitTypeEnum.PCS, 1.0 / 10.0);
        CONVERSIONS.put("Tomate", tomate);


        Map<UnitTypeEnum, Double> laitue = new HashMap<>();
        laitue.put(UnitTypeEnum.KG, 1.0);
        laitue.put(UnitTypeEnum.PCS, 1.0 / 2.0);
        CONVERSIONS.put("Laitue", laitue);


        Map<UnitTypeEnum, Double> chocolat = new HashMap<>();
        chocolat.put(UnitTypeEnum.KG, 1.0);
        chocolat.put(UnitTypeEnum.PCS, 1.0 / 10.0);
        chocolat.put(UnitTypeEnum.L, 1.0 / 2.5);
        CONVERSIONS.put("Chocolat", chocolat);


        Map<UnitTypeEnum, Double> poulet = new HashMap<>();
        poulet.put(UnitTypeEnum.KG, 1.0);
        poulet.put(UnitTypeEnum.PCS, 1.0 / 8.0);
        CONVERSIONS.put("Poulet", poulet);


        Map<UnitTypeEnum, Double> beurre = new HashMap<>();
        beurre.put(UnitTypeEnum.KG, 1.0);
        beurre.put(UnitTypeEnum.PCS, 1.0 / 4.0);
        beurre.put(UnitTypeEnum.L, 1.0 / 5.0);
        CONVERSIONS.put("Beurre", beurre);
    }

    public static double toKg(String ingredientName, double quantity, UnitTypeEnum unit) {
        Map<UnitTypeEnum, Double> map = CONVERSIONS.get(ingredientName);

        if (map == null || !map.containsKey(unit)) {
            throw new RuntimeException(
                    "Conversion impossible pour " + ingredientName + " avec unité " + unit
            );
        }

        return quantity * map.get(unit);
    }
}
