package dao;

import model.Category;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
* Klass för att hämta alla kategorier från databasen
 */

public class CategoryDAO {
    public List<Category> findAllCategories(Connection conn) throws SQLException {
        List<Category> categoryNames  = new ArrayList<>();
        String sql = "SELECT name FROM category";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Kör igenom resultatet och skapar category objekt
            while (rs.next()) {
                String name = rs.getString("name");
                categoryNames.add(new Category(name));
            }
        }
        return categoryNames;
    }
}