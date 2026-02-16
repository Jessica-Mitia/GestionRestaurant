package org.example.restaurantmanagement.service;

import org.example.restaurantmanagement.db.DBConnection;
import org.example.restaurantmanagement.model.*;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    public Dish findDishById(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        Connection connection = db.getDBConnection();
        Dish dish = new Dish();

        String sql = """
                    SELECT d.id as dish_id, d.name as dish_name, d.selling_price as dish_price, d.dish_type as dish_type
                    FROM dish d
                    WHERE d.id = ?
                """;

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dish.setId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("dish_name"));
                    dish.setPrice(rs.getObject("dish_price") == null
                            ? null : rs.getDouble("dish_price"));
                    dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                    dish.setDishIngredients(findDishIngredientByDishId(rs.getInt("dish_id")));
                }
            }
        }

        return dish;
    };

    public List<DishIngredient> findDishIngredientByDishId(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        Connection connection = db.getDBConnection();

        List<DishIngredient> dishIngredients = new ArrayList<DishIngredient>();

        String sql = """
                    SELECT di.id as di_id, di.id_dish as di_id_dish, di.id_ingredient as di_id_ingredient, 
                    di.quantity_required as di_quantity_required, di.unit as di_unit
                    FROM dish_ingredient di
                    WHERE di.id_dish = ?
                """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setId(rs.getInt("di_id"));
                    dishIngredient.setIngredient(findIngredientById(rs.getInt("di_id_ingredient")));
                    dishIngredient.setQuantity(rs.getObject("di_quantity_required") == null ?
                            null : rs.getDouble("di_quantity_required"));
                    dishIngredient.setUnitType(UnitTypeEnum.valueOf(rs.getString("di_unit")));
                    dishIngredients.add(dishIngredient);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dishIngredients;
    };

    public List<StockMovement> findStockMovementByIngredientId(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        Connection connection = db.getDBConnection();
        List<StockMovement> stockMovements = new ArrayList<>();

        String sql = """
                    SELECT id, id_ingredient, quantity, type, unit, creation_datetime
                    FROM stock_movement where id_ingredient = ?
                    ORDER BY id ASC
                """;

        try(PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockMovement stockMovement = new StockMovement();
                    stockMovement.setId(rs.getInt("id"));
                    stockMovement.setType(MovementTypeEnum.valueOf(rs.getString("type")));
                    stockMovement.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());

                    StockValue stockValue = new StockValue();
                    stockValue.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));
                    stockValue.setQuantity(rs.getDouble("quantity"));

                    stockMovement.setValue(stockValue);

                    stockMovements.add(stockMovement);
                }
            }
        }
        return stockMovements;
    }

    public Ingredient findIngredientById(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        Connection connection = db.getDBConnection();

        String sql = "select id, name, price as price, category from ingredient where id = ?";

        Ingredient ingredient = new Ingredient();
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            try {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));
                    ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ingredient.setStockMovementList(findStockMovementByIngredientId(id));
                }
            }catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredient;
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(
            Connection conn,
            Integer dishId,
            List<DishIngredient> dishIngredients
    ) throws SQLException {

        if (dishIngredients == null || dishIngredients.isEmpty()) return;

        String sql = """
        INSERT INTO dish_ingredient
        (id, id_dish, id_ingredient, quantity_required, unit)
        VALUES (?, ?, ?, ?, ?::unit_type)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {

                int id = di.getId() != null
                        ? di.getId()
                        : getNextSerialValue(conn, "dish_ingredient", "id");

                ps.setInt(1, id);
                ps.setInt(2, dishId);
                ps.setInt(3, di.getIngredient().getId());

                if (di.getQuantity() != null) {
                    ps.setDouble(4, di.getQuantity());
                } else {
                    ps.setNull(4, Types.DOUBLE);
                }

                ps.setString(5, di.getUnitType().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sql = "SELECT pg_get_serial_sequence(?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName)
            throws SQLException {

        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null) {
            throw new IllegalArgumentException(
                    "Any sequence found for " + tableName + "." + columnName
            );
        }
        updateSequenceNextValue(conn, tableName, columnName, sequenceName);

        String nextValSql = "SELECT nextval(?)";

        try (PreparedStatement ps = conn.prepareStatement(nextValSql)) {
            ps.setString(1, sequenceName);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private void updateSequenceNextValue(Connection conn, String tableName, String columnName, String sequenceName) throws SQLException {
        String setValSql = String.format(
                "SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                sequenceName, columnName, tableName
        );

        try (PreparedStatement ps = conn.prepareStatement(setValSql)) {
            ps.executeQuery();
        }
    }

    public Dish saveDish(Dish toSave) throws SQLException {
        DBConnection db = new DBConnection();
        Connection conn = db.getDBConnection();

        String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET name = EXCLUDED.name,
                    dish_type = EXCLUDED.dish_type
                    RETURNING id
                """;

        int dishId = toSave.getId();

        try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
            conn.setAutoCommit(false);
            ps.setInt(1, dishId);

            deleteDishIngredients(conn, dishId);

            insertDishIngredients(conn, dishId, toSave.getDishIngredients());
        }

        conn.commit();
        return findDishById(dishId);
    }

    public Ingredient saveIngredient(Ingredient toSave) {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {
            String insertIngredientSQL = """
            INSERT INTO ingredient(id, name, price, category)
            VALUES (?, ?, ?, ?::category_enum)
            ON CONFLICT (id) DO UPDATE SET name = EXCLUDED.name, price = EXCLUDED.price, category = EXCLUDED.category
        """;

            try (PreparedStatement ps = conn.prepareStatement(insertIngredientSQL)) {
                ps.setInt(1, toSave.getId());
                ps.setString(2, toSave.getName());
                ps.setDouble(3, toSave.getPrice());
                ps.setString(4, toSave.getCategory().toString());
                ps.executeUpdate();
            }

            conn.setAutoCommit(false);

            String insertMovementSQL = """
            INSERT INTO stock_movement(id, id_ingredient, quantity, type, unit, creation_datetime)
            VALUES (?, ?, ?, ?::mouvement_type, ?::unit_type, ?)
            ON CONFLICT (id) DO NOTHING
        """;

            try (PreparedStatement ps = conn.prepareStatement(insertMovementSQL)) {
                for (StockMovement m : toSave.getStockMovementList()) {
                    ps.setInt(1, m.getId());
                    ps.setInt(2, toSave.getId());
                    ps.setDouble(3, m.getValue().getQuantity());
                    ps.setString(4, m.getType().toString());
                    ps.setString(5, m.getValue().getUnit().toString());
                    ps.setTimestamp(6, Timestamp.from(m.getCreationDateTime()));
                    ps.executeUpdate();

                    conn.commit();
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return toSave;
    }

    public Order saveOrder(Order orderToSave) throws SQLException {
        DBConnection db = new DBConnection();
        Connection conn = db.getDBConnection();

        String sql = """
                    INSERT INTO "order" (id, reference, creation_datetime)
                    VALUES (?, ?, ?)
                """;

        return orderToSave;
    }

    public DishOrder saveDishOrder(DishOrder toSave) throws SQLException {
        DBConnection db = new DBConnection();
        Connection conn = db.getDBConnection();

        String sql = """
                    INSERT INTO dish_order (id, id_dish, quantity)
                    VALUES (?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toSave.getId());
            ps.setInt(2, toSave.getDish().getId());
            ps.setInt(3, toSave.getQuantity());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return toSave;
    }

    public List<DishOrder> findDishOrderByOrderId(Integer orderId) throws SQLException {
        DBConnection db = new DBConnection();
        Connection conn = db.getDBConnection();
        List<DishOrder> dishOrders = new ArrayList<>();

        String sql = """
                    SELECT id, id_order, id_dish, quantity
                    FROM dish_order WHERE id_order = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setId(rs.getInt("id"));
                    dishOrder.setDish(findDishById(rs.getInt("id_dish")));
                    dishOrder.setQuantity(rs.getInt("quantity"));
                    dishOrders.add(dishOrder);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return dishOrders;
    }

    public Order findOrderByReference(String reference) throws SQLException {
        DBConnection db = new DBConnection();
        Connection conn = db.getDBConnection();
        Order order = new Order();

        String sql = """
                    SELECT id, reference, creation_datetime FROM "order" WHERE reference = ?
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reference);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                order.setId(rs.getInt("id"));
                order.setReference(rs.getString("reference"));
                order.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());
                order.setDishOrders(findDishOrderByOrderId(rs.getInt("id")));
            }
        }

        return order;
    }


    public StockValue getStockValueAt (Instant t, Integer ingredientIdentifier) throws SQLException {
        DBConnection db = new DBConnection();
        String sql = """
                    SELECT
                       unit,
                       SUM(
                           CASE
                    	       WHEN type = 'OUT' THEN quantity * -1
                                   ELSE quantity
                                   END
                           ) AS actual_quantity
                    FROM stock_movement
                    WHERE id_ingredient = ?
                    AND creation_datetime <= ?
                    GROUP BY id_ingredient, unit
                """;

        StockValue stockValue = new StockValue();

        try (Connection conn = db.getDBConnection();){
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, ingredientIdentifier);
            ps.setTimestamp(2, Timestamp.from(t));

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                stockValue.setUnit(UnitTypeEnum.valueOf(rs.getString("unit")));

                Double quantity =
                        rs.getObject("actual_quantity") == null
                                ? 0.0
                                : rs.getDouble("actual_quantity");

                stockValue.setQuantity(quantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return stockValue;
    }

    public Double getDishCost (Integer dishId) throws SQLException {
        DBConnection db = new DBConnection();
        String sql = """
                    select dish.id, dish.name, ROUND(sum(ingredient.price * di.quantity_required), 2) AS dishCost
                    FROM dish_ingredient AS di
                    LEFT JOIN dish ON di.id_dish = dish.id
                    LEFT JOIN ingredient ON di.id_ingredient = ingredient.id
                    WHERE dish.id = ?
                    GROUP BY dish.id
                """;

        try (Connection conn = db.getDBConnection();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble("dishCost");
            }
        }

        return null;
    }


    public Double getGrossMargin (Integer dishId) throws SQLException {
        DBConnection db = new DBConnection();
        String sql = "SELECT dish.selling_price FROM dish WHERE dish.id = ?";

        Double grossMargin = null;

        try (Connection conn = db.getDBConnection();) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, dishId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Double sellingPrice = rs.getDouble("selling_price");
                grossMargin = sellingPrice - getDishCost(dishId);
            }
        }

        return grossMargin;
    }
}