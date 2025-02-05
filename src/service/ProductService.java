package service;

import dao.ProductDAO;
import model.Product;
import util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }

    public List<Product> getAllProducts() {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            return productDAO.findAllProducts(conn);
        } catch (SQLException e) {
            System.out.println("Error fetching all products: " + e.getMessage());
            return List.of();
        }
    }
    public List<Product> getProductsByCategory(String categoryName) {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            return productDAO.findProductsByCategory(conn, categoryName);
        } catch (SQLException e) {
            System.out.println("Error fetching products by category: " + e.getMessage());
            return List.of();
        }
    }
    public List<Product> searchProducts(String searchTerm) {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            return productDAO.searchProduct(conn, searchTerm);
        } catch (SQLException e) {
            System.out.println("Error searching products: " + e.getMessage());
            return List.of();
        }
    }
}