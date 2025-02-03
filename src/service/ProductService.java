package service;

import dao.ProductDAO;
import model.Product;

import java.util.List;

public class ProductService {
    private final ProductDAO productDAO;

    public ProductService() {
        this.productDAO = new ProductDAO();
    }
    public List<Product> getAllProducts() {
        return productDAO.findAllProducts();
    }
    public List<Product> getProductsByCategory(String categoryName) {
        return productDAO.findProductsByCategory(categoryName);
    }
    public List<Product> searchProducts(String searchTerm) {
        return productDAO.searchProduct(searchTerm);
    }
}
