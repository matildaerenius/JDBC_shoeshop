package gui;

import constants.CommonConstants;
import model.Customer;
import service.CustomerService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {
    private ViewManager viewManager;
    private CustomerService customerService;

    public LoginPanel(ViewManager viewManager, CustomerService customerService) {
        this.viewManager = viewManager;
        this.customerService = customerService;
        setLayout(null);
        setBackground(CommonConstants.PRIMARY_COLOR);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setBounds(0, 25, 520, 100);
        loginLabel.setForeground(CommonConstants.TEXT_COLOR);
        loginLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(loginLabel);

        JLabel emailLabel = new JLabel("E-mail:");
        emailLabel.setBounds(30, 150, 400, 25);
        emailLabel.setForeground(CommonConstants.TEXT_COLOR);
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField emailField = new JTextField();
        emailField.setBounds(30, 185, 450, 55);
        emailField.setBackground(CommonConstants.SECONDARY_COLOR);
        emailField.setForeground(CommonConstants.TEXT_COLOR);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(emailLabel);
        add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 315, 400, 25);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(30, 345, 450, 55);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR);
        passwordField.setForeground(CommonConstants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 20));

        JCheckBox showPasswordCheckBox = getjCheckBox(passwordField);

        add(passwordLabel);
        add(passwordField);
        add(showPasswordCheckBox);

        JButton loginButton = new JButton("Login");
        loginButton.setFocusable(false);
        loginButton.setFont(new Font("Dialog", Font.BOLD, 18));
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setBackground(CommonConstants.TEXT_COLOR);
        loginButton.setBounds(125,510,250,50);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                ImageIcon errorIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/sad.png",35, 35);
                ImageIcon successIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/success.png",35, 35);

                Customer customer = customerService.validateUser(email, password);
                if (customer != null) {
                    viewManager.setCustomer(customer);
                    JOptionPane.showMessageDialog(LoginPanel.this,
                            "Welcome " + customer.getFirstName() + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE,
                            successIcon);
                    viewManager.showProductPanel();
                } else {
                    JOptionPane.showMessageDialog(LoginPanel.this,
                            "Login Failed...Wrong email or password!",
                            "Login error",
                            JOptionPane.ERROR_MESSAGE,
                            errorIcon);
                }

            }
        });
        add(loginButton);

        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        };
        emailField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        JLabel registerLabel = new JLabel("Not a user? Register Here");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.setForeground(CommonConstants.TEXT_COLOR);

        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                viewManager.showRegisterPanel();
            }
        }
        );

        registerLabel.setBounds(125,580,250,40);
        add(registerLabel);
    }


    private static JCheckBox getjCheckBox(JPasswordField passwordField) {
        JCheckBox showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setBounds(30, 410, 200, 25);
        showPasswordCheckBox.setForeground(CommonConstants.TEXT_COLOR);
        showPasswordCheckBox.setBackground(CommonConstants.PRIMARY_COLOR);
        showPasswordCheckBox.setFocusable(false);
        showPasswordCheckBox.setFont(new Font("Dialog", Font.PLAIN, 14));
        showPasswordCheckBox.addActionListener(e -> {
            if (showPasswordCheckBox.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('*');
            }
        });
        return showPasswordCheckBox;
    }
}
