package gui;

import constants.CommonConstants;
import model.Customer;
import service.CustomerService;
import util.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterPanel extends JPanel {
    private ViewManager viewManager;
    private final CustomerService customerService;

    public RegisterPanel(ViewManager viewManager, CustomerService customerService) {
        this.viewManager = viewManager;
        this.customerService = customerService;
        setLayout(null);
        setPreferredSize(new Dimension(520, 680));
        setBackground(CommonConstants.PRIMARY_COLOR);

        addGuiComponents();
    }

    private void addGuiComponents() {
        JLabel registerLabel = new JLabel("Register");
        registerLabel.setBounds(0, 25, 520, 40);
        registerLabel.setForeground(CommonConstants.TEXT_COLOR);
        registerLabel.setFont(new Font("Dialog", Font.BOLD, 40));
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(registerLabel);

        JLabel firstnameLabel = new JLabel("Firstname:");
        firstnameLabel.setBounds(30, 80, 400, 25);
        firstnameLabel.setForeground(CommonConstants.TEXT_COLOR);
        firstnameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField firstnameField = new JTextField();
        firstnameField.setBounds(30, 110, 450, 40);
        firstnameField.setBackground(CommonConstants.SECONDARY_COLOR);
        firstnameField.setForeground(CommonConstants.TEXT_COLOR);
        firstnameField.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(firstnameLabel);
        add(firstnameField);

        JLabel lastnameLabel = new JLabel("Lastname:");
        lastnameLabel.setBounds(30, 160, 400, 25);
        lastnameLabel.setForeground(CommonConstants.TEXT_COLOR);
        lastnameLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField lastnameField = new JTextField();
        lastnameField.setBounds(30, 190, 450, 40);
        lastnameField.setBackground(CommonConstants.SECONDARY_COLOR);
        lastnameField.setForeground(CommonConstants.TEXT_COLOR);
        lastnameField.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(lastnameLabel);
        add(lastnameField);

        JLabel emailLabel = new JLabel("E-mail:");
        emailLabel.setBounds(30, 240, 400, 25);
        emailLabel.setForeground(CommonConstants.TEXT_COLOR);
        emailLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField emailField = new JTextField();
        emailField.setBounds(30, 270, 450, 40);
        emailField.setBackground(CommonConstants.SECONDARY_COLOR);
        emailField.setForeground(CommonConstants.TEXT_COLOR);
        emailField.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(emailLabel);
        add(emailField);

        JLabel cityLabel = new JLabel("City:");
        cityLabel.setBounds(30, 320, 400, 25);
        cityLabel.setForeground(CommonConstants.TEXT_COLOR);
        cityLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JTextField cityField = new JTextField();
        cityField.setBounds(30, 350, 450, 40);
        cityField.setBackground(CommonConstants.SECONDARY_COLOR);
        cityField.setForeground(CommonConstants.TEXT_COLOR);
        cityField.setFont(new Font("Dialog", Font.PLAIN, 20));

        add(cityLabel);
        add(cityField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(30, 400, 400, 25);
        passwordLabel.setForeground(CommonConstants.TEXT_COLOR);
        passwordLabel.setFont(new Font("Dialog", Font.PLAIN, 18));

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(30, 430, 450, 40);
        passwordField.setBackground(CommonConstants.SECONDARY_COLOR);
        passwordField.setForeground(CommonConstants.TEXT_COLOR);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 20));

        JCheckBox showPasswordCheckBox = getjCheckBox(passwordField);

        add(passwordLabel);
        add(passwordField);
        add(showPasswordCheckBox);


        JButton registerButton = new JButton("Register");
        registerButton.setFocusable(false);
        registerButton.setFont(new Font("Dialog", Font.BOLD, 18));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setBackground(CommonConstants.TEXT_COLOR);
        registerButton.setBounds(125,540,250,50);
        registerButton.addActionListener(e -> {
            String firstname = firstnameField.getText().trim();
            String lastname = lastnameField.getText().trim();
            String city = cityField.getText().trim();
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            ImageIcon beggingIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/begging.png",35, 35);
            ImageIcon sadIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/sad.png",35, 35);

            if (firstname.isEmpty() || lastname.isEmpty() || city.isEmpty()
                    || email.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields...",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE,
                        beggingIcon);
                return;
            }
            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(this,
                        "Invalid email address....",
                        "Email Error",
                        JOptionPane.ERROR_MESSAGE,
                        sadIcon);
                return;
            }


            ImageIcon successIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/success.png",35, 35);
            ImageIcon disappointedIcon = ImageUtils.createScaledImageIcon("src/resources/pictures/messages/disappointed.png",35, 35);

            Customer customer = new Customer(firstname, lastname, city, email, pass);
            boolean success = customerService.registerUser(customer);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Registration successful. You can now log in!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE,
                        successIcon);
                viewManager.showLoginPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Registration failed. Email already taken.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE,
                        disappointedIcon);
            }
        });
        add(registerButton);
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    registerButton.doClick();
                }
            }
        };

        firstnameField.addKeyListener(enterKeyListener);
        lastnameField.addKeyListener(enterKeyListener);
        emailField.addKeyListener(enterKeyListener);
        cityField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);


        JLabel loginLabel = new JLabel("Have an account? Login Here");
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.setForeground(CommonConstants.TEXT_COLOR);

        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                viewManager.showLoginPanel();
            }
        });

        loginLabel.setBounds(125,610,250,30);
        add(loginLabel);
    }

    private static JCheckBox getjCheckBox(JPasswordField passwordField) {
        JCheckBox showPasswordCheckBox = new JCheckBox("Show password");
        showPasswordCheckBox.setBounds(30, 480, 200, 25);
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

