package dao;

import model.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
* Anropar Stored Procedure "AddToCart" för att lägga till produkt i varukorg
 */
public class OrderDAO {

    // Returnerar id för kundens active order eller null om ingen finns
    public static Integer getActiveOrderId(Connection conn, int customerId) throws SQLException {
        String sql = "SELECT id FROM orders WHERE customer_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return null;
    }

    // Anropar sp "addtocart" för att lägga in produkter i varukorgen, eller skapar ny order om ingen active finns
    public boolean addToCart(Connection conn, int customerId, Integer orderId, int productId) throws SQLException {
        String sql = "{CALL AddToCart(?, ?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, customerId);
            if (orderId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, orderId);
            }
            stmt.setInt(3, productId);

            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding to cart: " + e.getMessage());
            throw e; // Kastar vidare undantaget för att hantera det i service klass
        }
    }

    // Uppdaterar en active order till paid
    public boolean completeOrder(Connection conn, int orderId) throws SQLException {
        String sql = "UPDATE orders SET status = 'PAID' WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error completing order: " + e.getMessage());
            throw e; // Kastar vidare undantaget för att hantera det i service klass
        }
    }

    // Lägger ihop alla betalda ordrar för kund i en lista med orders
    public List<Order> getOrdersForCustomer(Connection conn, int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();

        // Lägger ihop antal produkter och totalpris via en LEFT JOIN till order_details och product
        String sql =
                " SELECT o.id, o.date, " +
                        "        COALESCE(SUM(od.quantity), 0) AS total_items, " +
                        "        COALESCE(SUM(od.quantity * p.price), 0) AS total_sum " +
                        " FROM orders o " +
                        " LEFT JOIN order_details od ON o.id = od.orders_id " +
                        " LEFT JOIN product p ON od.product_id = p.id " +
                        " WHERE o.customer_id = ? " +
                        "   AND o.status = 'PAID' " +
                        " GROUP BY o.id " +
                        " ORDER BY o.date DESC ";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("id"),
                            rs.getDate("date"),
                            rs.getInt("total_items"),
                            rs.getInt("total_sum")
                    );
                    orders.add(order);
                }
            }
        }

        return orders;
    }
}