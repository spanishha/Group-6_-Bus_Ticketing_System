package group4;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// INHERITANCE: Extends JFrame para sa login window.
// INTERFACE: Implements ActionListener para mahawakan ang click ng Log-In button.
public class BusLogin extends JFrame implements ActionListener {
    JLabel labelTitle = new JLabel("Baliwag Transit, INC", SwingConstants.CENTER);
    JLabel labelUsername = new JLabel("Enter Operator ID: ");
    JLabel labelPassword = new JLabel("Enter Access Code: ");
    JTextField textUsername = new JTextField();
    JPasswordField textPassword = new JPasswordField();
    JButton buttonLogin = new JButton("LOG-IN");
    static String adminUser = "AD_BT";
    Color primaryGreen = new Color(40, 120, 50);
    Color yellow = new Color(255, 204, 0);
    Color darkGreen = new Color(20, 80, 30);

    public BusLogin() {
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Baliwag Transit, INC - Login");
        setLayout(null);
        getContentPane().setBackground(primaryGreen);
        
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(null);
        loginPanel.setBackground(primaryGreen);
        loginPanel.setBorder(BorderFactory.createLineBorder(yellow, 2));
        loginPanel.setBounds(450, 150, 600, 400);
        this.add(loginPanel);
        
        labelTitle.setFont(new Font("Arial", Font.BOLD, 36));
        labelTitle.setForeground(Color.WHITE);
        labelTitle.setBounds(50, 40, 500, 55);
        loginPanel.add(labelTitle);
        
        JLabel subtitle = new JLabel("WELCOME!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(yellow);
        subtitle.setBounds(50, 95, 500, 25);
        loginPanel.add(subtitle);
        
        JSeparator separator = new JSeparator();
        separator.setForeground(yellow);
        separator.setBackground(yellow);
        separator.setBounds(100, 135, 400, 2);
        loginPanel.add(separator);
        
        labelUsername.setFont(new Font("Arial", Font.BOLD, 15));
        labelUsername.setForeground(Color.WHITE);
        labelUsername.setBounds(75, 170, 180, 25);
        loginPanel.add(labelUsername);
        
        labelPassword.setFont(new Font("Arial", Font.BOLD, 15));
        labelPassword.setForeground(Color.WHITE);
        labelPassword.setBounds(75, 230, 180, 25);
        loginPanel.add(labelPassword);
        
        textUsername.setBounds(255, 165, 270, 35);
        textUsername.setBackground(Color.WHITE);
        textUsername.setForeground(Color.BLACK);
        textUsername.setCaretColor(darkGreen);
        textUsername.setFont(new Font("Arial", Font.PLAIN, 15));
        textUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(yellow, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        loginPanel.add(textUsername);
        
        textPassword.setBounds(255, 225, 270, 35);
        textPassword.setBackground(Color.WHITE);
        textPassword.setForeground(Color.BLACK);
        textPassword.setCaretColor(darkGreen);
        textPassword.setFont(new Font("Arial", Font.PLAIN, 15));
        textPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(yellow, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        loginPanel.add(textPassword);
        
        buttonLogin.setBounds(200, 305, 200, 45);
        buttonLogin.setBackground(yellow);
        buttonLogin.setForeground(Color.BLACK);
        buttonLogin.setFont(new Font("Arial", Font.BOLD, 16));
        buttonLogin.setFocusPainted(false);
        buttonLogin.setBorder(BorderFactory.createLineBorder(darkGreen, 2));
        buttonLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonLogin.addActionListener(this);
        loginPanel.add(buttonLogin);
        
        setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        new BusLogin().setVisible(true);
    }

    @Override
    // INTERFACE IMPLEMENTATION: Ito ang event handler na hinihingi ng ActionListener.
    // INTERFACE IMPLEMENTATION: Event handler na hinihingi ng ActionListener.
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(buttonLogin)) {
            String inputUser = textUsername.getText().trim();
            String inputPass = new String(textPassword.getPassword()).trim();
            String terminal = Terminal.findByOperatorId(inputUser);

            if (inputUser.equalsIgnoreCase(adminUser) || inputUser.equalsIgnoreCase("Admin")) {
                if (inputPass.equals(BusAdmin.getStoredAdminPassword())) {
                    BusAdmin adminPage = new BusAdmin();
                    adminPage.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Incorrect Admin Access Code!", "LOGIN ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else if (terminal != null) {
                if ("BLOCKED".equalsIgnoreCase(Terminal.getStatus(terminal))) {
                    JOptionPane.showMessageDialog(null, "The " + terminal + " Cashier Account is BLOCKED!\nPlease contact the Administrator.", "ACCESS DENIED", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (inputPass.equals(Terminal.getPassword(terminal))) {
                    Terminal.setAttempts(terminal, 0);
                    Terminal.setCurrent(terminal);
                    
                    BusMain cashierPage = new BusMain(terminal);
                    cashierPage.setVisible(true);
                    this.dispose();
                } else {
                    int currentAttempts = Terminal.getAttempts(terminal) + 1;
                    Terminal.setAttempts(terminal, currentAttempts);
                    int remaining = 3 - currentAttempts;

                    if (currentAttempts >= 3) {
                        Terminal.setStatus(terminal, "BLOCKED");
                        JOptionPane.showMessageDialog(null, "Account BLOCKED! You have exceeded 3 failed login attempts.", "SECURITY ALERT", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Incorrect Access Code! You have " + remaining + " attempt(s) left.", "LOGIN WARNING", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "INVALID OPERATOR ID OR ACCESS CODE!", "LOGIN ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
