package gui;

import constants.CommonConstants;
import model.Category;
import model.Product;
import service.CartService;
import service.CategoryService;
import service.ProductService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
* Denna frame visar alla produkter med sökfunktion, detaljer knapp och lägg till i varukorgen knapp
 */

public class ProductPanel extends JPanel {
    private ViewManager viewManager;
    private ProductService productService;
    private CategoryService categoryService;
    private CartService cartService;

    private JPanel productPanel;
    private JScrollPane scrollPane;
    private List<JButton> categoryButtons;
    private JButton allButton;

    public ProductPanel(ViewManager viewManager, ProductService productService, CategoryService categoryService, CartService cartService) {
        this.viewManager = viewManager;
        this.productService = productService;
        this.categoryService = categoryService;
        this.cartService = cartService;
        this.categoryButtons = new ArrayList<>();
        setLayout(null);
        setBackground(CommonConstants.PRIMARY_COLOR);

        addGuiComponents();
        loadProducts();
    }

    private void addGuiComponents() {

        JLabel titleLabel = new JLabel("SHOESHOP");
        titleLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        titleLabel.setForeground(CommonConstants.TEXT_COLOR);
        titleLabel.setBounds(30, 10, 140, 40);

        JTextField searchTextField = new JTextField("Search");
        searchTextField.setBackground(CommonConstants.SECONDARY_COLOR);
        searchTextField.setForeground(CommonConstants.TEXT_COLOR);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 13));
        searchTextField.setBounds(200, 10, 400, 30);

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(CommonConstants.TEXT_COLOR);
        searchButton.setFocusable(false);
        searchButton.setFont(new Font("Dialog", Font.BOLD, 13));
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(610, 10, 100, 30);
        searchButton.addActionListener(e -> {
            String searchTerm = searchTextField.getText().trim();
            if (!searchTerm.isEmpty()) {
                searchProducts(searchTerm);
            } else {
                loadProducts();
            }
        });
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchButton.doClick();
                }
            }
        };
        searchTextField.addKeyListener(enterKeyListener);

        JButton cartButton = new JButton("View Cart");
        cartButton.setFocusable(false);
        cartButton.setBackground(CommonConstants.TEXT_COLOR);
        cartButton.setFont(new Font("Dialog", Font.BOLD, 13));
        cartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartButton.setBounds(740, 10, 100, 30);
        cartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewManager.showCartPanel();
            }
        });

        JButton logoutButton = new JButton("Log out");
        logoutButton.setFocusable(false);
        logoutButton.setBackground(CommonConstants.TEXT_COLOR);
        logoutButton.setFont(new Font("Dialog", Font.BOLD, 13));
        logoutButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutButton.setBounds(850, 10, 100, 30);

        ImageIcon questioningIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/questioning.png",35, 35);

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
            }
        });

        JPanel categoryPanel = new JPanel(null);
        categoryPanel.setBackground(CommonConstants.SECONDARY_COLOR);
        categoryPanel.setBounds(20, 60, 150, 530);

        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setForeground(CommonConstants.TEXT_COLOR);
        categoryLabel.setFont(new Font("Dialog", Font.BOLD, 15));
        categoryLabel.setBounds(45, 3, 120, 30);
        categoryPanel.add(categoryLabel);

        allButton = new JButton("All");
        allButton.setBackground(CommonConstants.TEXT_COLOR);
        allButton.setFocusable(false);
        allButton.setFont(new Font("Dialog", Font.BOLD, 13));
        allButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        allButton.setBounds(15, 40, 120, 30);
        allButton.addActionListener(e -> {
            highlightSelectedButton(allButton);
            loadProducts();
        });
        categoryPanel.add(allButton);

        List<Category> categories = categoryService.findAllCategories();

        int yPosition = 95;
        for (Category category  : categories) {
            String categoryName = category.getName();
            JButton categoryButton = new JButton(categoryName);
            categoryButton.setBackground(CommonConstants.TEXT_COLOR);
            categoryButton.setFocusable(false);
            categoryButton.setFont(new Font("Dialog", Font.BOLD, 13));
            categoryButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            categoryButton.setBounds(15, yPosition, 120, 30);
            categoryButtons.add(categoryButton);
            yPosition += 60; // Flyttar nästa knapp nedåt


            categoryButton.addActionListener(e -> {
                    highlightSelectedButton(categoryButton);
                    updateProductPanel(categoryName);
                    });

            categoryPanel.add(categoryButton);
        }

        productPanel = new JPanel(null);
        productPanel.setBackground(CommonConstants.SECONDARY_COLOR);
        productPanel.setBounds(200, 60, 750, 530);

        scrollPane = new JScrollPane(productPanel);
        scrollPane.setBounds(200, 60, 750, 530);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        add(titleLabel);
        add(searchTextField);
        add(searchButton);
        add(cartButton);
        add(logoutButton);
        add(categoryPanel);
        add(scrollPane);
    }

    private void updateProductPanel(String categoryName) {
        productPanel.removeAll(); // Tar bort tidigare produkter

        List<Product> products = productService.getProductsByCategory(categoryName);
        displayProducts(products);
    }

    private void loadProducts() {
        productPanel.removeAll();

        List<Product> products = productService.getAllProducts(); // Hämtar alla produkter från service
        displayProducts(products);
    }

    private void displayProducts(List<Product> products) {
        int xPosition = 10, yPosition = 10;

        for (Product product : products) {
            JPanel productCard = new JPanel(null);
            productCard.setBackground(CommonConstants.SECONDARY_COLOR);
            productCard.setBounds(xPosition, yPosition, 230, 300);

            JPanel textPanel = new JPanel(new GridLayout(2, 2));
            textPanel.setBackground(CommonConstants.SECONDARY_COLOR);
            textPanel.setBounds(20, 30, 200, 40);

            JLabel brandLabel = new JLabel(product.getBrand());
            brandLabel.setForeground(Color.WHITE);
            brandLabel.setFont(new Font("Dialog", Font.BOLD, 14));

            JLabel colorLabel = new JLabel("Color: " + product.getColor());
            colorLabel.setForeground(new Color(176, 176, 176));
            colorLabel.setFont(new Font("Dialog", Font.BOLD, 12));

            JLabel sizeLabel = new JLabel("Size: " + product.getSize());
            sizeLabel.setForeground(new Color(176, 176, 176));
            sizeLabel.setFont(new Font("Dialog", Font.BOLD, 12));

            JLabel priceLabel = new JLabel("Price: " + product.getPrice() + " kr");
            priceLabel.setForeground(new Color(176, 176, 176));
            priceLabel.setFont(new Font("Dialog", Font.BOLD, 12));

            textPanel.add(brandLabel);
            textPanel.add(colorLabel);
            textPanel.add(sizeLabel);
            textPanel.add(priceLabel);

            JLabel productImage = new JLabel();
            String imagePath = getImagePathForProduct(product);
            ImageIcon productIcon = ImageUtils.createScaledImageIcon(imagePath, 200, 200);
            productImage.setIcon(productIcon);
            productImage.setBounds(15, 80, 200, 150);

            ImageIcon workerIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/worker.png",35, 35);

            JButton detailsButton = new JButton("Details");
            detailsButton.setFocusable(false);
            detailsButton.setBackground(CommonConstants.TEXT_COLOR);
            detailsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            detailsButton.setBounds(15, 240, 100, 30);
            detailsButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(null, "---Details is under construction---",
                        "Under construction",
                        JOptionPane.INFORMATION_MESSAGE,
                        workerIcon);
            });

            JLabel addToCartLabel = createAddToCartLabel(product);
            addToCartLabel.setBounds(170, 240, 50, 30);

            productCard.add(textPanel);
            productCard.add(productImage);
            productCard.add(detailsButton);
            productCard.add(addToCartLabel);

            productPanel.add(productCard);

            xPosition += 240;
            if (xPosition + 240 > scrollPane.getWidth() - 20) {
                xPosition = 10;
                yPosition += 320;
            }
        }

        productPanel.setPreferredSize(new Dimension(750, yPosition + 320));
        productPanel.revalidate();
        productPanel.repaint();
    }

    private void searchProducts(String searchTerm) {
        productPanel.removeAll();

        List<Product> filteredProducts = productService.searchProducts(searchTerm);

        if (!filteredProducts.isEmpty()) {
            displayProducts(filteredProducts);
        } else {
            JLabel noResultsLabel = new JLabel("No products found for: " + searchTerm);
            noResultsLabel.setForeground(CommonConstants.TEXT_COLOR);
            noResultsLabel.setFont(new Font("Dialog", Font.BOLD, 18));
            noResultsLabel.setBounds(20, 20, 500, 30);
            productPanel.add(noResultsLabel);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    private JLabel createAddToCartLabel(Product product) {
        ImageIcon cartIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/shoppingcart.png",30,30);
        JLabel addToCartLabel = new JLabel(cartIcon);

        addToCartLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addToCartLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleAddToCart(product);
            }
        });

        return addToCartLabel;
    }

    private void handleAddToCart(Product product) {
        int customerId = viewManager.getCurrentCustomer().getId();
        boolean success = viewManager.getCartService().addToCart(customerId, product.getId());

        ImageIcon successIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/success.png",35, 35);
        ImageIcon sadIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/sad.png",35, 35);

        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "Added to cart!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE,
                    successIcon
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Sorry...the product is out of stock :(",
                    "Out of stock",
                    JOptionPane.ERROR_MESSAGE,
                    sadIcon
            );
        }
    }


    private void highlightSelectedButton(JButton selectedButton) {
        // Återställer färg på alla knappar
        for (JButton button : categoryButtons) {
            button.setBackground(CommonConstants.TEXT_COLOR);
        }
        allButton.setBackground(CommonConstants.TEXT_COLOR);

        selectedButton.setBackground(new Color(176, 176, 176));
    }

    private String getImagePathForProduct(Product product) {
        String formattedBrand = product.getBrand().toLowerCase().replace(" ", "_");
        String formattedColor = product.getColor().toLowerCase().replace(" ", "_");
        return "src/resources/pictures/products/" + formattedColor + "_" + formattedBrand + ".png";
    }
}

