package group4;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// INHERITANCE
public class Records extends JFrame {
    public static final String FOLDER = "TicketRecords";
    public static final String FILE = FOLDER + "/transactions.txt";
    // ENCAPSULATION
    private final Color armyGreenBg = new Color(59, 83, 35);
    private final Color yellow = new Color(255, 204, 0);
    private final Color darkArmyGreen = new Color(38, 54, 23);
    private final JLabel totalTickets = new JLabel("TOTAL TICKETS BOUGHT: 0");
    private final JLabel totalSales = new JLabel("TOTAL AMOUNT: ₱0.00");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{
                "No.",
                "Bus Number",
                "Date & Time",
                "Origin",
                "Destination",
                "Passengers",
                "Payment",
                "Account Number",
                "Amount"
            }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);

    public Records() {
        setTitle("Baliwag Ticketing System - Records - " + Terminal.getTitle());
        setSize(1400, 650);
        setMinimumSize(new Dimension(950, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(armyGreenBg);
        setLayout(new BorderLayout());
        JPanel transactionPanel = new JPanel(new BorderLayout(10, 12));
        transactionPanel.setBackground(armyGreenBg);
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel title = new JLabel("TRANSACTION RECORDS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setForeground(yellow);
        transactionPanel.add(title, BorderLayout.NORTH);
        styleTable();
        int[] preferredWidths = {45, 110, 165, 115, 280, 90, 100, 200, 115};
        for (int i = 0; i < preferredWidths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(preferredWidths[i]);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane transactionScroll = new JScrollPane(table);
        transactionScroll.setPreferredSize(new Dimension(1300, 400));
        transactionPanel.add(transactionScroll, BorderLayout.CENTER);
        JPanel totalsPanel = new JPanel();
        totalsPanel.setLayout(new BoxLayout(totalsPanel, BoxLayout.Y_AXIS));
        totalsPanel.setBackground(armyGreenBg);
        totalsPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        totalTickets.setFont(new Font("Arial", Font.BOLD, 18));
        totalTickets.setForeground(yellow);
        totalSales.setFont(new Font("Arial", Font.BOLD, 18));
        totalSales.setForeground(yellow);
        totalsPanel.add(totalTickets);
        totalsPanel.add(Box.createVerticalStrut(8));
        totalsPanel.add(totalSales);
        transactionPanel.add(totalsPanel, BorderLayout.SOUTH);
        add(transactionPanel, BorderLayout.CENTER);
        loadRecords();
        setVisible(true);
    }

    private void styleTable() {
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(32);
        table.setSelectionBackground(yellow);
        table.setSelectionForeground(Color.BLACK);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );
        table.getTableHeader().setBackground(darkArmyGreen);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public static void addTransaction(String origin, String destination, int passengers, String payment, double amount, String busNumber, String paymentInfo) {
        String dateTime = getCurrentDateTime();
        String accountNumber = extractAccountNumber(paymentInfo, payment);
        if (!createFolder()) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE, true))) {
            writer.write(clean(busNumber) + "|"
                    + clean(dateTime) + "|"
                    + clean(origin) + "|"
                    + clean(destination) + "|"
                    + passengers + "|"
                    + clean(payment) + "|"
                    + clean(accountNumber) + "|"
                    + String.format(Locale.US, "%.2f", amount) + "|"
                    + clean(Terminal.getCurrent())
            );
            writer.newLine();
        } catch (IOException ex) {
            showFileError("saving transaction", ex);
        }
    }

    public static void addTransaction(String origin, String destination, int passengers, String payment, double amount, String busNumber) {
        addTransaction(origin, destination, passengers, payment, amount, busNumber, "");
    }

    public static void addTransaction(String origin, String destination, int passengers, String payment, double amount) {
        addTransaction(origin, destination, passengers, payment, amount, "Not recorded", "");
    }

    public static void addTransaction(String destination, int passengers, String payment, double amount) {
        addTransaction("Not recorded", destination, passengers, payment, amount, "Not recorded", "");
    }

    private static String extractAccountNumber(String paymentInfo, String paymentMethod) {
        // DEFENSIVE PROGRAMMING (input validation)
        if (paymentMethod == null
                || paymentMethod.trim().equalsIgnoreCase("Cash")) {
            return "N/A";
        }
        // DEFENSIVE PROGRAMMING (null/blank validation)
        if (paymentInfo == null || paymentInfo.trim().isEmpty()) {
            return "N/A";
        }
        String[] labels = {
            "Instapay Account Number:",
            "Instapay Number:",
            "GCash Number:",
            "Card Account Number:",
            "Card Number:",
            "Card ID Number:",
            "Account Number:",
            "Reference Number:"
        };
        String info = paymentInfo.trim();
        for (String line : info.split("\\R")) {
            String trimmedLine = line.trim();
            for (String label : labels) {
                if (trimmedLine.toLowerCase(Locale.ROOT)
                        .startsWith(label.toLowerCase(Locale.ROOT))) {
                    String number = trimmedLine.substring(label.length()).trim();
                    return number.isEmpty() ? "N/A" : number;
                }
            }
        }
        if (!info.contains("\n") && !info.contains("\r") && !info.contains(":")) {
            return info;
        }
        return "N/A";
    }

    public void loadRecords() {
        model.setRowCount(0);
        int ticketCount = 0;
        double sales = 0.0;
        int rowNumber = 1;
        File file = new File(FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(
                    new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) {
                        continue;
                    }
                    String[] part = line.split("\\|", -1);
                    String recordTerminal = "Baliwag";
                    if (part.length >= 9 && !part[8].trim().isEmpty()) {
                        recordTerminal = part[8].trim();
                    }
                    if (!recordTerminal.equalsIgnoreCase(Terminal.getCurrent())) {
                        continue;
                    }
                    String busNumber = "Not recorded";
                    String dateTime = "Not recorded";
                    String origin = "Not recorded";
                    String destination = "";
                    String passengersText = "0";
                    String payment = "";
                    String accountNumber = "N/A";
                    String amountText = "0";
                    try {
                        if (part.length >= 8) {
                            busNumber = part[0];
                            dateTime = part[1];
                            origin = part[2];
                            destination = part[3];
                            passengersText = part[4];
                            payment = part[5];
                            accountNumber = part[6];
                            amountText = part[7];
                        } else if (part.length == 7) {
                            busNumber = part[0];
                            dateTime = part[1];
                            origin = part[2];
                            destination = part[3];
                            passengersText = part[4];
                            payment = part[5];
                            amountText = part[6];
                        } else if (part.length == 6) {
                            dateTime = part[0];
                            origin = part[1];
                            destination = part[2];
                            passengersText = part[3];
                            payment = part[4];
                            amountText = part[5];
                        } else if (part.length == 5) {
                            origin = part[0];
                            destination = part[1];
                            passengersText = part[2];
                            payment = part[3];
                            amountText = part[4];
                        } else {
                            continue;
                        }
                        int passengers = Integer.parseInt(
                                passengersText.trim()
                        );
                        double amount = Double.parseDouble(
                                amountText.trim()
                        );
                        model.addRow(new Object[]{
                            rowNumber++,
                            busNumber,
                            dateTime,
                            origin,
                            destination,
                            passengers,
                            payment,
                            accountNumber,
                            String.format(
                                    Locale.US, "₱%.2f", amount
                            )
                        });
                        ticketCount += passengers;
                        sales += amount;
                    // DEFENSIVE PROGRAMMING (exception handling)
                    } catch (NumberFormatException ex) {
                    }
                }
            } catch (IOException ex) {
                showFileError("loading transactions", ex);
            }
        }
        totalTickets.setText("TOTAL TICKETS BOUGHT: " + ticketCount);
        totalSales.setText(String.format(Locale.US, "TOTAL AMOUNT: ₱%.2f", sales));
    }

    private static String getCurrentDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static boolean createFolder() {
        File folder = new File(FOLDER);
        if (!folder.exists() && !folder.mkdirs()) {
            JOptionPane.showMessageDialog(null, "Unable to create the records folder.", "Folder Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private static String clean(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ").trim();
    }

    private static void showFileError(
            String action, IOException ex) {
        JOptionPane.showMessageDialog(null, "Error " + action + ":\n" + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
    }
}
