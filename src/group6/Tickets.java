package group4;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

// INHERITANCE
public class Tickets extends JDialog {
    // ENCAPSULATION:
    private String origin;
    private String destination;
    private String destinationStop;
    private double stopFare;
    private String busName;
    private String[] passengerTypes;
    private String[] passengerIDs;
    private String[] seatNumbers;
    private double totalAmount;
    private String paymentInfo;
    private String paymentMethod;
    private double amountPaid;
    private int seatsRemaining;
    private String dateTime;
    private JButton print;
    private JPanel receiptContainer;
    private Runnable onPrintSuccess;
    private final Color yellow = new Color(255, 204, 0);
    private final Color darkArmyGreen = new Color(38, 54, 23);
    private final Color separatorBlue = new Color(90, 120, 180);
    private static final int WINDOW_WIDTH = 750;
    private static final int WINDOW_HEIGHT = 750;
    private static final int RECEIPT_WIDTH = 680;
    private static final int RECEIPT_PADDING = 15;
    private static final int INFO_LEFT_PADDING = 20;

    public Tickets(
            String origin,
            String destination,
            String destinationStop,
            double stopFare,
            String busName,
            String[] passengerTypes,
            String[] passengerIDs,
            String[] seatNumbers,
            double totalAmount,
            String paymentInfo,
            String paymentMethod,
            double amountPaid,
            int seatsRemaining) {
        super((Frame) null, "Individual Bus Receipts", false);
        this.origin = origin;
        this.destination = destination;
        this.destinationStop = destinationStop;
        this.stopFare = stopFare;
        this.busName = busName;
        this.passengerTypes = passengerTypes == null ? new String[0] : passengerTypes.clone();
        this.passengerIDs = passengerIDs == null ? new String[0] : passengerIDs.clone();
        this.seatNumbers = seatNumbers == null ? new String[0] : seatNumbers.clone();
        this.totalAmount = totalAmount;
        this.paymentInfo = paymentInfo == null ? "" : paymentInfo;
        this.paymentMethod = paymentMethod == null ? "" : paymentMethod;
        this.amountPaid = amountPaid;
        this.seatsRemaining = seatsRemaining;
        this.dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        setUndecorated(true);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Color.WHITE);
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.WHITE);
        titleBar.setPreferredSize(new Dimension(WINDOW_WIDTH, 75));
        titleBar.setBorder(new MatteBorder(0, 0, 2, 0, darkArmyGreen));
        JLabel title = new JLabel("BALIWAG TRANSIT, INC.", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(darkArmyGreen);
        title.setOpaque(true);
        title.setBackground(Color.WHITE);
        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(180, 50, 50));
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setPreferredSize(new Dimension(45, 35));
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());
        JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        closePanel.setBackground(Color.WHITE);
        closePanel.add(closeButton);
        titleBar.add(title, BorderLayout.CENTER);
        titleBar.add(closePanel, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);
        receiptContainer = new JPanel();
        receiptContainer.setLayout(new BoxLayout(receiptContainer, BoxLayout.Y_AXIS));
        receiptContainer.setBackground(Color.WHITE);
        receiptContainer.setBorder(new EmptyBorder(10, 5, 10, 5));
        for (int i = 0; i < this.passengerTypes.length; i++) {
            JPanel ticket = createReceipt(i);
            receiptContainer.add(ticket);
            if (i < this.passengerTypes.length - 1) {
                receiptContainer.add(Box.createVerticalStrut(15));
            }
        }
        JScrollPane scrollPane = new JScrollPane(receiptContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        JTextArea instruction = new JTextArea();
        instruction.setEditable(false);
        instruction.setFocusable(false);
        instruction.setFont(new Font("Arial", Font.PLAIN, 12));
        instruction.setForeground(Color.BLACK);
        instruction.setBackground(Color.WHITE);
        instruction.setBorder(new EmptyBorder(5, 10, 5, 10));
        instruction.setRows(1);
        instruction.setLineWrap(false);
        instruction.setWrapStyleWord(false);
        print = new JButton("PRINT TICKETS");
        print.setFont(new Font("Arial", Font.BOLD, 16));
        print.setBackground(yellow);
        print.setForeground(Color.BLACK);
        print.setFocusPainted(false);
        print.setBorder(BorderFactory.createLineBorder(darkArmyGreen, 1));
        print.setPreferredSize(new Dimension(180, 36));
        print.setMinimumSize(new Dimension(180, 36));
        print.setMaximumSize(new Dimension(180, 36));
        print.addActionListener(e -> saveTickets());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 45));
        buttonPanel.add(print);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(instruction, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, 75));
        add(bottomPanel, BorderLayout.SOUTH);
        getContentPane().setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        pack();
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private JPanel createReceipt(int index) {
        JPanel receipt = new JPanel();
        receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
        receipt.setBackground(Color.WHITE);
        receipt.setBorder(new CompoundBorder(new LineBorder(darkArmyGreen, 2), new EmptyBorder(12, INFO_LEFT_PADDING, 12, RECEIPT_PADDING)));
        receipt.setAlignmentX(Component.CENTER_ALIGNMENT);
        String passengerType = getValue(passengerTypes, index);
        String passengerID = getValue(passengerIDs, index);
        String seatNumber = getValue(seatNumbers, index);
        String passengerStop = getPassengerStop();
        double regularFare = getRegularFare();
        double discountAmount = 0.0;
        double passengerFare = regularFare;
        if (isDiscounted(passengerType)) {
            discountAmount = regularFare * 0.20;
            passengerFare = regularFare - discountAmount;
        }
        addHeading(receipt, "BALIWAG TRANSIT, INC.", 22);
        addHeading(receipt, "INDIVIDUAL BUS TICKET", 16);
        addReceiptLine(receipt, "Date and Time: " + dateTime);
        addReceiptLine(receipt, "Bus: " + busName);
        addReceiptLine(receipt, "Origin: " + origin);
        addReceiptLine(receipt, "Destination: " + destination);
        addReceiptLine(receipt, "Passenger Stop: " + passengerStop);
        addReceiptLine(receipt, "Passenger Type: " + passengerType);
        addReceiptLine(receipt, "Passenger ID: " + passengerID);
        addReceiptLine(receipt, "Seat Number: " + seatNumber);
        addSeparator(receipt);
        addReceiptLine(receipt, String.format("Regular Fare: PHP %.2f", regularFare));
        if (discountAmount > 0) {
            addReceiptLine(receipt,
                    String.format("Discount (20%%): -PHP %.2f", discountAmount));
        } else {
            addReceiptLine(receipt, "Discount: PHP 0.00");
        }
        addReceiptLine(receipt, String.format("Passenger Fare: PHP %.2f", passengerFare), true);
        addSeparator(receipt);
        addReceiptLine(receipt, "Payment Method: " + paymentMethod);
        addPaymentInfo(receipt);
        addReceiptLine(receipt, String.format("Total Amount: PHP %.2f", passengerFare), true);
        addReceiptLine(receipt, "Seats Remaining: " + seatsRemaining);
        addReceiptLine(receipt, "PLEASE PRESENT YOUR TICKET TO THE CONDUCTOR UPON BOARDING.");
        int height = receipt.getPreferredSize().height;
        receipt.setPreferredSize(new Dimension(RECEIPT_WIDTH, height));
        receipt.setMinimumSize(new Dimension(RECEIPT_WIDTH, height));
        receipt.setMaximumSize(new Dimension(RECEIPT_WIDTH, height));
        return receipt;
    }

    private void addHeading(JPanel panel, String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.BOLD, size));
        label.setForeground(darkArmyGreen);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(3, 0, 3, 0));
        panel.add(label);
    }

    private void addReceiptLine(
            JPanel panel, String text) {
        addReceiptLine(panel, text, false);
    }

    private void addReceiptLine(
            JPanel panel, String text, boolean bold) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, bold ? 16 : 14));
        label.setForeground(darkArmyGreen);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(
                new EmptyBorder(4, 0, 4, 0));
        panel.add(label);
    }

    private void addSeparator(JPanel panel) {
        JSeparator separator = new JSeparator();
        separator.setForeground(separatorBlue);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        panel.add(Box.createVerticalStrut(4));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(4));
    }

    private boolean isDiscounted(String passengerType) {
        // DEFENSIVE PROGRAMMING (null check)
        if (passengerType == null) {
            return false;
        }
        return passengerType.equalsIgnoreCase("Student") || passengerType.equalsIgnoreCase("Senior Citizen") || passengerType.equalsIgnoreCase("PWD");
    }

    private double getRegularFare() {
        if (stopFare >= 0) {
            return stopFare;
        }
        if (passengerTypes.length == 0) {
            return 0.0;
        }
        double totalWeight = 0.0;
        for (String type : passengerTypes) {
            totalWeight += isDiscounted(type)
                    ? 0.80 : 1.00;
        }
        return totalWeight == 0.0
                ? 0.0
                : totalAmount / totalWeight;
    }

    private double getPassengerFare(int index) {
        double fare = getRegularFare();
        if (isDiscounted(getValue(passengerTypes, index))) {
            fare *= 0.80;
        }
        return fare;
    }

    private String getPassengerStop() {
        // DEFENSIVE PROGRAMMING (null/blank/sentinel validation)
        if (destinationStop == null || destinationStop.trim().isEmpty() || destinationStop.equalsIgnoreCase("None") || destinationStop.equalsIgnoreCase("N/A")) {
            return destination == null ? "N/A" : destination;
        }
        return destinationStop;
    }

    private String getValue(String[] values, int index) {
        // DEFENSIVE PROGRAMMING (array bounds/null checks)
        if (values == null || index < 0 || index >= values.length || values[index] == null || values[index].trim().isEmpty()) {
            return "N/A";
        }
        return values[index];
    }

    private void addPaymentInfo(JPanel panel) {
        String info = paymentInfo.trim();
        boolean hasCash = false;
        boolean hasChange = false;
        if (!info.isEmpty()) {
            String[] lines = info.split("\\r?\\n");
            for (String item : lines) {
                String line = item.trim();
                if (line.isEmpty()
                        || line.toLowerCase().startsWith("payment method:")) {
                    continue;
                }
                line = maskAccountNumber(line);
                if (line.toLowerCase().startsWith("cash rendered:")) {
                    hasCash = true;
                }
                if (line.toLowerCase().startsWith("change:")) {
                    hasChange = true;
                }
                addReceiptLine(panel, line);
            }
        }
        if (paymentMethod.equalsIgnoreCase("Cash")) {
            if (!hasCash) {
                addReceiptLine(panel, String.format("Cash Rendered: PHP %.2f", amountPaid));
            }
            if (!hasChange) {
                addReceiptLine(panel,
                        String.format("Change: PHP %.2f", Math.max(0.0, amountPaid - totalAmount)));
            }
        }
    }

    private String maskAccountNumber(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("number")
                || lower.contains("account")
                || lower.contains("instapay")
                || lower.contains("card")) {
            int colon = text.indexOf(':');
            if (colon >= 0 && colon < text.length() - 1) {
                String label = text.substring(0, colon + 1);
                String value = text.substring(colon + 1).trim();
                if (value.length() > 4) {
                    return label + " ****" + value.substring(value.length() - 4);
                }
            }
        }
        return text;
    }

    public void setOnPrintSuccess(Runnable callback) {
        this.onPrintSuccess = callback;
    }

    private void saveTickets() {
        if (passengerTypes.length == 0) {
            JOptionPane.showMessageDialog(this, "No passenger tickets to print!");
            return;
        }
        try (FileWriter writer = new FileWriter("tickets.txt", true)) {
            for (int i = 0; i < passengerTypes.length; i++) {
                String passengerType = getValue(passengerTypes, i);
                String passengerID = getValue(passengerIDs, i);
                String seatNumber = getValue(seatNumbers, i);
                String passengerStop = getPassengerStop();
                double regularFare = getRegularFare();
                double discountAmount = isDiscounted(passengerType) ? regularFare * 0.20 : 0.0;
                double passengerFare = regularFare - discountAmount;
                writer.write("========================================\n");
                writer.write("BALIWAG TRANSIT, INC.\n");
                writer.write("INDIVIDUAL BUS TICKET\n");
                writer.write("Date and Time: " + dateTime + "\n");
                writer.write("Bus: " + busName + "\n");
                writer.write("Origin: " + origin + "\n");
                writer.write("Destination: " + destination + "\n");
                writer.write("Passenger Stop: " + passengerStop + "\n");
                writer.write("Passenger Type: " + passengerType + "\n");
                writer.write("Passenger ID: " + passengerID + "\n");
                writer.write("Seat Number: " + seatNumber + "\n");
                writer.write(String.format("Regular Fare: PHP %.2f%n", regularFare));
                if (discountAmount > 0) {
                    writer.write(String.format("Discount (20%%): -PHP %.2f%n", discountAmount));
                } else {
                    writer.write("Discount: PHP 0.00\n");
                }
                writer.write(String.format("Passenger Fare: PHP %.2f%n", passengerFare));
                writer.write("Payment Method: " + paymentMethod + "\n");
                String info = paymentInfo.trim();
                boolean hasCash = false;
                boolean hasChange = false;
                if (!info.isEmpty()) {
                    for (String item : info.split("\\r?\\n")) {
                        String line = item.trim();
                        if (line.isEmpty()
                                || line.toLowerCase()
                                        .startsWith("payment method:")) {
                            continue;
                        }
                        String lower = line.toLowerCase();
                        if (lower.startsWith("cash rendered:")) {
                            hasCash = true;
                        }
                        if (lower.startsWith("change:")) {
                            hasChange = true;
                        }
                        writer.write(
                                maskAccountNumber(line) + "\n");
                    }
                }
                if (paymentMethod.equalsIgnoreCase("Cash")) {
                    if (!hasCash) {
                        writer.write(String.format("Cash Rendered: PHP %.2f%n", amountPaid));
                    }
                    if (!hasChange) {
                        writer.write(String.format("Change: PHP %.2f%n", Math.max(0.0, amountPaid - totalAmount)));
                    }
                }
                writer.write(String.format("Total Amount: PHP %.2f%n", passengerFare));
                writer.write("Seats Remaining: " + seatsRemaining + "\n");
                writer.write("========================================\n\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving tickets: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        print.setEnabled(false);
        JOptionPane.showMessageDialog(
                this,
                "Print successful!");
        // DEFENSIVE PROGRAMMING (null check)
        if (onPrintSuccess != null) {
            onPrintSuccess.run();
        }
        dispose();
    }
}
