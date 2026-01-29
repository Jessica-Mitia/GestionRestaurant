package org.example.restaurantmanagement.model;

import java.util.List;

public class StockTestMain {
    public static void main(String[] args) {
        Ingredient tomate = new Ingredient();
        tomate.setName("Tomate");
        tomate.setStockMovementList(List.of(
                new StockMovement(null, new StockValue(4.0, UnitTypeEnum.KG), MovementTypeEnum.IN, null),

                new StockMovement(null, new StockValue(5.0, UnitTypeEnum.PCS), MovementTypeEnum.OUT, null)
        ));
        double finalTomate = StockService.computeFinalStockInKg(tomate);
        System.out.println("Stock final Tomate = " + finalTomate + " KG");


        Ingredient laitue = new Ingredient();
        laitue.setName("Laitue");
        laitue.setStockMovementList(List.of(
                new StockMovement(null, new StockValue(5.0, UnitTypeEnum.KG), MovementTypeEnum.IN, null),
                new StockMovement(null, new StockValue(2, UnitTypeEnum.PCS), MovementTypeEnum.OUT, null)
        ));
        double finalLaitue = StockService.computeFinalStockInKg(laitue);
        System.out.println("Stock final Laitue = " + finalLaitue + " KG");

        Ingredient poulet = new Ingredient();
        poulet.setName("Poulet");
        poulet.setStockMovementList(List.of(
                new StockMovement(null, new StockValue(10.0, UnitTypeEnum.KG), MovementTypeEnum.IN, null),
                new StockMovement(null, new StockValue(4, UnitTypeEnum.PCS), MovementTypeEnum.OUT, null)
        ));
        double finalPoulet = StockService.computeFinalStockInKg(poulet);
        System.out.println("Stock final Poulet = " + finalPoulet + " KG");

        Ingredient chocolat = new Ingredient();
        chocolat.setName("Chocolat");
        chocolat.setStockMovementList(List.of(
                new StockMovement(null, new StockValue(3.0, UnitTypeEnum.KG), MovementTypeEnum.IN, null),
                new StockMovement(null, new StockValue(1, UnitTypeEnum.L), MovementTypeEnum.OUT, null)
        ));
        double finalChocolat = StockService.computeFinalStockInKg(chocolat);
        System.out.println("Stock final Chocolat = " + finalChocolat + " KG");

        Ingredient beurre = new Ingredient();
        beurre.setName("Beurre");
        beurre.setStockMovementList(List.of(
                new StockMovement(null, new StockValue(2.5, UnitTypeEnum.KG), MovementTypeEnum.IN, null),
                new StockMovement(null, new StockValue(1, UnitTypeEnum.L), MovementTypeEnum.OUT, null)
        ));
        double finalBeurre = StockService.computeFinalStockInKg(beurre);
        System.out.println("Stock final Beurre = " + finalBeurre + " KG");
    }
}
