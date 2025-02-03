package service;

import dao.OrderDAO;
import model.Order;
import util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class OrderService {

    private final OrderDAO orderDAO;
    private final DbConnection dbConnection;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.dbConnection = DbConnection.getInstance();
    }

    public boolean completeOrder(int customerId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // Starta transaktion

            try {
                Integer activeOrderId = orderDAO.getActiveOrderId(conn, customerId);
                if (activeOrderId == null) {
                    conn.rollback();
                    return false; // Ingen aktiv order att slutföra
                }

                boolean success = orderDAO.completeOrder(conn, activeOrderId);
                if (success) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            } catch (SQLException e) {
                conn.rollback(); // Rulla tillbaka vid fel
                System.out.println("Error completing order: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public List<Order> getOrdersForCustomer(int customerId) {
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false); // Starta transaktion

            try {
                List<Order> orders = orderDAO.getOrdersForCustomer(conn, customerId);
                conn.commit();
                return orders;
            } catch (SQLException e) {
                conn.rollback(); // Rulla tillbaka vid fel
                System.out.println("Error fetching orders: " + e.getMessage());
                return List.of();
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            return List.of();
        }
    }
}

