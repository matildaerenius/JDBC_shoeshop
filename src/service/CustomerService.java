package service;

import dao.CustomerDAO;
import model.Customer;

public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer validateUser(String email, String password) {
        try {
            return customerDAO.validateUser(email, password);
        } catch (Exception e) {
            System.out.println("Error validating user:" + e.getMessage());
            return null;
        }
    }

    public boolean registerUser(Customer customer) {
        try {
            return customerDAO.registerUser(customer);
        } catch (Exception e) {
            System.out.println("Error registering user: " + e.getMessage());
            return false;
        }
    }
}
