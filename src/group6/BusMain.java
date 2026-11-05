package group4;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

// INHERITANCE
// INTERFACE
public class BusMain extends JFrame implements ActionListener, ItemListener {
    // ENCAPSULATION
    private JLabel title = new JLabel("Baliwag Transit, INC");
    private JLabel passnum = new JLabel("No. of Passenger:");
    private JLabel Originfrom = new JLabel("Origin (FROM):");
    private JLabel destination = new JLabel("Destination (TO):");
    private JLabel paymentM = new JLabel("Payment Method:");
    private JLabel discount = new JLabel("Passenger Type:");
    private JTextField passnumText = new JTextField();
    private JComboBox<String> originBox = new JComboBox<>();
    private JTextArea passengerTypeText = new JTextArea();
    private JScrollPane passengerScroll = new JScrollPane(passengerTypeText);
    private JComboBox<String> destinationBox = new JComboBox<>();
    private JComboBox<String> paymentBox = new JComboBox<>();
    private Availability busAvailability = new Availability();
    private BusSeat busSeatPanel = new BusSeat();
    private JButton enter = new JButton("SELECT DISCOUNT");
    private JButton print = new JButton("PRINT TICKET");
    private JButton clear = new JButton("CLEAR ALL");
    private JButton records = new JButton("VIEW RECORDS");
    private JButton logout = new JButton("LOG OUT");
    private JLabel total = new JLabel("TOTAL DUE: PHP 0.00");
    private JLabel change = new JLabel("CHANGE: PHP 0.00");
    private String[] passengerTypes;
    private String[] passengerIDs;
    private int passengerCount = 0;
    private boolean isUpdatingDestinations = false;
    private String selectedStop = null;
    private double selectedStopFare = -1;
    // ABSTRACTION AND POLYMORPHISM
    private FareCalculator fareCalculator = new BaliwagFareCalculator();
    private String lastActiveBus = "None";
    private Color armyGreenBg = new Color(0, 100, 0);
    private Color yellow = new Color(255, 204, 0);
    private Color darkarmygreen = new Color(38, 54, 23);

    public BusMain() {
        this(Terminal.getCurrent());
    }

    public BusMain(String terminal) {
        this(getTerminalName(terminal), getInitialDestination(getTerminalName(terminal)));
    }

