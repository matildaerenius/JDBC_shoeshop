package dao;

import model.CartItem;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Anropar stored procedure "removeOneFromCart" för att ta bort produkt
 */

public class CartDAO {

    // Hämtar alla varor i en viss order, baserat på orderid
    public List<CartItem> getCartItems(Connection conn, int orderId) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT p.id, p.size, p.brand, p.color, p.price, od.quantity " +
                "FROM order_details od " +
                "JOIN product p ON od.product_id = p.id " +
                "WHERE od.orders_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    cartItems.add(new CartItem(
                            new Product(
                                    rs.getInt("id"),
                                    rs.getInt("size"),
                                    rs.getString("color"),
                                    rs.getString("brand"),
                                    rs.getInt("price"),
                                    0 // Lagerstatus hanteras separat :)
                            ),
                            rs.getInt("quantity")
                    ));
                }
            }
        }

        return cartItems;
    }


    // Anropar sp för att ta bort en produkt från varukorgen
    public boolean removeOneFromCart(Connection conn, int customerId, int orderId, int productId) throws SQLException {
        String sql = "{CALL RemoveOneFromCart(?, ?, ?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, customerId);
            cstmt.setInt(2, orderId);
            cstmt.setInt(3, productId);

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            throw e; // Kastar vidare undantaget för att hantera det i service klass
        }
    }
}

