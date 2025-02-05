package dao;

import model.Customer;
import util.DbConnection;

import java.sql.*;

/**
* Loggar in en kund eller registrerar en ny kund
 */
public class CustomerDAO {

    // Validerar en kund = logga in. Returnerar en customer, annars null om ingen hittas
    public Customer validateUser(Connection conn, String email, String password) throws SQLException {
        String sql = "SELECT * FROM customer "
                + "WHERE email = ? AND password = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("id"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("city"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    // Registrerar en ny kund in till db
    public boolean registerUser(Connection conn, Customer customer) throws SQLException {
        // Kollar om e-post redan finns
        if (emailExists(conn, customer.getEmail())) {
            return false;
    }
        String sql = "INSERT INTO customer(first_name, last_name, city, email, password) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getCity());
            pstmt.setString(4, customer.getEmail());
            pstmt.setString(5, customer.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        customer.setId(rs.getInt(1)); // Sätter id som databasen genererade
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }

    // Kollar om e-posten redan existerar
    private boolean emailExists(Connection conn, String email) throws SQLException {
        String sql = "SELECT id FROM customer WHERE email = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        }
        }
    }

