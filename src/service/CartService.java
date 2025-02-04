package service;

import dao.CartDAO;
import dao.OrderDAO;
import model.CartItem;
import util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CartService {
    private final CartDAO cartDAO;
    private final OrderDAO orderDAO;
    private final DbConnection dbConnection;

    public CartService() {
        this.cartDAO = new CartDAO();
        this.orderDAO = new OrderDAO();
        this.dbConnection = DbConnection.getInstance();
    }

    public List<CartItem> getCartItems(int customerId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // Startar transaktionen

            try {
                Integer activeOrderId = orderDAO.getActiveOrderId(conn, customerId);
                if (activeOrderId == null) {
                    conn.commit();
                    return List.of(); // Tom lista om ingen aktiv order finns
                }
                List<CartItem> cartItems = cartDAO.getCartItems(conn, activeOrderId);
                conn.commit();
                return cartItems;
            } catch (SQLException e) {
                conn.rollback(); // Rollback vid fel
                System.out.println("Error fetching cart items: " + e.getMessage());
                return List.of();
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return List.of();
        }
    }


    public boolean addToCart(int customerId, int productId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // Startar transaktionen

            try {
                Integer activeOrderId = orderDAO.getActiveOrderId(conn, customerId);
                boolean success = orderDAO.addToCart(conn, customerId, activeOrderId, productId);
                if (success) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback(); // Rulla tillbaka vid fel
                System.out.println("Error adding to cart: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeFromCart(int customerId, int productId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // Starta transaktion

            try {
                Integer activeOrderId = orderDAO.getActiveOrderId(conn, customerId);
                if (activeOrderId == null) {
                    conn.rollback();
                    return false; // Ingen aktiv order att ta bort från
                }
                boolean success = cartDAO.removeOneFromCart(conn, customerId, activeOrderId, productId);
                if (success) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback(); // Rulla tillbaka vid fel
                System.out.println("Error removing from cart: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return false;
        }
    }
}
