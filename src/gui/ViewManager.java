package gui;

import constants.CommonConstants;
import model.Customer;
import service.CartService;
import service.CategoryService;
import service.CustomerService;
import service.OrderService;
import service.ProductService;

import javax.swing.*;
import java.awt.*;

/**
 * Denna frame växlar mellan de olika fönstren med hjälp av en cardlayout
 */

public class ViewManager extends JFrame {
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final CustomerService customerService;
    private final CartService cartService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    private Customer currentCustomer;
    private CartPanel cartPanel;
    private OrderHistoryPanel orderHistoryPanel;

    public CartService getCartService() {
        return cartService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public ViewManager() {
        super("ShoeShop");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setLayout(new BorderLayout());

        customerService = new CustomerService();
        productService = new ProductService();
        categoryService = new CategoryService();
        cartService = new CartService();
        orderService = new OrderService();

        LoginPanel loginPanel = new LoginPanel(this, customerService);
        RegisterPanel registerPanel = new RegisterPanel(this, customerService);
        ProductPanel productPanel = new ProductPanel(this, productService, categoryService, cartService);
        cartPanel = new CartPanel(this, cartService,orderService);
        orderHistoryPanel = new OrderHistoryPanel(this, orderService);


        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);


        // Wrapper panel för att lägga LoginPanel i mitten av fönstret
        JPanel loginWrapper = new JPanel(new BorderLayout());
        loginWrapper.setBackground(CommonConstants.PRIMARY_COLOR);
        loginWrapper.add(Box.createHorizontalStrut(240), BorderLayout.WEST);
        loginWrapper.add(Box.createHorizontalStrut(240), BorderLayout.EAST);
        loginWrapper.add(loginPanel, BorderLayout.CENTER);

        // Wrapper panel för att lägga RegisterPanel i mitten av fönstret
        JPanel registerWrapper = new JPanel(new BorderLayout());
        registerWrapper.setBackground(CommonConstants.PRIMARY_COLOR);
        registerWrapper.add(Box.createHorizontalStrut(240), BorderLayout.WEST);
        registerWrapper.add(Box.createHorizontalStrut(240), BorderLayout.EAST);
        registerWrapper.add(registerPanel, BorderLayout.CENTER);

        cardPanel.add(loginWrapper, "login");
        cardPanel.add(productPanel, "products");
        cardPanel.add(cartPanel, "cart");
        cardPanel.add(registerWrapper, "register");
        cardPanel.add(orderHistoryPanel, "orderHistory");

        add(cardPanel, BorderLayout.CENTER);
        showLoginPanel();
        setVisible(true);

    }
    public void showLoginPanel() {
        cardLayout.show(cardPanel, "login");
        pack();
    }
    public void showRegisterPanel() {
        cardLayout.show(cardPanel, "register");
        pack();
    }

    public void showProductPanel() {
        cardLayout.show(cardPanel, "products");
        pack();
    }

    public void showCartPanel() {
        // Laddar om varukorgen från db varje gång vi visar CartPanel
        cartPanel.loadCartItems();
        cardLayout.show(cardPanel, "cart");
        pack();
    }
    public void showOrderHistoryPanel() {
        // Laddar orderHistory varje gång vi visar orderHistory
        orderHistoryPanel.loadOrderHistory();
        cardLayout.show(cardPanel, "orderHistory");
        pack();
    }

    public void setCustomer(Customer customer) {
        this.currentCustomer = customer;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

}
