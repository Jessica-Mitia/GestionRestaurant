import org.example.restaurantmanagement.db.DBConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DishTest {
    @Test
    void testDBConnection() throws SQLException {
        DBConnection db = new DBConnection();
        Connection con = db.getDBConnection();
        Assertions.assertNotNull(con);
        Assertions.assertFalse(con.isClosed());
    }
}
