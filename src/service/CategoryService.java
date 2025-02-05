package service;

import dao.CategoryDAO;
import model.Category;
import util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public List<Category> findAllCategories() {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            // Använder bara en select så vi kan använda autoCommit som standard aka true
            return categoryDAO.findAllCategories(conn);
        } catch (SQLException e) {
            System.out.println("Error fetching categories: " + e.getMessage());
            return List.of();
        }
    }
}