    public BusMain(String initialOrigin, String initialDestination) {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setTitle("Baliwag Transit - Cashier (" + initialOrigin + " Terminal)");
        setLayout(null);
        getContentPane().setBackground(armyGreenBg);

        title.setBounds(600, 30, Toolkit.getDefaultToolkit().getScreenSize().width, 40);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        add(title);

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        passnum.setBounds(40, 110, 250, 30);
        passnum.setFont(labelFont);
        passnum.setForeground(Color.WHITE);
        add(passnum);

        Originfrom.setBounds(40, 180, 250, 30);
        Originfrom.setFont(labelFont);
        Originfrom.setForeground(Color.WHITE);
        add(Originfrom);

        destination.setBounds(40, 250, 250, 30);
        destination.setFont(labelFont);
        destination.setForeground(Color.WHITE);
        add(destination);

        paymentM.setBounds(40, 320, 250, 30);
        paymentM.setFont(labelFont);
        paymentM.setForeground(Color.WHITE);
        add(paymentM);

        discount.setBounds(40, 400, 250, 30);
        discount.setFont(labelFont);
        discount.setForeground(Color.WHITE);
        add(discount);

        passnumText.setBounds(300, 100, 260, 45);
        passnumText.setFont(new Font("Arial", Font.PLAIN, 16));
        passnumText.setBackground(Color.WHITE);
        passnumText.setForeground(Color.BLACK);
        passnumText.setCaretColor(darkarmygreen);
        passnumText.setBorder(BorderFactory.createLineBorder(yellow, 2));
        add(passnumText);

        originBox.setBounds(300, 170, 400, 45);
        originBox.setFont(new Font("Arial", Font.PLAIN, 16));
        originBox.setBackground(Color.WHITE);
        originBox.setForeground(Color.BLACK);
        originBox.setBorder(BorderFactory.createLineBorder(yellow, 2));
        
        String terminalOrigin = getTerminalName(initialOrigin);
        originBox.addItem(terminalOrigin);
        originBox.setSelectedItem(terminalOrigin);
        originBox.setEnabled(false);
        originBox.addActionListener(this);
        originBox.addItemListener(this);
        add(originBox);

        enter.setBounds(575, 100, 180, 45);
        enter.setFont(new Font("Arial", Font.BOLD, 13));
        enter.setBackground(yellow);
        enter.setForeground(darkarmygreen);
        enter.setFocusPainted(false);
        enter.setBorder(BorderFactory.createRaisedBevelBorder());
        enter.addActionListener(this);
        add(enter);

        destinationBox.setBounds(300, 240, 400, 45);
        destinationBox.setFont(new Font("Arial", Font.PLAIN, 16));
        destinationBox.setBackground(Color.WHITE);
        destinationBox.setForeground(Color.BLACK);
        destinationBox.setBorder(BorderFactory.createLineBorder(yellow, 2));
        add(destinationBox);

        busAvailability.setBounds(850, 100, 620, 330);
        add(busAvailability);

        busSeatPanel.setBounds(850, 450, 620, 300);
        add(busSeatPanel);

        updateDestinationChoices();
        if (initialDestination != null) {
            destinationBox.setSelectedItem(initialDestination);
        }
        destinationBox.addActionListener(this);
        updateActiveRoute();

        Timer busCheckTimer = new Timer(200, e -> {
            String activeBus = busAvailability.getSelectedBus();
            String origin = (String) originBox.getSelectedItem();
            String dest = (String) destinationBox.getSelectedItem();
            // DEFENSIVE PROGRAMMING (null check)
            if (origin == null || dest == null) return;
            // DEFENSIVE PROGRAMMING (state validation)
            if (activeBus == null || activeBus.equals("None")) {
                if (!"None".equals(lastActiveBus) || !origin.equals(busSeatPanel.getCurrentOrigin()) || !dest.equals(busSeatPanel.getCurrentDestination())) {
                    busSeatPanel.setActiveBus(origin, dest, "None");
                    lastActiveBus = "None";
                }
            } else {
                String activeKey = origin + "|" + dest + "|" + activeBus;
                if (!activeKey.equals(busSeatPanel.getActiveBusKey())) {
                    busSeatPanel.setActiveBus(origin, dest, activeBus);
                }
                lastActiveBus = activeBus;
            }
        });
        busCheckTimer.start();

        paymentBox.setBounds(300, 310, 400, 45);
        paymentBox.setFont(new Font("Arial", Font.PLAIN, 16));
        paymentBox.setBackground(Color.WHITE);
        paymentBox.setForeground(Color.BLACK);
        paymentBox.setBorder(BorderFactory.createLineBorder(yellow, 2));
        paymentBox.addItem("-- Select Payment Method --");
        paymentBox.addItem("Cash");
        paymentBox.addItem("Instapay");
        paymentBox.addItem("Card");
        add(paymentBox);

        passengerScroll.setBounds(300, 380, 270, 75);
        passengerScroll.setBorder(BorderFactory.createLineBorder(yellow, 2));
        add(passengerScroll);

        passengerTypeText.setEditable(false);
        passengerTypeText.setFont(new Font("Arial", Font.PLAIN, 13));
        passengerTypeText.setBackground(Color.WHITE);
        passengerTypeText.setForeground(Color.BLACK);

        total.setBounds(300, 480, 400, 40);
        total.setFont(new Font("Arial", Font.BOLD, 22));
        total.setForeground(yellow);
        add(total);

        change.setBounds(300, 530, 400, 40);
        change.setFont(new Font("Arial", Font.BOLD, 20));
        change.setForeground(yellow);
        add(change);

        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        styleButton(print, 25, 650, 250, 60, yellow, darkarmygreen, buttonFont);
        styleButton(clear, 290, 650, 250, 60, yellow, darkarmygreen, buttonFont);
        styleButton(records, 555, 650, 250, 60, yellow, darkarmygreen, buttonFont);
        styleButton(logout, 25, 735, 120, 35, new Color(180, 50, 50), Color.WHITE, new Font("Arial", Font.BOLD, 12));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private static String getTerminalName(String terminal) {
        // DEFENSIVE PROGRAMMING (input validation)
        if (terminal == null || terminal.trim().isEmpty()) {
            return "Baliwag";
        }
        for (String[] t : Terminal.getAllTerminals()) {
            if (t[0].equalsIgnoreCase(terminal.trim())) {
                return t[0];
            }
        }
        return terminal.trim();
    }

    private static String getInitialDestination(String origin) {
        String[] dests = Terminal.getDestinations();
        if (dests != null && dests.length > 0) {
            return dests[0];
        }
        return "Baliwag".equalsIgnoreCase(origin) ? "Cubao" : "Baliwag";
    }

    private void showRouteStops(String selectedRoute, boolean isOrigin) {
        if (selectedRoute == null || isOrigin) return;
        String origin = (String) originBox.getSelectedItem();
        String route = "Baliwag".equals(origin) ? selectedRoute : origin;
        
        String[] stops;
        double[] fares;

        if ("Cubao".equals(route)) {
            if ("Baliwag".equals(origin)) {
                stops = new String[]{"Santa Rita", "Tabang, Guiguinto", "Plaridel", "SM Center Pulilan", "Cubao"};
                fares = new double[]{119, 99, 89, 60, 135};
            } else {
                stops = new String[]{"Baliwag", "SM Center Pulilan", "Plaridel", "Tabang, Guiguinto", "Santa Rita"};
                fares = new double[]{135, 105, 89, 69, 59};
            }
        } else if ("Grace Park".equals(route)) {
            if ("Baliwag".equals(origin)) {
                stops = new String[]{"Tabang, Guiguinto", "Plaridel", "SM Center Pulilan", "Grace Park"};
                fares = new double[]{109, 89, 69, 140};
            } else {
                stops = new String[]{"Baliwag", "SM Center Pulilan", "Plaridel", "Tabang, Guiguinto"};
                fares = new double[]{135, 105, 89, 69};
            }
        } else if ("Divisoria".equals(route)) {
            if ("Baliwag".equals(origin)) {
                stops = new String[]{"Balagtas", "Tabang, Guiguinto", "Plaridel", "SM Center Pulilan", "Divisoria"};
                fares = new double[]{129, 109, 89, 69, 145};
            } else {
                stops = new String[]{"Baliwag", "SM Center Pulilan", "Plaridel", "Tabang, Guiguinto", "Balagtas"};
                fares = new double[]{135, 110, 89, 60, 69};
            }
        } else {
            if (!"Baliwag".equals(origin)) {
                stops = new String[]{"Baliwag", "SM Center Pulilan", "Plaridel", "Tabang, Guiguinto", "Santa Rita"};
                fares = new double[]{135, 105, 89, 69, 59};
            } else {
                stops = Terminal.getCustomStops(route);
                fares = new double[stops.length];
                double baseFare = 60.0;
                for (int i = 0; i < stops.length; i++) {
                    fares[i] = baseFare + (i * 20.0);
                }
            }
        }

        String[] options = new String[stops.length];
        for (int i = 0; i < stops.length; i++) {
            options[i] = stops[i] + " — PHP " + String.format("%.2f", fares[i]);
        }
        String choice = (String) JOptionPane.showInputDialog(this, "Choose your destination stop and fare:", "Select Bus Stop", JOptionPane.QUESTION_MESSAGE, null, options, options[options.length - 1]);
        if (choice != null) {
            for (int i = 0; i < options.length; i++) {
                if (choice.equals(options[i])) {
                    selectedStop = stops[i];
                    overflow(fares[i]);
                    break;
                }
            }
        }
    }

    private void updateDestinationChoices() {
        if (isUpdatingDestinations) return;
        isUpdatingDestinations = true;
        String currentDest = (String) destinationBox.getSelectedItem();
        destinationBox.removeAllItems();

        for (String dest : Terminal.getDestinations()) {
            destinationBox.addItem(dest);
        }

        if (currentDest != null) {
            for (int i = 0; i < destinationBox.getItemCount(); i++) {
                if (currentDest.equals(destinationBox.getItemAt(i))) {
                    destinationBox.setSelectedItem(currentDest);
                    break;
                }
            }
        }
        isUpdatingDestinations = false;
    }

    private void updateActiveRoute() {
        String origin = (String) originBox.getSelectedItem();
        String dest = (String) destinationBox.getSelectedItem();
        if (origin == null || dest == null) return;
        busAvailability.setActiveRoute(origin, dest);
        busSeatPanel.setActiveBus(origin, dest, "None");
        lastActiveBus = "None";
    }

    // INTERFACE IMPLEMENTATION
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource().equals(originBox) && e.getStateChange() == ItemEvent.SELECTED) {
            selectedStop = null;
            selectedStopFare = -1;
            updateDestinationChoices();
            updateActiveRoute();
            showRouteStops((String) destinationBox.getSelectedItem(), false);
            if (passengerCount > 0 && passengerTypes != null) calculateTotal();
        }
    }

    private void styleButton(JButton btn, int x, int y, int width, int height, Color bgColor, Color fgColor, Font font) {
        btn.setBounds(x, y, width, height);
        btn.setFont(font);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createRaisedBevelBorder());
        btn.addActionListener(this);
        add(btn);
    }

    // INTERFACE IMPLEMENTATION
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(enter)) {
            int count;
            try {
                count = Integer.parseInt(passnumText.getText().trim());
                // DEFENSIVE PROGRAMMING (range validation):
                if (count > 66) {
                    JOptionPane.showMessageDialog(this, "Maximum of 66 passengers only!", "Limit", JOptionPane.WARNING_MESSAGE);
                    passnumText.setText("");
                    return;
                }
                if (count <= 0) {
                    JOptionPane.showMessageDialog(this, "Enter 1 or more passengers!");
                    return;
                }
            // DEFENSIVE PROGRAMMING (exception handling)
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number!");
                return;
            }
            passengerCount = count;
            passengerTypes = new String[passengerCount];
            passengerIDs = new String[passengerCount];
            StringBuilder displayTypes = new StringBuilder();

            for (int i = 0; i < passengerCount; i++) {
                String passengerNumber = String.format("%03d", i + 1);
                String[] choices = {
                    "Regular", "Student", "Senior Citizen", "PWD"
                };

                String selectedType = (String) JOptionPane.showInputDialog(
                        this,
                        "Select type of passenger " + passengerNumber + ":",
                        "Passenger " + passengerNumber,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        choices,
                        choices[0]);

                if (selectedType == null) {
                    cancelPassengerEntry();
                    return;
                }

                passengerTypes[i] = selectedType;
                String idNumber = "N/A";

                if (!selectedType.equals("Regular")) {
                    int minDigits = 0;
                    int maxDigits = 0;

                    if (selectedType.equals("Student")) {
                        minDigits = 10;
                        maxDigits = 12;
                    } else if (selectedType.equals("Senior Citizen")) {
                        minDigits = 4;
                        maxDigits = 6;
                    } else if (selectedType.equals("PWD")) {
                        minDigits = 12;
                        maxDigits = 12;
                    }

                    while (true) {
                        idNumber = JOptionPane.showInputDialog(
                                this,
                                "Enter ID Number for " + selectedType
                                + " (Passenger " + passengerNumber + "):\n"
                                + "Required digits: " + minDigits
                                + " to " + maxDigits,
                                "ID Validation Required",
                                JOptionPane.QUESTION_MESSAGE);

                        // DEFENSIVE PROGRAMMING (null/input validation):
                        if (idNumber == null) {
                            cancelPassengerEntry();
                            return;
                        }
                        idNumber = idNumber.trim();

                        if (idNumber.matches("\\d{" + minDigits + "," + maxDigits + "}")) {
                            break;
                        } else {
                            JOptionPane.showMessageDialog(this, "Invalid ID Number! Must be " + minDigits + " to " + maxDigits + " digits.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                passengerIDs[i] = idNumber;
                displayTypes.append(passengerNumber).append(" - ").append(selectedType).append("\n");
            }
            passengerTypeText.setText(displayTypes.toString());
            busSeatPanel.setMaxAllowedSelection(passengerCount);
            calculateTotal();
        }

        if (e.getSource().equals(destinationBox)) {
            if (!isUpdatingDestinations) {
                updateActiveRoute();
                selectedStop = null;
                selectedStopFare = -1;
                showRouteStops((String) destinationBox.getSelectedItem(), false);
            }
            if (passengerCount > 0 && passengerTypes != null) calculateTotal();
        }

        if (e.getSource().equals(print)) {
            // DEFENSIVE PROGRAMMING (precondition validation)
            if (passengerCount <= 0 || passengerTypes == null) {
                JOptionPane.showMessageDialog(this, "Please fill the passenger information.");
                return;
            }
            
            String payment = (String) paymentBox.getSelectedItem();
            // DEFENSIVE PROGRAMMING (input validation)
            if (payment == null || payment.startsWith("--")) {
                JOptionPane.showMessageDialog(this, "Please select a payment method!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String activeBus = busAvailability.getSelectedBus();
            if (activeBus == null || activeBus.equals("None")) {
                JOptionPane.showMessageDialog(this, "Please select an available bus first!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // DEFENSIVE PROGRAMMING (consistency validation)
            if (busSeatPanel.getSelectedSeatCount() != passengerCount) {
                JOptionPane.showMessageDialog(this, "Please select exactly " + passengerCount + " seat(s)!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double totalAmount = Double.parseDouble(total.getText().replace("TOTAL DUE: PHP ", ""));
            double amountPaid = totalAmount;
            String paymentInfo = "Payment Method: " + payment;

            if ("Cash".equals(payment)) {
                String cashInput = JOptionPane.showInputDialog(this, "Total Due: PHP " + totalAmount + "\nCash Rendered:", "Cash Payment", JOptionPane.QUESTION_MESSAGE);
                if (cashInput == null) return;
                try {
                    amountPaid = Double.parseDouble(cashInput.trim());
                    if (amountPaid < totalAmount) {
                        JOptionPane.showMessageDialog(this, "Insufficient cash rendered!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double changeAmt = amountPaid - totalAmount;
                change.setText(String.format("CHANGE: PHP %.2f", changeAmt));
                paymentInfo = "Payment Method: Cash\nCash Rendered: PHP " + String.format("%.2f", amountPaid) + "\nChange: PHP " + String.format("%.2f", changeAmt);
            } else if ("Instapay".equals(payment)) {
                String accNum = JOptionPane.showInputDialog(this, "Enter Instapay Number (Exact 11 digits):", "Instapay Validation", JOptionPane.QUESTION_MESSAGE);
                // DEFENSIVE PROGRAMMING (format validation)
                if (accNum == null || !accNum.trim().matches("\\d{11}")) {
                    JOptionPane.showMessageDialog(this, "Instapay number must be exactly 11 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                paymentInfo = "Payment Method: Instapay\nAccount Number: " + accNum.trim();
                amountPaid = totalAmount;
            } else if ("Card".equals(payment)) {
                String[] banks = {"Landbank", "BDO", "BPI", "Metrobank", "UnionBank"};
                String selectedBank = (String) JOptionPane.showInputDialog(this, "Select Bank / Provider:", "Bank Selection", JOptionPane.QUESTION_MESSAGE, null, banks, banks[0]);
                if (selectedBank == null) return;

                String accNum = JOptionPane.showInputDialog(this, "Enter Card Number (12 to 16 digits):", "Card Number Required", JOptionPane.QUESTION_MESSAGE);
                // DEFENSIVE PROGRAMMING (format validation)
                if (accNum == null || !accNum.trim().matches("\\d{12,16}")) {
                    JOptionPane.showMessageDialog(this, "Card number must be between 12 to 16 digits!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                paymentInfo = "Payment Method: Card\nBank: " + selectedBank + "\nCard Number: " + accNum.trim();
                amountPaid = totalAmount;
            }

            final String recordPaymentInfo = paymentInfo;

            String[] seatNumbers = busSeatPanel.getSelectedSeatNumbers();
            String origin = (String) originBox.getSelectedItem();
            String dest = (String) destinationBox.getSelectedItem();
            int availableBefore = busAvailability.getAvailableSeats(origin, dest, activeBus);
            
            if (!busAvailability.bookSelectedBusSeats(passengerCount)) return;
            busSeatPanel.confirmBookingForActiveBus();

            Tickets ticketWindow = new Tickets(
                origin, 
                dest, 
                selectedStop, 
                selectedStopFare, 
                activeBus, 
                passengerTypes, 
                passengerIDs, 
                seatNumbers, 
                totalAmount, 
                paymentInfo, 
                payment, 
                amountPaid, 
                Math.max(0, availableBefore - passengerCount)
            );
            
            ticketWindow.setOnPrintSuccess(() -> {
                Records.addTransaction(origin, dest, passengerCount, payment, totalAmount, activeBus, recordPaymentInfo);
                resetPassengerInputsAfterPrint();
            });
            ticketWindow.setVisible(true);
        }

        if (e.getSource().equals(clear)) {
            passnumText.setText("");
            passengerTypeText.setText("");
            paymentBox.setSelectedIndex(0);
            total.setText("TOTAL DUE: PHP 0.00");
            change.setText("CHANGE: PHP 0.00");
            passengerCount = 0;
            passengerTypes = null;
            passengerIDs = null;
            busSeatPanel.resetSeats();
        }

        if (e.getSource().equals(records)) {
            new Records();
        }

        if (e.getSource().equals(logout)) {
            if (JOptionPane.showConfirmDialog(this, "Log out?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                new BusLogin().setVisible(true);
                this.dispose();
            }
        }
    }

    private void cancelPassengerEntry() {
        passengerCount = 0;
        passengerTypes = null;
        passengerIDs = null;
        passengerTypeText.setText("");
        total.setText("TOTAL DUE: PHP 0.00");
        change.setText("CHANGE: PHP 0.00");
        busSeatPanel.setMaxAllowedSelection(0);
    }

    private void resetPassengerInputsAfterPrint() {
        passnumText.setText("");
        passengerTypeText.setText("");
        paymentBox.setSelectedIndex(0);
        total.setText("TOTAL DUE: PHP 0.00");
        change.setText("CHANGE: PHP 0.00");
        passengerCount = 0;
        passengerTypes = null;
        passengerIDs = null;
        busSeatPanel.setMaxAllowedSelection(0);
    }

    public void calculateTotal() {
        double fare = selectedStopFare >= 0 ? selectedStopFare : 135;
        double totalAmount = 0;
        if (passengerTypes != null) {
            for (int i = 0; i < passengerCount; i++) {
                double pFare = fareCalculator.calculatePassengerFare(fare, passengerTypes[i]);
                totalAmount += pFare;
            }
        }
        total.setText(String.format("TOTAL DUE: PHP %.2f", totalAmount));
    }

    private void overflow() { calculateTotal(); }
    private void overflow(int count) { passengerCount = count; calculateTotal(); }
    private void overflow(double fare) { selectedStopFare = fare; calculateTotal(); }

    public static void main(String[] args) {
        new BusMain();
    }
}
