package dao;

import model.Product;
import util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Listar alla produkter från db och filtrering på kategori och söktermer
 */
public class ProductDAO {

    // Hämtar produkter baserat på kategori och lägger i en lista av produkter
    public List<Product> findProductsByCategory(String categoryName) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, p.size, p.color, p.brand, p.price, p.stock " +
                "FROM product p " +
                "JOIN product_category pc ON p.id = pc.product_id " +
                "JOIN category c ON pc.category_id = c.id " +
                "WHERE c.name = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, categoryName);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getInt("size"),
                            rs.getString("color"),
                            rs.getString("brand"),
                            rs.getInt("price"),
                            rs.getInt("stock")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching products by category: " + e.getMessage());
        }

        return products;
    }

    // Hittar alla produkter och lägger i en lista av produkter
    public List<Product> findAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, size, color, brand, price, stock FROM product";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("id"),
                        rs.getInt("size"),
                        rs.getString("color"),
                        rs.getString("brand"),
                        rs.getInt("price"),
                        rs.getInt("stock")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching all products: " + e.getMessage());
        }

        return products;
    }

    // Söker efter produkter genom brand, color, size eller price och lägger i lista av produkter
    public List<Product> searchProduct(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, size, color, brand, price, stock " +
                "FROM product " +
                "WHERE brand LIKE ? OR color LIKE ? OR CAST(size AS CHAR) LIKE ? OR CAST(price AS CHAR) LIKE ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Wildcard sökningar
            String queryTerm = "%" + searchTerm + "%";
            pstmt.setString(1, queryTerm);
            pstmt.setString(2, queryTerm);
            pstmt.setString(3, queryTerm);
            pstmt.setString(4, queryTerm);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getInt("id"),
                            rs.getInt("size"),
                            rs.getString("color"),
                            rs.getString("brand"),
                            rs.getInt("price"),
                            rs.getInt("stock")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
        }

        return products;
    }

}

