package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
* Öppnar upp anslutningar till databasen, hämtar uppgifter från properties fil
 */

public final class DbConnection {

    private static DbConnection instance;
    private String url;
    private String user;
    private String password;


    private DbConnection() {
        Properties props = new Properties();
        String path = "resources/db.properties";

        // Hämtar filen db.properties
        try (InputStream input = new FileInputStream(path)) {
            props.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Error loading db.properties file", e);
        }

        this.url = props.getProperty("DB_URL");
        this.user = props.getProperty("DB_USERNAME");
        this.password = props.getProperty("DB_PASSWORD");

    }

    // Singleton
    public static DbConnection getInstance() {
        if (instance == null) {
            synchronized (DbConnection.class) {
                if (instance == null) {
                    instance = new DbConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

}
