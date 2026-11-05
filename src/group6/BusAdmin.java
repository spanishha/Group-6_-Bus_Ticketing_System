package group4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

// INHERITANCE
// INTERFACE
public class BusAdmin extends JFrame implements ActionListener {
    // ENCAPSULATION
    private static final String PASS_FILE = Records.FOLDER + "admin_pass.txt";
    private static final String DEFAULT_PASS = "baliwagadmin";
    JPanel sidebarPanel = new JPanel();
    JPanel headerPanel = new JPanel();
    JPanel contentPanel = new JPanel();
    JLabel titleLabel = new JLabel("Good Day, Admin!");
    JLabel logoLabel = new JLabel("BALIWAG TRANSIT, INC", SwingConstants.CENTER);
    JButton logoutButton = new JButton("LOG OUT");
    JButton dashboardButton = new JButton("Dashboard");
    JButton recordsButton = new JButton("View Records");
    JButton busDetailsButton = new JButton("Bus Details");
    JButton accountsButton = new JButton("Accounts");
    JButton settingsButton = new JButton("Settings");
    
    JPanel filterPanel = new JPanel();
    JLabel filterTitleLabel = new JLabel("SELECT ROUTE FILTER:");
    JButton filterBaliwagCubao = new JButton("Baliwag - Cubao");
    JButton filterBaliwagGracePark = new JButton("Baliwag - Grace Park");
    JButton filterBaliwagDivisoria = new JButton("Baliwag - Divisoria");
    JButton filterCubaoBaliwag = new JButton("Cubao - Baliwag");
    JButton filterGraceParkBaliwag = new JButton("Grace Park - Baliwag");
    JButton filterDivisoriaBaliwag = new JButton("Divisoria - Baliwag");
    JButton filterShowAll = new JButton("Show All Routes");
    
    JLabel routeValueLabel = new JLabel("ALL BUSES", SwingConstants.CENTER);
    JLabel ticketValueLabel = new JLabel("0 PAX", SwingConstants.CENTER);
    JLabel revenueValueLabel = new JLabel("PHP 0.00", SwingConstants.CENTER);
    JLabel busValueLabel = new JLabel("Active Fleet", SwingConstants.CENTER);
    JPanel card1, card2, card3, card4;
    JTable dashboardTable, recordsTable, busTable, accountsTable;
    DefaultTableModel dashboardModel, recordsModel, busModel, accountsModel;
    JScrollPane dashboardScroll, recordsScroll, busScroll, accountsScroll;
    JButton deleteRecordButton = new JButton("Delete Selected Record");
    JButton unblockButton = new JButton("Unblock Selected Account");
    JButton blockButton = new JButton("Block Selected Account");
    JButton createAccountButton = new JButton("Create New Account");
    Color primaryGreen = new Color(40, 120, 50);
    Color darkGreen = new Color(20, 80, 30);
    Color lightGrayBg = new Color(245, 245, 245);

    public BusAdmin() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Baliwag Transit - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        sidebarPanel.setPreferredSize(new Dimension(240, 0));
        sidebarPanel.setBackground(Color.WHITE);
        sidebarPanel.setLayout(null);
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        logoLabel.setBounds(10, 20, 220, 40);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(darkGreen);
        sidebarPanel.add(logoLabel);

        styleSidebarButton(dashboardButton, 80);
        styleSidebarButton(recordsButton, 130);
        styleSidebarButton(busDetailsButton, 180);
        styleSidebarButton(accountsButton, 230);
        styleSidebarButton(settingsButton, 280);
        setActiveMenu(dashboardButton);
        add(sidebarPanel, BorderLayout.WEST);

