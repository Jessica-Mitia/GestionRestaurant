package org.example.restaurantmanagement.service;

import org.example.restaurantmanagement.db.DBConnection;
import org.example.restaurantmanagement.model.CategoryEnum;
import org.example.restaurantmanagement.model.Dish;
import org.example.restaurantmanagement.model.DishTypeEnum;
import org.example.restaurantmanagement.model.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataRetriever {
    public Dish findDishById(Integer id) throws SQLException {

        Dish dish = null;
        List<Ingredient> ingredients = new ArrayList<>();
        DBConnection db = new DBConnection();

        StringBuilder sql = new StringBuilder(
                """
                    SELECT d.id AS d_id, d.name AS d_name, d.dish_type AS d_type,
                    i.id AS i_id, i.name AS i_name, i.price AS i_price, i.category AS i_category
                    FROM dish d
                    LEFT JOIN ingredient i ON d.id = i.dish_id
                    WHERE d.id = ?
                """
        );

        try (
                Connection con = db.getDBConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    if (dish == null) {
                        dish = new Dish(
                                rs.getInt("d_id"),
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                ingredients
                        );
                    }

                    if (rs.getObject("i_id") != null) {
                        Ingredient ingredient = new Ingredient(
                                rs.getInt("i_id"),
                                rs.getString("i_name"),
                                rs.getDouble("i_price"),
                                CategoryEnum.valueOf(rs.getString("i_category")),
                                dish
                        );
                        ingredients.add(ingredient);
                    }
                }
            }  catch (SQLException e) {
                throw new RuntimeException("SQLException: " + e.getMessage());
            }
        }

        return dish;
    }


    public List<Ingredient> findIngredients(int page, int size) throws SQLException {

        List<Ingredient> ingredients = new ArrayList<>();
        int offset = (page - 1) * size;
        DBConnection db = new DBConnection();

        StringBuilder sql = new StringBuilder(
                            """
                                SELECT i.id            AS i_id,
                                        i.name          AS i_name,
                                        i.price         AS i_price,
                                        i.category      AS i_category,
                                        d.id            AS d_id,
                                        d.name          AS d_name,
                                        d.dish_type     AS d_type
                                FROM ingredient i
                                LEFT JOIN dish d ON i.dish_id = d.id
                                LIMIT ? OFFSET ?
                             """
        );

        try (
                Connection con = db.getDBConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            ps.setInt(1, size);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Dish dish = null;
                    if (rs.getObject("d_id") != null) {
                        dish = new Dish(
                                rs.getInt("d_id"),
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                new ArrayList<>()
                        );
                    }

                    Ingredient ingredient = new Ingredient(
                            rs.getInt("i_id"),
                            rs.getString("i_name"),
                            rs.getDouble("i_price"),
                            CategoryEnum.valueOf(rs.getString("i_category")),
                            dish
                    );

                    ingredients.add(ingredient);
                }
            }
        }

        return ingredients;
    }


    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {
        DBConnection db = new DBConnection();

        String checkSql = "SELECT 1 FROM ingredient WHERE name = ?";
        String insertSql = "INSERT INTO ingredient(name, price, category) VALUES (?, ?, ?::category_enum)";

        List<Ingredient> savedIngredients = new ArrayList<>();

        try (Connection con = db.getDBConnection()){
            con.setAutoCommit(false);

            try (PreparedStatement checkPs = con.prepareStatement(checkSql);
            PreparedStatement insertPs = con.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                for (Ingredient ingredient : newIngredients) {
                    checkPs.setString(1, ingredient.getName());
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            throw new RuntimeException(
                                    "This ingredient already exists: " + ingredient.getName()
                            );
                        }
                    }

                    insertPs.setString(1, ingredient.getName());
                    insertPs.setDouble(2, ingredient.getPrice());
                    insertPs.setString(3,ingredient.getCategory().name());
                    insertPs.executeUpdate();

                    try (ResultSet keys = insertPs.getGeneratedKeys()) {
                        if (keys.next()) {
                            ingredient.setId(keys.getInt(1));
                        }
                    }
                    savedIngredients.add(ingredient);
                }

                con.commit();
                return savedIngredients;
            } catch (RuntimeException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Dish saveDish(Dish dishToSave) {
        DBConnection db = new DBConnection();

        try (Connection con = db.getDBConnection()) {
            con.setAutoCommit(false);

            Integer dishId = dishToSave.getId();

            if (dishId == null || dishId == 0) {
                StringBuilder insertDish = new StringBuilder(
                        """
                            INSERT INTO dish(name, dish_type)
                            VALUES (?, ?::dish_type_enum)
                            RETURNING id
                        """
                );

                try (PreparedStatement ps = con.prepareStatement(insertDish.toString())) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());

                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        dishId = rs.getInt("id");
                        dishToSave.setId(dishId);
                    }
                }

                if (dishId == null) {
                    throw new RuntimeException("Impossible de récupérer l'id du nouveau plat");
                }

            } else {
                StringBuilder updateDish = new StringBuilder(
                        """
                            UPDATE dish
                            SET name = ?, dish_type = ?::dish_type_enum
                            WHERE id = ?
                        """
                );

                try (PreparedStatement ps = con.prepareStatement(updateDish.toString())) {
                    ps.setString(1, dishToSave.getName());
                    ps.setString(2, dishToSave.getDishType().name());
                    ps.setInt(3, dishId);
                    ps.executeUpdate();
                }
            }

            StringBuilder detachIngredients = new StringBuilder(
                    """
                        UPDATE ingredient
                        SET dish_id = NULL
                        WHERE dish_id = ?
                    """
            );

            try (PreparedStatement ps = con.prepareStatement(detachIngredients.toString())) {
                ps.setInt(1, dishId);
                ps.executeUpdate();
            }

            StringBuilder attachIngredient = new StringBuilder(
                    """
                        UPDATE ingredient
                        SET dish_id = ?
                        WHERE id = ?
                    """
            );

            try (PreparedStatement ps = con.prepareStatement(attachIngredient.toString())) {
                for (Ingredient ingredient : dishToSave.getIngredients()) {
                    ps.setInt(1, dishId);
                    ps.setInt(2, ingredient.getId());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            con.commit();
            return dishToSave;

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du saveDish", e);
        }
    }



    public List<Dish> findDishsByIngredientName(String ingredientName) throws SQLException {
        DBConnection db = new DBConnection();
        Map<Integer, Dish> dishMap = new HashMap<>();

        StringBuilder sql = new StringBuilder(
                """
                    SELECT d.id AS d_id, d.name AS d_name, d.dish_type AS d_type,
                    i.id AS i_id, i.name AS i_name, i.price AS i_price, i.category AS i_category
                    FROM dish d
                    LEFT JOIN ingredient i ON d.id = i.dish_id
                    WHERE d.id IN (
                        SELECT dish_id
                        FROM ingredient
                        WHERE name ILIKE ?
                    )
                    ORDER BY d.id
                """
            );

        try (
                Connection con = db.getDBConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            ps.setString(1, "%" + ingredientName + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    int dishId = rs.getInt("d_id");

                    Dish dish = dishMap.get(dishId);
                    if (dish == null) {
                        dish = new Dish(
                                dishId,
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                new ArrayList<>()
                        );
                        dishMap.put(dishId, dish);
                    }

                    if (rs.getObject("i_id") != null) {
                        Ingredient ingredient = new Ingredient(
                                rs.getInt("i_id"),
                                rs.getString("i_name"),
                                rs.getDouble("i_price"),
                                CategoryEnum.valueOf(rs.getString("i_category")),
                                dish
                        );
                        dish.getIngredients().add(ingredient);
                    }
                }
            }
        }

        return new ArrayList<>(dishMap.values());
    }



    public List<Ingredient> findIngredientsByCriteria (String ingredientName, CategoryEnum category, String dishName, int page, int size) throws SQLException {
        DBConnection db = new DBConnection();
        StringBuilder sql = new StringBuilder(
                """
                SELECT i.id AS i_id, i.name AS i_name, i.price AS i_price, i.category AS i_category,
                d.id AS d_id, d.name AS d_name, d.dish_type AS d_type
                FROM ingredient i
                JOIN dish d ON i.dish_id = d.id
                WHERE 1=1
                """
        );
        int offset = (page - 1) * size;
        List<Object> params = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isBlank()) {
            sql.append(" AND i.name ILIKE ?");
            params.add("%" + ingredientName + "%");
        }

        if (category != null) {
            sql.append(" AND i.category = ?::category_enum");
            params.add(category.name());
        }

        if (dishName != null && !dishName.isBlank()) {
            sql.append(" AND d.name ILIKE ?");
            params.add("%" + dishName + "%");
        }

        sql.append(" ORDER BY i.id LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (
                Connection con = db.getDBConnection();
                PreparedStatement ps = con.prepareStatement(sql.toString())
        ) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    Dish dish = null;
                    if (rs.getObject("d_id") != null) {
                        dish = new Dish(
                                rs.getInt("d_id"),
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                new ArrayList<>()
                        );
                    }

                    Ingredient ingredient = new Ingredient(
                            rs.getInt("i_id"),
                            rs.getString("i_name"),
                            rs.getDouble("i_price"),
                            CategoryEnum.valueOf(rs.getString("i_category")),
                            dish
                    );

                    ingredients.add(ingredient);
                }
            }
        }
        return ingredients;
    }


    public Ingredient findIngredientByName(String name) throws SQLException {
        DBConnection db = new DBConnection();
        StringBuilder sql = new StringBuilder(
                """
                    SELECT i.id as i_id,i.name as i_name,i.price as i_price,i.category as i_category, i.dish_id as i_dish_id,
                    d.id as d_id, d.name as d_name, d.dish_type as d_type
                    from ingredient i
                    left join dish d on i.dish_id = d.id
                    where i.name ILIKE ?
                """
        );
        Ingredient ingredient = null;

        try (Connection con = db.getDBConnection();
        PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Dish dish = null;
                    if (rs.getObject("d_id") != null) {
                        dish = new Dish(
                                rs.getInt("d_id"),
                                rs.getString("d_name"),
                                DishTypeEnum.valueOf(rs.getString("d_type")),
                                new ArrayList<>()
                        );
                    }

                    ingredient = new Ingredient(
                            rs.getInt("i_id"),
                            rs.getString("i_name"),
                            rs.getDouble("i_price"),
                            CategoryEnum.valueOf(rs.getString("i_category")),
                            dish
                    );
                }
                return ingredient;
            }
        }
    }
}

