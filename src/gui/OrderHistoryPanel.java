package gui;

import constants.CommonConstants;
import model.Order;
import service.OrderService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel som visar en kunds orderhistorik
 */

public class OrderHistoryPanel extends JPanel {
    private final ViewManager viewManager;
    private final OrderService orderService;

    private JPanel itemsPanel;

    public OrderHistoryPanel(ViewManager viewManager, OrderService orderService) {
        this.viewManager = viewManager;
        this.orderService = orderService;
        setLayout(null);
        setBackground(CommonConstants.PRIMARY_COLOR);

        initTopBar();
        initItemsArea();
    }

    private void initTopBar() {
        JLabel historyTitle = new JLabel("Order History", SwingConstants.CENTER);
        historyTitle.setFont(new Font("Dialog", Font.PLAIN, 28));
        historyTitle.setForeground(CommonConstants.TEXT_COLOR);
        historyTitle.setBounds(400, 10, 200, 40);
        add(historyTitle);

        JButton backButton = new JButton("Back to Cart");
        backButton.setFocusable(false);
        backButton.setBackground(CommonConstants.TEXT_COLOR);
        backButton.setFont(new Font("Dialog", Font.BOLD, 13));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setBounds(700, 10, 160, 30);
        backButton.addActionListener(e -> viewManager.showCartPanel());
        add(backButton);

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
        itemsPanel = new JPanel(null);
        itemsPanel.setBackground(CommonConstants.SECONDARY_COLOR);

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBounds(50, 70, 900, 500);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane);
    }

    /*
     Publik metod som laddar orderhistorik och visar dom i panelen
     Anropas när man växlar till denna panel i ViewManager
     */
    public void loadOrderHistory() {
        itemsPanel.removeAll();

        int customerId = viewManager.getCurrentCustomer().getId();
        List<Order> orders = orderService.getOrdersForCustomer(customerId);

        if (orders.isEmpty()) {
            JLabel emptyLabel = new JLabel("You have no past orders");
            emptyLabel.setForeground(CommonConstants.TEXT_COLOR);
            emptyLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            emptyLabel.setBounds(300, 50, 400, 30);
            itemsPanel.add(emptyLabel);

            itemsPanel.setPreferredSize(new Dimension(900, 150));
            itemsPanel.revalidate();
            itemsPanel.repaint();
            return;
        }

        int currentY = 20;
        for (Order order : orders) {
            JPanel orderPanel = createOrderPanel(order);
            orderPanel.setBounds(50, currentY, 800, 100);
            itemsPanel.add(orderPanel);

            currentY += 110;
        }

        itemsPanel.setPreferredSize(new Dimension(900, currentY + 20));
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }


     // Skapar en panel som representerar en order
    private JPanel createOrderPanel(Order order) {
        JPanel panel = new JPanel(null);
        panel.setBackground(CommonConstants.SECONDARY_COLOR);
        panel.setPreferredSize(new Dimension(800, 100));


        JLabel orderIdLabel = new JLabel("Order #" + order.getId());
        orderIdLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        orderIdLabel.setForeground(Color.WHITE);
        orderIdLabel.setBounds(280, 10, 200, 20);
        panel.add(orderIdLabel);

        JLabel orderDateLabel = new JLabel("Date: " + order.getOrderDate());
        orderDateLabel.setBounds(280, 35, 200, 20);
        orderDateLabel.setForeground(new Color(176, 176, 176));
        panel.add(orderDateLabel);

        JLabel totalItemsLabel = new JLabel("Items: " + order.getTotalItems());
        totalItemsLabel.setBounds(280, 60, 200, 20);
        totalItemsLabel.setForeground(new Color(176, 176, 176));
        panel.add(totalItemsLabel);

        ImageIcon workerIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/worker.png",35, 35);

        JButton detailsButton = new JButton("Details");
        detailsButton.setFocusable(false);
        detailsButton.setBackground(Color.WHITE);
        detailsButton.setFont(new Font("Dialog", Font.BOLD, 13));
        detailsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detailsButton.setBounds(450,35,80,20);
        detailsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(null, "---Details is under construction---",
                    "Under construction",
                    JOptionPane.INFORMATION_MESSAGE,
                    workerIcon);
        });
        panel.add(detailsButton);

        JLabel totalSumLabel = new JLabel("Total: " + order.getTotalSum() + " kr");
        totalSumLabel.setBounds(450, 60, 200, 20);
        totalSumLabel.setForeground(new Color(176, 176, 176));
        panel.add(totalSumLabel);

        return panel;
    }

}
