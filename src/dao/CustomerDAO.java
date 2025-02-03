package dao;

import model.Customer;
import util.DbConnection;

import java.sql.*;

/**
* Loggar in en kund eller registrerar en ny kund
 */
public class CustomerDAO {

    // Validerar en kund = logga in. Returnerar en customer, annars null om ingen hittas
    public Customer validateUser(String email, String password) {
        String sql = "SELECT * FROM customer "
                + "WHERE email = ? AND password = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

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

        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
        }
        return null;
    }

    // Registrerar en ny kund in till db
    public boolean registerUser(Customer customer){
        // Kollar om e-post redan finns
        if (emailExists(customer.getEmail())) {
            return false;
    }
        String sql = "INSERT INTO customer(first_name, last_name, city, email, password) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

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
                conn.commit(); // Bekräftar transaktionen
                return true;
            } else {
                conn.rollback(); // Rollback om inga rader påverkades
            }

        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
        return false;
    }

    // Kollar om e-posten redan existerar
    private boolean emailExists(String email) {
        String sql = "SELECT id FROM customer WHERE email = ?";

        try (Connection conn = DbConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }
}
