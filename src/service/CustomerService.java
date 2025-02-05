package service;

import dao.CustomerDAO;
import model.Customer;
import util.DbConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer validateUser(String email, String password) {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            // Använder bara en select så vi kan använda autoCommit som standard aka true
            return customerDAO.validateUser(conn, email, password);
        } catch (SQLException e) {
            System.out.println("Error validating user: " + e.getMessage());
            return null;
        }
    }

    public boolean registerUser(Customer customer) {
        try (Connection conn = DbConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false); // Startar transaktionen
            boolean success = customerDAO.registerUser(conn, customer);
            if (success) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return success;
        } catch (SQLException e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
}
