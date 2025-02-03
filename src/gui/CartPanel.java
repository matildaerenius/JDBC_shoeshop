package gui;

import constants.CommonConstants;
import model.CartItem;
import model.Product;
import service.CartService;
import service.OrderService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {
    private ViewManager viewManager;
    private JPanel itemsPanel;
    private JLabel totalItemsLabel;
    private JLabel totalSumLabel;
    private CartService cartService;
    private OrderService orderService;

    public CartPanel(ViewManager viewManager, CartService cartService, OrderService orderService) {
        this.viewManager = viewManager;
        this.cartService = cartService;
        this.orderService = orderService;

        setLayout(null);
        setBackground(CommonConstants.PRIMARY_COLOR);

        initTopBar();
        initItemsArea();
        initBottomBar();
    }

    private void initTopBar() {
        JLabel cartTitle = new JLabel("Shopping cart", SwingConstants.CENTER);
        cartTitle.setFont(new Font("Dialog", Font.PLAIN, 28));
        cartTitle.setForeground(CommonConstants.TEXT_COLOR);
        cartTitle.setBounds(400, 10, 200, 40);
        add(cartTitle);

        JButton continueShoppingButton = new JButton("Continue shopping");
        continueShoppingButton.setFocusable(false);
        continueShoppingButton.setBackground(CommonConstants.TEXT_COLOR);
        continueShoppingButton.setFont(new Font("Dialog", Font.BOLD, 13));
        continueShoppingButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        continueShoppingButton.setBounds(700, 10, 160, 30);
        continueShoppingButton.addActionListener(e -> viewManager.showProductPanel());
        add(continueShoppingButton);

        JButton orderHistoryButton = new JButton("Order History");
        orderHistoryButton.setFocusable(false);
        orderHistoryButton.setBackground(CommonConstants.TEXT_COLOR);
        orderHistoryButton.setFont(new Font("Dialog", Font.BOLD, 13));
        orderHistoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        orderHistoryButton.setBounds(60, 10, 160, 30);
        orderHistoryButton.addActionListener(e -> viewManager.showOrderHistoryPanel());
        add(orderHistoryButton);

        ImageIcon questioningIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/questioning.png",35, 35);

        JButton logoutButton = new JButton("Log out");
        logoutButton.setFocusable(false);
        logoutButton.setBackground(CommonConstants.TEXT_COLOR);
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 13));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBounds(870, 10, 100, 30);
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    questioningIcon

            );
            if (choice == JOptionPane.YES_OPTION) {
                viewManager.showLoginPanel();
            }
        });
        add(logoutButton);
    }

    private void initItemsArea() {
        itemsPanel = new JPanel();
        itemsPanel.setLayout(null);
        itemsPanel.setBackground(CommonConstants.SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBounds(50, 70, 900, 500);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);
    }

    private void initBottomBar() {
        JButton payButton = new JButton("PAY");
        payButton.setBackground(CommonConstants.TEXT_COLOR);
        payButton.setFocusable(false);
        payButton.setFont(new Font("Dialog", Font.BOLD, 20));
        payButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        payButton.setBounds(400, 600, 210, 50);
        payButton.addActionListener(e -> handlePayment());
        add(payButton);

        totalItemsLabel = new JLabel("Total items:");
        totalItemsLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        totalItemsLabel.setForeground(CommonConstants.TEXT_COLOR);
        totalItemsLabel.setBounds(700, 600, 200, 20);
        add(totalItemsLabel);

        totalSumLabel = new JLabel("Total sum:");
        totalSumLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        totalSumLabel.setForeground(CommonConstants.TEXT_COLOR);
        totalSumLabel.setBounds(700, 630, 200, 20);
        add(totalSumLabel);
    }
    private void handlePayment() {
        int customerId = viewManager.getCurrentCustomer().getId();
        boolean success = orderService.completeOrder(customerId);

        ImageIcon successIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/success.png",35, 35);
        ImageIcon disappointedIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/disappointed.png",35, 35);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Payment successful! Thank you for your purchase",
                    "Payment",
                    JOptionPane.INFORMATION_MESSAGE,
                    successIcon
            );
            loadCartItems();
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "You have no active order",
                    "Order Error",
                    JOptionPane.ERROR_MESSAGE,
                    disappointedIcon
            );
        }
    }

    public void loadCartItems() {
        itemsPanel.removeAll();

        int customerId = viewManager.getCurrentCustomer().getId();
        List<CartItem> cartItems = cartService.getCartItems(customerId);

        if (cartItems.isEmpty()) {
            JLabel emptyLabel = new JLabel("Your cart is empty");
            emptyLabel.setForeground(CommonConstants.TEXT_COLOR);
            emptyLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            emptyLabel.setBounds(360, 50, 300, 30);
            itemsPanel.add(emptyLabel);

            itemsPanel.setPreferredSize(new Dimension(900, 150));
            itemsPanel.revalidate();
            itemsPanel.repaint();

            totalItemsLabel.setText("Total items: 0");
            totalSumLabel.setText("Total sum: 0 kr");
            return;
        }

        int totalItems = 0;
        int totalSum = 0;
        int currentY = 20;

        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
            totalSum += item.getQuantity() * item.getProduct().getPrice();

            JPanel itemPanel = createItemPanel(item);
            itemPanel.setBounds(50, currentY, 800, 100);
            itemsPanel.add(itemPanel);

            currentY += 110;
        }

        itemsPanel.setPreferredSize(new Dimension(900, currentY + 20));
        itemsPanel.revalidate();
        itemsPanel.repaint();

        totalItemsLabel.setText("Total items: " + totalItems);
        totalSumLabel.setText("Total sum: " + totalSum + " kr");
    }

    private JPanel createItemPanel(CartItem item) {
        JPanel panel = new JPanel(null);
        panel.setBackground(CommonConstants.SECONDARY_COLOR);
        panel.setPreferredSize(new Dimension(800, 200));

        JLabel productImageLabel = new JLabel();
        String imagePath = getImagePathForProduct(item.getProduct());
        ImageIcon productIcon = ImageUtils.createScaledImageIcon(imagePath, 80, 80);
        productImageLabel.setIcon(productIcon);
        productImageLabel.setBounds(210, 10, 80, 80);
        panel.add(productImageLabel);

        JLabel productNameLabel = new JLabel(item.getProduct().getBrand());
        productNameLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        productNameLabel.setForeground(Color.WHITE);
        productNameLabel.setBounds(310, 10, 200, 20);
        panel.add(productNameLabel);

        JLabel productDetailsLabel = new JLabel(
                "Color: " + item.getProduct().getColor() +
                        " | Size: " + item.getProduct().getSize()
        );
        productDetailsLabel.setBounds(310, 35, 400, 20);
        productDetailsLabel.setForeground(new Color(176, 176, 176));
        panel.add(productDetailsLabel);

        JLabel productPriceLabel = new JLabel("Price: " + item.getProduct().getPrice() + " kr");
        productPriceLabel.setBounds(310, 60, 200, 20);
        productPriceLabel.setForeground(new Color(176, 176, 176));
        panel.add(productPriceLabel);

        JLabel productQuantityLabel = new JLabel("Quantity: " + item.getQuantity());
        productQuantityLabel.setBounds(500, 60, 200, 20);
        productQuantityLabel.setForeground(new Color(176, 176, 176));
        panel.add(productQuantityLabel);

        JButton minusButton = new JButton("-");
        minusButton.setBackground(Color.WHITE);
        minusButton.setFocusable(false);
        minusButton.setFont(new Font("Dialog", Font.BOLD, 14));
        minusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        minusButton.setBounds(500, 35, 25, 20);
        minusButton.setMargin(new Insets(0, 0, 0, 0));
        minusButton.addActionListener(e -> {
            int customerId = viewManager.getCurrentCustomer().getId();
            boolean success = cartService.removeFromCart(customerId, item.getProduct().getId());
            if (success) {
                loadCartItems();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Error removing item from cart.",
                        "Cart Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        panel.add(minusButton);

        ImageIcon sadIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/sad.png",35, 35);

        JButton plusButton = new JButton("+");
        plusButton.setBackground(Color.WHITE);
        plusButton.setFocusable(false);
        plusButton.setFont(new Font("Dialog", Font.BOLD, 14));
        plusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        plusButton.setBounds(530, 35, 25,20);
        plusButton.setMargin(new Insets(0, 0, 0, 0));
        plusButton.addActionListener(e -> {
            int customerId = viewManager.getCurrentCustomer().getId();
            boolean success = cartService.addToCart(customerId, item.getProduct().getId());
            if (success) {
                loadCartItems();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Sorry...the product is out of stock :(",
                        "Out of Stock",
                        JOptionPane.ERROR_MESSAGE,
                        sadIcon
                );
            }
        });
        panel.add(plusButton);

        return panel;
    }

    private String getImagePathForProduct(Product product) {
        String formattedBrand = product.getBrand().toLowerCase().replace(" ", "_");
        String formattedColor = product.getColor().toLowerCase().replace(" ", "_");
        return "src/resources/pictures/products/" + formattedColor + "_" + formattedBrand + ".png";
    }
}
