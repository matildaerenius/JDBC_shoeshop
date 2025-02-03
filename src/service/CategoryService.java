package service;

import dao.CategoryDAO;
import model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public List<Category> findAllCategories() {
        return categoryDAO.findAllCategories();
    }
}