        JPanel rightContainer = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 65));
        headerPanel.setBackground(primaryGreen);
        headerPanel.setLayout(null);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(30, 15, 350, 35);
        headerPanel.add(titleLabel);

        logoutButton.setBounds(1120, 15, 120, 35);
        logoutButton.setBackground(new Color(200, 50, 50));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(this);
        headerPanel.add(logoutButton);
        rightContainer.add(headerPanel, BorderLayout.NORTH);

        contentPanel.setBackground(lightGrayBg);
        contentPanel.setLayout(null);

        card1 = createStatCard("Active Filter Route", routeValueLabel, 30, 20, 280, 85);
        card2 = createStatCard("Total Tickets Issued", ticketValueLabel, 330, 20, 280, 85);
        card3 = createStatCard("Total Revenue Generated", revenueValueLabel, 630, 20, 280, 85);
        card4 = createStatCard("Route Coverage", busValueLabel, 930, 20, 280, 85);
        contentPanel.add(card1);
        contentPanel.add(card2);
        contentPanel.add(card3);
        contentPanel.add(card4);

        filterPanel.setBounds(30, 120, 1180, 115);
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        filterPanel.setLayout(null);
        filterTitleLabel.setFont(new Font("Arial", Font.BOLD, 11));
        filterTitleLabel.setForeground(Color.GRAY);
        filterTitleLabel.setBounds(15, 8, 150, 20);
        filterPanel.add(filterTitleLabel);

        setupFilterButton(filterBaliwagCubao, 15, 32, 185);
        setupFilterButton(filterBaliwagGracePark, 210, 32, 195);
        setupFilterButton(filterBaliwagDivisoria, 415, 32, 185);
        setupFilterButton(filterCubaoBaliwag, 610, 32, 185);
        setupFilterButton(filterGraceParkBaliwag, 805, 32, 195);
        setupFilterButton(filterDivisoriaBaliwag, 1010, 32, 155);
        setupFilterButton(filterShowAll, 15, 72, 185);

        contentPanel.add(filterPanel);

        String[] cols = {"No.", "Bus Number", "Date & Time", "Origin", "Destination", "Passengers", "Payment", "Account Number", "Amount"};
        dashboardModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        dashboardTable = new JTable(dashboardModel);
        setupTableStyling(dashboardTable);
        dashboardScroll = new JScrollPane(dashboardTable);
        dashboardScroll.setBounds(30, 245, 1180, 410);
        contentPanel.add(dashboardScroll);

        recordsModel = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        recordsTable = new JTable(recordsModel);
        setupTableStyling(recordsTable);
        recordsScroll = new JScrollPane(recordsTable);
        recordsScroll.setBounds(30, 20, 1180, 580);
        recordsScroll.setVisible(false);
        contentPanel.add(recordsScroll);

        deleteRecordButton.setBounds(30, 610, 220, 35);
        deleteRecordButton.setBackground(new Color(200, 50, 50));
        deleteRecordButton.setForeground(Color.WHITE);
        deleteRecordButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteRecordButton.setFocusPainted(false);
        deleteRecordButton.addActionListener(this);
        deleteRecordButton.setVisible(false);
        contentPanel.add(deleteRecordButton);

        String[] busCols = {"Route Code", "Origin", "Destination", "Bus Terminal", "Base Fare"};
        busModel = new DefaultTableModel(busCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        busTable = new JTable(busModel);
        setupTableStyling(busTable);
        loadBusFleetData();
        busScroll = new JScrollPane(busTable);
        busScroll.setBounds(30, 20, 1180, 635);
        busScroll.setVisible(false);
        contentPanel.add(busScroll);

        String[] accCols = {"Account Role", "Operator ID", "Account Status"};
        accountsModel = new DefaultTableModel(accCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        accountsTable = new JTable(accountsModel);
        setupTableStyling(accountsTable);
        loadAccountStatuses();
        accountsScroll = new JScrollPane(accountsTable);
        accountsScroll.setBounds(30, 20, 1180, 580);
        accountsScroll.setVisible(false);
        contentPanel.add(accountsScroll);

        unblockButton.setBounds(30, 610, 220, 35);
        styleActionButton(unblockButton, primaryGreen);
        unblockButton.setVisible(false);
        contentPanel.add(unblockButton);

        blockButton.setBounds(265, 610, 220, 35);
        styleActionButton(blockButton, new Color(180, 50, 50));
        blockButton.setVisible(false);
        contentPanel.add(blockButton);

        createAccountButton.setBounds(500, 610, 220, 35);
        styleActionButton(createAccountButton, darkGreen);
        createAccountButton.setVisible(false);
        contentPanel.add(createAccountButton);

        rightContainer.add(contentPanel, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);
        loadTransactionRecords("ALL", "ALL");
        setVisible(true);
    }

    private void styleSidebarButton(JButton b, int y) {
        b.setBounds(15, y, 210, 40);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(new EmptyBorder(0, 15, 0, 0));
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setForeground(Color.DARK_GRAY);
        b.addActionListener(this);
        sidebarPanel.add(b);
    }

    private void setupFilterButton(JButton b, int x, int y, int w) {
        b.setBounds(x, y, w, 30);
        b.setBackground(darkGreen);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.addActionListener(this);
        filterPanel.add(b);
    }

    private void styleActionButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.addActionListener(this);
    }

    private void setActiveMenu(JButton selectedButton) {
        JButton[] buttons = {dashboardButton, recordsButton, busDetailsButton, accountsButton, settingsButton};
        for (JButton b : buttons) {
            if (b.equals(selectedButton)) {
                b.setBackground(primaryGreen);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(Color.WHITE);
                b.setForeground(Color.DARK_GRAY);
            }
        }
    }

    private JPanel createStatCard(String text, JLabel label, int x, int y, int w, int h) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBounds(x, y, w, h);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JLabel cardTitle = new JLabel(" " + text.toUpperCase());
        cardTitle.setFont(new Font("Arial", Font.BOLD, 11));
        cardTitle.setForeground(Color.GRAY);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(darkGreen);
        card.add(cardTitle, BorderLayout.NORTH);
        card.add(label, BorderLayout.CENTER);
        return card;
    }

    private void setupTableStyling(JTable table) {
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(darkGreen);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setResizingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    public static String getStoredAdminPassword() {
        File file = new File(PASS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String p = reader.readLine();
                if (p != null) return p.trim();
            } catch (IOException e) {}
        }
        return DEFAULT_PASS;
    }

    public static boolean saveAdminPassword(String newPassword) {
        File folder = new File(Records.FOLDER);
        if (!folder.exists()) folder.mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASS_FILE))) {
            writer.write(newPassword.trim());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void loadAccountStatuses() {
        accountsModel.setRowCount(0);
        for (String[] t : Terminal.getAllTerminals()) {
            accountsModel.addRow(new Object[]{
                "Cashier - " + t[0],
                t[1],
                Terminal.getStatus(t[0])
            });
        }
    }

    private void loadBusFleetData() {
        busModel.setRowCount(0);
        List<String[]> terminals = Terminal.getAllTerminals();
        for (String[] t : terminals) {
            if (!t[0].equalsIgnoreCase("Baliwag")) {
                String code1 = "B-" + t[0].charAt(0);
                String code2 = "" + t[0].charAt(0) + "-B";
                busModel.addRow(new Object[]{code1, "Baliwag", t[0], "Operational", "PHP 135.00"});
                busModel.addRow(new Object[]{code2, t[0], "Baliwag", "Operational", "PHP 135.00"});
            }
        }
    }

    private void createNewCashierAccount() {
        JPasswordField adminPassField = new JPasswordField(15);
        JPanel adminPanel = new JPanel();
        adminPanel.add(new JLabel("Enter Admin Password:"));
        adminPanel.add(adminPassField);
        int opt = JOptionPane.showConfirmDialog(this, adminPanel, "Admin Authorization", JOptionPane.OK_CANCEL_OPTION);
        if (opt != JOptionPane.OK_OPTION) return;

        if (!new String(adminPassField.getPassword()).trim().equals(getStoredAdminPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect Admin Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField termField = new JTextField(15);
        JTextField opIdField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JTextField stopsField = new JTextField(25);
        
        JPanel form = new JPanel(new GridLayout(8, 1, 5, 5));
        form.add(new JLabel("Terminal Name (e.g., Pampanga):"));
        form.add(termField);
        form.add(new JLabel("Operator ID (e.g., CC_PP):"));
        form.add(opIdField);
        form.add(new JLabel("Initial Password:"));
        form.add(passField);
        form.add(new JLabel("Enter 5 Bus Stops separated by comma:"));
        form.add(stopsField);

        int formOpt = JOptionPane.showConfirmDialog(this, form, "Create New Account & Stops", JOptionPane.OK_CANCEL_OPTION);
        if (formOpt != JOptionPane.OK_OPTION) return;

        String name = termField.getText().trim();
        String opId = opIdField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        String stops = stopsField.getText().trim();

        // DEFENSIVE PROGRAMMING (input validation)
        if (name.isEmpty() || opId.isEmpty() || pass.isEmpty() || stops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Terminal.addTerminal(name, opId, pass, stops)) {
            JOptionPane.showMessageDialog(this, "Account and 5 bus stops created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAccountStatuses();
            loadBusFleetData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save account.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void unblockCashierAccount() {
        int row = accountsTable.getSelectedRow();
        // DEFENSIVE PROGRAMMING (selection validation)
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String[]> terminals = Terminal.getAllTerminals();
        String terminal = terminals.get(row)[0];

        JPasswordField pf = new JPasswordField(15);
        JPanel p = new JPanel(); p.add(new JLabel("Admin Password:")); p.add(pf);
        if (JOptionPane.showConfirmDialog(this, p, "Authorize", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        
        if (!new String(pf.getPassword()).trim().equals(getStoredAdminPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Terminal.setStatus(terminal, "ACTIVE");
        Terminal.setAttempts(terminal, 0);
        loadAccountStatuses();
        JOptionPane.showMessageDialog(this, terminal + " account unblocked!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void blockCashierAccount() {
        int row = accountsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an account from table to block.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String[]> terminals = Terminal.getAllTerminals();
        String terminal = terminals.get(row)[0];

        if (Terminal.getStatus(terminal).equalsIgnoreCase("BLOCKED")) {
            JOptionPane.showMessageDialog(this, "The " + terminal + " account is ALREADY BLOCKED.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPasswordField pf = new JPasswordField(15);
        JPanel p = new JPanel(); p.add(new JLabel("Enter Admin Password:")); p.add(pf);
        if (JOptionPane.showConfirmDialog(this, p, "Admin Authorization", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        
        if (!new String(pf.getPassword()).trim().equals(getStoredAdminPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect Admin Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Terminal.setStatus(terminal, "BLOCKED");
        loadAccountStatuses();
        JOptionPane.showMessageDialog(this, "The " + terminal + " cashier account is now BLOCKED!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadTransactionRecords(String oF, String dF) {
        dashboardModel.setRowCount(0);
        int totalPax = 0; double totalRev = 0; int rNo = 1;
        File file = new File(Records.FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] d = line.split("\\|", -1);
                    if (d.length < 6) continue;
                    try {
                        String orig = d[2].trim();
                        String dest = d[3].trim();
                        
                        if (!oF.equals("ALL")) {
                            if (!orig.equalsIgnoreCase(oF) || !dest.equalsIgnoreCase(dF)) continue;
                        }
                        
                        int pax = Integer.parseInt(d[4].trim());
                        double amt = Double.parseDouble((d.length >= 8 ? d[7] : d[d.length - 1]).trim());
                        dashboardModel.addRow(new Object[]{rNo++, d[0], d[1], orig, dest, pax, d[5], d.length >= 7 ? d[6] : "N/A", String.format("PHP %.2f", amt)});
                        totalPax += pax; totalRev += amt;
                    } catch (NumberFormatException e) {}
                }
            } catch (Exception e) {}
        }
        ticketValueLabel.setText(totalPax + " PAX");
        revenueValueLabel.setText(String.format("PHP %.2f", totalRev));
    }

    private void loadAllFullRecords() {
        recordsModel.setRowCount(0);
        int rNo = 1;
        File file = new File(Records.FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] d = line.split("\\|", -1);
                    if (d.length < 6) continue;
                    try {
                        recordsModel.addRow(new Object[]{rNo++, d[0], d[1], d[2], d[3], Integer.parseInt(d[4].trim()), d[5], d.length >= 7 ? d[6] : "N/A", String.format("PHP %.2f", Double.parseDouble((d.length >= 8 ? d[7] : d[d.length - 1]).trim()))});
                    } catch (NumberFormatException e) {}
                }
            } catch (Exception e) {}
        }
    }

    private void deleteSelectedRecord() {
        int row = recordsTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a record to archive.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Archive the selected transaction? It will be removed from active records.",
            "Confirm Archive", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        JPasswordField pf = new JPasswordField(15);
        JPanel p = new JPanel(); p.add(new JLabel("Admin Password:")); p.add(pf);
        if (JOptionPane.showConfirmDialog(this, p, "Authorize", JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) return;
        if (!new String(pf.getPassword()).trim().equals(getStoredAdminPassword())) {
            JOptionPane.showMessageDialog(this, "Incorrect Password!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int selectedRow = recordsTable.convertRowIndexToModel(row);
        if (archiveSelectedRecord(selectedRow)) {
            loadAllFullRecords();
            loadTransactionRecords("ALL", "ALL");
            JOptionPane.showMessageDialog(this, "Transaction archived successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to archive transaction.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean archiveSelectedRecord(int selectedRow) {
        File source = new File(Records.FILE);
        File archive = new File(Records.FOLDER + "archived_records.txt");
        if (!source.exists()) return false;

        try {
            List<String> lines = new java.util.ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(source))) {
                String line;
                while ((line = reader.readLine()) != null) lines.add(line);
            }

            int validRow = 0;
            String selectedLine = null;
            List<String> remainingLines = new java.util.ArrayList<>();

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    remainingLines.add(line);
                    continue;
                }

                String[] d = line.split("\\|", -1);
                boolean validRecord = false;
                if (d.length >= 6) {
                    try {
                        Integer.parseInt(d[4].trim());
                        Double.parseDouble((d.length >= 8 ? d[7] : d[d.length - 1]).trim());
                        validRecord = true;
                    } catch (NumberFormatException ex) {
                        validRecord = false;
                    }
                }

                if (validRecord && validRow == selectedRow) {
                    selectedLine = line;
                } else {
                    remainingLines.add(line);
                }
                if (validRecord) validRow++;
            }

            if (selectedLine == null) return false;

            File folder = new File(Records.FOLDER);
            if (!folder.exists()) folder.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archive, true))) {
                writer.write(selectedLine);
                writer.newLine();
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(source))) {
                for (String line : remainingLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private void switchView(String viewName) {
        card1.setVisible(viewName.equals("DASHBOARD"));
        card2.setVisible(viewName.equals("DASHBOARD"));
        card3.setVisible(viewName.equals("DASHBOARD"));
        card4.setVisible(viewName.equals("DASHBOARD"));
        filterPanel.setVisible(viewName.equals("DASHBOARD"));
        dashboardScroll.setVisible(viewName.equals("DASHBOARD"));
        recordsScroll.setVisible(viewName.equals("RECORDS"));
        deleteRecordButton.setVisible(viewName.equals("RECORDS"));
        busScroll.setVisible(viewName.equals("BUSES"));
        accountsScroll.setVisible(viewName.equals("ACCOUNTS"));
        unblockButton.setVisible(viewName.equals("ACCOUNTS"));
        blockButton.setVisible(viewName.equals("ACCOUNTS"));
        createAccountButton.setVisible(viewName.equals("ACCOUNTS"));
    }

    private void openSettingsDialog() {
        final JDialog settingsDialog = new JDialog(this, "Admin Settings", true);
        settingsDialog.setSize(450, 360);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(null);
        settingsDialog.getContentPane().setBackground(Color.WHITE);

        JLabel dialogTitle = new JLabel("System Settings", SwingConstants.CENTER);
        dialogTitle.setFont(new Font("Arial", Font.BOLD, 18));
        dialogTitle.setForeground(darkGreen);
        dialogTitle.setBounds(0, 15, 450, 30);
        settingsDialog.add(dialogTitle);

        JButton changePasswordButton = new JButton("Change System Passwords");
        changePasswordButton.setBounds(75, 60, 300, 40);
        styleActionButton(changePasswordButton, primaryGreen);
        changePasswordButton.addActionListener(e -> {
            List<String[]> terminals = Terminal.getAllTerminals();
            String[] options = new String[terminals.size() + 1];
            options[0] = "Admin Login";
            for (int i = 0; i < terminals.size(); i++) {
                options[i + 1] = "Cashier - " + terminals.get(i)[0];
            }
            String selected = (String) JOptionPane.showInputDialog(
                settingsDialog, "Select account to update:", "Change Password",
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]
            );
            if (selected == null) return;

            JPasswordField currentField = new JPasswordField(15);
            JPasswordField newField = new JPasswordField(15);
            JPasswordField confirmField = new JPasswordField(15);
            JPanel panel = new JPanel(new GridLayout(6, 1, 5, 5));
            panel.add(new JLabel("Enter Current Password:"));
            panel.add(currentField);
            panel.add(new JLabel("Enter New Password:"));
            panel.add(newField);
            panel.add(new JLabel("Confirm New Password:"));
            panel.add(confirmField);

            int result = JOptionPane.showConfirmDialog(settingsDialog, panel, "Update Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            String currentInput = new String(currentField.getPassword()).trim();
            String newInput = new String(newField.getPassword()).trim();
            String confirmInput = new String(confirmField.getPassword()).trim();

            if (selected.equals("Admin Login")) {
                if (!currentInput.equals(getStoredAdminPassword().trim())) {
                    JOptionPane.showMessageDialog(settingsDialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newInput.isEmpty() || !newInput.equals(confirmInput)) {
                    JOptionPane.showMessageDialog(settingsDialog, "New passwords do not match or empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (saveAdminPassword(newInput)) {
                    JOptionPane.showMessageDialog(settingsDialog, "Admin password updated successfully!");
                }
            } else {
                String terminal = selected.substring("Cashier - ".length());
                if (!currentInput.equals(Terminal.getPassword(terminal).trim())) {
                    JOptionPane.showMessageDialog(settingsDialog, "Incorrect current password!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newInput.isEmpty() || !newInput.equals(confirmInput)) {
                    JOptionPane.showMessageDialog(settingsDialog, "New passwords do not match or empty!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (Terminal.savePassword(terminal, newInput)) {
                    JOptionPane.showMessageDialog(settingsDialog, "Cashier password for " + terminal + " updated successfully!");
                }
            }
        });
        settingsDialog.add(changePasswordButton);

        JButton exportButton = new JButton("Export Transaction Summary");
        exportButton.setBounds(75, 110, 300, 40);
        styleActionButton(exportButton, primaryGreen);
        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Text File");
            fileChooser.setSelectedFile(new File("Baliwag_Transactions.txt"));
            int choice = fileChooser.showSaveDialog(settingsDialog);
            if (choice == JFileChooser.APPROVE_OPTION) {
                File target = fileChooser.getSelectedFile();
                File source = new File(Records.FILE);
                if (!source.exists()) {
                    JOptionPane.showMessageDialog(settingsDialog, "No records found to export!", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(source));
                     BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().isEmpty()) continue;
                        writer.write(line);
                        writer.newLine();
                    }
                    JOptionPane.showMessageDialog(settingsDialog, "Export successful!\nSaved at: " + target.getAbsolutePath());
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(settingsDialog, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        settingsDialog.add(exportButton);

        JButton aboutButton = new JButton("About System");
        aboutButton.setBounds(75, 160, 300, 40);
        styleActionButton(aboutButton, primaryGreen);
        aboutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(settingsDialog, "Baliwag Transit Ticketing System\nDeveloped by Group 6", "About System", JOptionPane.INFORMATION_MESSAGE);
        });
        settingsDialog.add(aboutButton);

        JButton closeButton = new JButton("Close Settings");
        closeButton.setBounds(150, 220, 150, 35);
        styleActionButton(closeButton, new Color(180, 50, 50));
        closeButton.addActionListener(e -> settingsDialog.dispose());
        settingsDialog.add(closeButton);

        settingsDialog.setVisible(true);
    }

    @Override
    // INTERFACE IMPLEMENTATION
    public void actionPerformed(ActionEvent e) {
        Object s = e.getSource();
        if (s.equals(logoutButton)) {
            new BusLogin().setVisible(true); this.dispose();
        } else if (s.equals(dashboardButton)) {
            setActiveMenu(dashboardButton); titleLabel.setText("Good Day, Admin!"); switchView("DASHBOARD"); loadTransactionRecords("ALL", "ALL");
        } else if (s.equals(recordsButton)) {
            setActiveMenu(recordsButton); titleLabel.setText("Transaction Records Summary"); loadAllFullRecords(); switchView("RECORDS");
        } else if (s.equals(busDetailsButton)) {
            setActiveMenu(busDetailsButton); titleLabel.setText("Bus Fleet & Route Details"); switchView("BUSES");
        } else if (s.equals(deleteRecordButton)) {
            deleteSelectedRecord();
        } else if (s.equals(accountsButton)) {
            setActiveMenu(accountsButton); titleLabel.setText("Account Management & Status"); loadAccountStatuses(); switchView("ACCOUNTS");
        } else if (s.equals(unblockButton)) {
            unblockCashierAccount();
        } else if (s.equals(blockButton)) {
            blockCashierAccount();
        } else if (s.equals(createAccountButton)) {
            createNewCashierAccount();
        } else if (s.equals(settingsButton)) {
            setActiveMenu(settingsButton); openSettingsDialog();
        } else if (s.equals(filterBaliwagCubao)) { 
            routeValueLabel.setText("BALIWAG - CUBAO"); loadTransactionRecords("Baliwag", "Cubao"); 
        } else if (s.equals(filterBaliwagGracePark)) { 
            routeValueLabel.setText("BALIWAG - GRACE PARK"); loadTransactionRecords("Baliwag", "Grace Park"); 
        } else if (s.equals(filterBaliwagDivisoria)) { 
            routeValueLabel.setText("BALIWAG - DIVISORIA"); loadTransactionRecords("Baliwag", "Divisoria"); 
        } else if (s.equals(filterCubaoBaliwag)) { 
            routeValueLabel.setText("CUBAO - BALIWAG"); loadTransactionRecords("Cubao", "Baliwag"); 
        } else if (s.equals(filterGraceParkBaliwag)) { 
            routeValueLabel.setText("GRACE PARK - BALIWAG"); loadTransactionRecords("Grace Park", "Baliwag"); 
        } else if (s.equals(filterDivisoriaBaliwag)) { 
            routeValueLabel.setText("DIVISORIA - BALIWAG"); loadTransactionRecords("Divisoria", "Baliwag"); 
        } else if (s.equals(filterShowAll)) { 
            routeValueLabel.setText("ALL BUSES"); loadTransactionRecords("ALL", "ALL"); 
        }
    }

    public static void main(String[] args) {
        new BusAdmin();
    }
}
