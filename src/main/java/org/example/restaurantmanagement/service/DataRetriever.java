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
        try (Connection connection = db.getDBConnection()) {
            Dish dish = null;

            String sql = """
                    SELECT d.id as dish_id, d.name as dish_name, d.selling_price as dish_price, d.dish_type as dish_type
                    FROM dish d
                    WHERE d.id = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        dish = new Dish();
                        dish.setId(rs.getInt("dish_id"));
                        dish.setName(rs.getString("dish_name"));
                        dish.setPrice(rs.getObject("dish_price") == null ? null : rs.getDouble("dish_price"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                        dish.setDishIngredients(findDishIngredientByDishId(rs.getInt("dish_id")));
                    }
                }
            }
            return dish;
        }
    }

    public List<DishIngredient> findDishIngredientByDishId(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection connection = db.getDBConnection()) {
            List<DishIngredient> dishIngredients = new ArrayList<>();

            String sql = """
                    SELECT di.id as di_id, di.id_dish as di_id_dish, di.id_ingredient as di_id_ingredient,
                           di.quantity_required as di_quantity_required, di.unit as di_unit
                    FROM dish_ingredient di
                    WHERE di.id_dish = ?
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DishIngredient dishIngredient = new DishIngredient();
                        dishIngredient.setId(rs.getInt("di_id"));
                        dishIngredient.setIngredient(findIngredientById(rs.getInt("di_id_ingredient")));
                        dishIngredient.setQuantity(rs.getObject("di_quantity_required") == null ? null : rs.getDouble("di_quantity_required"));
                        dishIngredient.setUnitType(UnitTypeEnum.valueOf(rs.getString("di_unit")));
                        dishIngredients.add(dishIngredient);
                    }
                }
            }
            return dishIngredients;
        }
    }

    public List<StockMovement> findStockMovementByIngredientId(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection connection = db.getDBConnection()) {
            List<StockMovement> stockMovements = new ArrayList<>();

            String sql = """
                    SELECT id, id_ingredient, quantity, type, unit, creation_datetime
                    FROM stock_movement WHERE id_ingredient = ?
                    ORDER BY id ASC
                """;

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
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
    }

    public Ingredient findIngredientById(Integer id) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection connection = db.getDBConnection()) {
            String sql = "SELECT id, name, price, category FROM ingredient WHERE id = ?";

            Ingredient ingredient = null;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ingredient = new Ingredient();
                        ingredient.setId(rs.getInt("id"));
                        ingredient.setName(rs.getString("name"));
                        ingredient.setPrice(rs.getObject("price") == null ? null : rs.getDouble("price"));
                        ingredient.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                        ingredient.setStockMovementList(findStockMovementByIngredientId(id));
                    }
                }
            }
            return ingredient;
        }
    }

    private void deleteDishIngredients(Connection conn, Integer dishId) throws SQLException {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            ps.executeUpdate();
        }
    }

    private void insertDishIngredients(Connection conn, Integer dishId, List<DishIngredient> dishIngredients) throws SQLException {
        if (dishIngredients == null || dishIngredients.isEmpty()) return;

        String sql = """
                INSERT INTO dish_ingredient (id, id_dish, id_ingredient, quantity_required, unit)
                VALUES (?, ?, ?, ?, ?::unit_type)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (DishIngredient di : dishIngredients) {
                int id = di.getId() != null ? di.getId() : getNextSerialValue(conn, "dish_ingredient", "id");

                ps.setInt(1, id);
                ps.setInt(2, dishId);
                ps.setInt(3, di.getIngredient().getId());

                if (di.getQuantity() != null) ps.setDouble(4, di.getQuantity());
                else ps.setNull(4, Types.DOUBLE);

                ps.setString(5, di.getUnitType().name());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private String getSerialSequenceName(Connection conn, String tableName, String columnName) throws SQLException {
        String sql = "SELECT pg_get_serial_sequence(?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return null;
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) throws SQLException {
        String sequenceName = getSerialSequenceName(conn, tableName, columnName);
        if (sequenceName == null)
            throw new IllegalArgumentException("No sequence for " + tableName + "." + columnName);

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
        try (Connection conn = db.getDBConnection()) {
            conn.setAutoCommit(false);

            String upsertDishSql = """
                    INSERT INTO dish (id, selling_price, name, dish_type)
                    VALUES (?, ?, ?, ?::dish_type)
                    ON CONFLICT (id) DO UPDATE
                    SET selling_price = EXCLUDED.selling_price,
                        name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type
                """;

            try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                ps.setInt(1, toSave.getId());
                if (toSave.getPrice() != null) ps.setDouble(2, toSave.getPrice());
                else ps.setNull(2, Types.DOUBLE);
                ps.setString(3, toSave.getName());
                ps.setString(4, toSave.getDishType().name());
                ps.executeUpdate();

                deleteDishIngredients(conn, toSave.getId());
                insertDishIngredients(conn, toSave.getId(), toSave.getDishIngredients());

                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        }
        return findDishById(toSave.getId());
    }

    public Ingredient saveIngredient(Ingredient toSave) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {
            conn.setAutoCommit(false);

            String insertIngredientSQL = """
                INSERT INTO ingredient(id, name, price, category)
                VALUES (?, ?, ?, ?::category_enum)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    price = EXCLUDED.price,
                    category = EXCLUDED.category
            """;

            try (PreparedStatement ps = conn.prepareStatement(insertIngredientSQL)) {
                ps.setInt(1, toSave.getId());
                ps.setString(2, toSave.getName());
                if (toSave.getPrice() != null) ps.setDouble(3, toSave.getPrice());
                else ps.setNull(3, Types.DOUBLE);
                ps.setString(4, toSave.getCategory().name());
                ps.executeUpdate();
            }

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
                    ps.setString(4, m.getType().name());
                    ps.setString(5, m.getValue().getUnit().name());
                    ps.setTimestamp(6, Timestamp.from(m.getCreationDateTime()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return toSave;
    }

    private void saveDishOrder(Connection conn, DishOrder toSave, Integer orderId) throws SQLException {
        String sql = """
                INSERT INTO dish_order (id, id_order, id_dish, quantity)
                VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, toSave.getId());
            ps.setInt(2, orderId);
            ps.setInt(3, toSave.getDish().getId());
            ps.setInt(4, toSave.getQuantity());
            ps.executeUpdate();
        }
    }

    public List<DishOrder> findDishOrderByOrderId(Integer orderId) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {
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
                }
            }
            return dishOrders;
        }
    }

    public Order findOrderByReference(String reference) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {
            Order order = null;

            String sql = """
                    SELECT id, reference, creation_datetime FROM "order" WHERE reference = ?
                """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, reference);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        order = new Order();
                        order.setId(rs.getInt("id"));
                        order.setReference(rs.getString("reference"));
                        order.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());
                        order.setDishOrders(findDishOrderByOrderId(rs.getInt("id")));
                    }
                }
            }
            return order;
        }
    }


    public List<Table> findAllTables() throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {

            List<Table> tables = new ArrayList<>();

            String sql = "SELECT id, number FROM restaurant_table";

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Table t = new Table();
                    t.setId(rs.getInt("id"));
                    t.setNumber(rs.getInt("number"));
                    t.setOrders(findOrdersByTableId(t.getId()));
                    tables.add(t);
                }
            }
            return tables;
        }
    }

    public List<Order> findOrdersByTableId(Integer tableId) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {

            List<Order> orders = new ArrayList<>();

            String sql = """
            SELECT o.id, o.reference, o.creation_datetime,
                   t.arrival_datetime, t.departure_datetime
            FROM "order" o
            JOIN table_order t ON t.id_order = o.id
            WHERE t.id_table = ?
        """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, tableId);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Order o = new Order();
                        o.setId(rs.getInt("id"));
                        o.setReference(rs.getString("reference"));
                        o.setCreationDateTime(rs.getTimestamp("creation_datetime").toInstant());

                        TableOrder to = new TableOrder();
                        to.setArrivalDateTime(rs.getTimestamp("arrival_datetime").toInstant());
                        to.setDepartureDateTime(rs.getTimestamp("departure_datetime").toInstant());

                        o.setTable(to);
                        orders.add(o);
                    }
                }
            }
            return orders;
        }
    }


    public List<Table> findTablesWithOrdersAt(Instant t) throws SQLException {
        DBConnection db = new DBConnection();
        try (Connection conn = db.getDBConnection()) {

            String sql = """
            SELECT rt.id   AS table_id,
                   rt.number AS table_number,
                   to2.arrival_datetime,
                   to2.departure_datetime
            FROM restaurant_table rt
            LEFT JOIN table_order to2 ON to2.id_table = rt.id
        """;

            List<Table> tables = new ArrayList<>();

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Integer tableId = rs.getInt("table_id");

                    Table table = tables.stream()
                            .filter(t0 -> t0.getId().equals(tableId))
                            .findFirst()
                            .orElseGet(() -> {
                                Table nt = new Table();
                                nt.setId(tableId);
                                try {
                                    nt.setNumber(rs.getInt("table_number"));
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                                nt.setOrders(new ArrayList<>());
                                tables.add(nt);
                                return nt;
                            });

                    Timestamp a = rs.getTimestamp("arrival_datetime");
                    Timestamp d = rs.getTimestamp("departure_datetime");

                    if (a != null && d != null) {
                        TableOrder to = new TableOrder();
                        to.setArrivalDateTime(a.toInstant());
                        to.setDepartureDateTime(d.toInstant());

                        Order o = new Order();
                        o.setTable(to);

                        table.getOrders().add(o);
                    }
                }
            }
            return tables;
        }
    }

    public Order saveOrder(Order orderToSave) throws SQLException {
        DBConnection db = new DBConnection();

        try (Connection conn = db.getDBConnection()) {
            conn.setAutoCommit(false);
            Instant now = orderToSave.getCreationDateTime();
            Integer selectedTableId = orderToSave.getTableOrder().getTable().getId();

            List<Table> tables = findTablesWithOrdersAt(now);

            Table selected = tables.stream()
                    .filter(t -> t.getId().equals(selectedTableId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Table inconnue"));

            List<Table> unavailable = new ArrayList<>();
            List<Table> available = new ArrayList<>();

            for (Table t : tables) {
                if (t.isAvailableAt(now)) available.add(t);
                else unavailable.add(t);
            }

            if (available.isEmpty()) {
                throw new RuntimeException("Aucune table n'est disponible actuellement");
            }

            if (!selected.isAvailableAt(now)) {
                String used = unavailable.stream()
                        .map(t -> String.valueOf(t.getNumber()))
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");

                throw new RuntimeException(
                        "La table " + selected.getNumber() +
                                " est déjà utilisée. Tables occupées: " + used
                );
            }

            String sql = """
                INSERT INTO "order" (id, reference, creation_datetime)
                VALUES (?, ?, ?)
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderToSave.getId());
                ps.setString(2, orderToSave.getReference());
                ps.setTimestamp(3, Timestamp.from(orderToSave.getCreationDateTime()));
                ps.executeUpdate();
            }


            String tableSql = """
            INSERT INTO table_order(id, id_table, id_order, arrival_datetime, departure_datetime)
            VALUES (?, ?, ?, ?, ?)
        """;

            try (PreparedStatement ps = conn.prepareStatement(tableSql)) {
                ps.setInt(1, orderToSave.getTableOrder().getTable().getId());
                ps.setInt(2, selectedTableId);
                ps.setInt(3, orderToSave.getId());
                ps.setTimestamp(4, Timestamp.from(orderToSave.getTableOrder().getArrivalDateTime()));
                ps.setTimestamp(5, Timestamp.from(orderToSave.getTableOrder().getDepartureDateTime()));
                ps.executeUpdate();
            }

            for (DishOrder d : orderToSave.getDishOrders()) {
                saveDishOrder(conn, d, orderToSave.getId());
            }

            conn.commit();
            return orderToSave;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
