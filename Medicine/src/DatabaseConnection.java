import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/medicines_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Ensure your MySQL root user password is correct
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");  // Explicitly load the MySQL driver
        } catch (ClassNotFoundException e) {
            // Logging the error instead of using e.printStackTrace()
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}