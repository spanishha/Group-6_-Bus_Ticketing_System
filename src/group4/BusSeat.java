package group4;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

// INHERITANCE
public class BusSeat extends JPanel {
    Color armyGreenBg = new Color(59, 83, 35);
    Color yellow = new Color(255, 204, 0);
    Color darkarmygreen = new Color(38, 54, 23);
    Color availableSeatColor = new Color(220, 235, 210);
    Color selectedSeatColor = new Color(255, 204, 0);
    Color occupiedSeatColor = new Color(180, 50, 50);
    // ENCAPSULATION
    private final int totalSeats = 66;
    private JButton[] seatButtons;
    private int selectedSeatCount = 0;
    private int maxAllowedSelection = 0;
    private String currentBusName = "NONE";
    private String currentOrigin = "Baliwag";
    private String currentDestination = "Cubao";
    private static String[] busKeys = new String[200];
    private static String[][] busStatuses = new String[200][66];
    private static int busCount = 0;

    public BusSeat() {
        setLayout(new BorderLayout(4, 4));
        setBackground(armyGreenBg);
        setOpaque(true);
        setEnabled(true);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(yellow, 2),
                "BUS SEAT (Select a Bus First)",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 10),
                yellow
        );
        setBorder(BorderFactory.createCompoundBorder(
                border,
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        JPanel legendPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 8, 2)
        );
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendBox("Available", availableSeatColor));
        legendPanel.add(createLegendBox("Selected", selectedSeatColor));
        legendPanel.add(createLegendBox("Occupied", occupiedSeatColor));
        add(legendPanel, BorderLayout.NORTH);
        JPanel seatGrid = new JPanel(new GridBagLayout());
        seatGrid.setOpaque(false);
        seatButtons = new JButton[totalSeats];
        char[] columns = {'A', 'B', 'C', 'D', 'E', 'F'};
        for (int i = 0; i < totalSeats; i++) {
            int rowNum;
            int columnNum;
            char colLetter;
            if (i < 60) {
                rowNum = (i / 5) + 1;
                int seatPosition = i % 5;
                if (seatPosition < 3) {
                    columnNum = seatPosition;
                } else {
                    columnNum = seatPosition + 1;
                }
                colLetter = columns[seatPosition];
            } else {
                rowNum = 13;
                columnNum = i - 60;
                colLetter = columns[columnNum];
            }
            String seatCode = rowNum + "" + colLetter;
            JButton seatButton = new JButton(seatCode);
            seatButton.setFont(new Font("Arial", Font.BOLD, 17));
            seatButton.setPreferredSize(new Dimension(95, 50));
            seatButton.setBackground(availableSeatColor);
            seatButton.setForeground(darkarmygreen);
            seatButton.setFocusPainted(false);
            seatButton.setOpaque(true);
            seatButton.setBorder(BorderFactory.createLineBorder(yellow, 2));
            seatButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            seatButton.setEnabled(true);
            seatButton.putClientProperty("status", "AVAILABLE");
            seatButton.addActionListener(e -> handleSeatClick(seatButton));
            seatButtons[i] = seatButton;
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = columnNum;
            gridBagConstraints.gridy = rowNum - 1;
            gridBagConstraints.insets = new Insets(3, 3, 3, 3);
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            seatGrid.add(seatButton, gridBagConstraints);
        }
        JScrollPane scrollPane = new JScrollPane(seatGrid);
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        scrollPane.getViewport().setBackground(armyGreenBg);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        JLabel infoLabel = new JLabel(
                "Click seats to select for booking",
                SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.BOLD, 10));
        infoLabel.setForeground(Color.WHITE);
        add(infoLabel, BorderLayout.SOUTH);
    }

    private void handleSeatClick(JButton seatButton) {
        if ("NONE".equalsIgnoreCase(currentBusName)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Please select an available bus first from 'BUS AVAILABILITY'!",
                    "Notice",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String status = (String) seatButton.getClientProperty("status");
        if ("OCCUPIED".equals(status)) {
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Seat " + seatButton.getText() + " is already occupied!",
                    "Seat Occupied",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if ("AVAILABLE".equals(status)) {
            // DEFENSIVE PROGRAMMING (range validation): Hindi pinapayagan ang pagpili kapag walang valid passenger count.
            if (maxAllowedSelection <= 0) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Please enter passenger count and click 'SELECT DISCOUNT' first!",
                        "Notice",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (selectedSeatCount >= maxAllowedSelection) {
                JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "You can only select up to " + maxAllowedSelection
                        + " seat(s) based on your passenger count!",
                        "Limit Reached",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            seatButton.putClientProperty("status", "SELECTED");
            seatButton.setBackground(selectedSeatColor);
            seatButton.setForeground(darkarmygreen);
            selectedSeatCount++;
        } else if ("SELECTED".equals(status)) {
            seatButton.putClientProperty("status", "AVAILABLE");
            seatButton.setBackground(availableSeatColor);
            seatButton.setForeground(darkarmygreen);
            selectedSeatCount--;
        }
        saveCurrentBusState();
        repaint();
    }

    private String getCurrentKey() {
        return currentOrigin + "|" + currentDestination + "|" + currentBusName;
    }

    private int findBusIndex(String key) {
        for (int i = 0; i < busCount; i++) {
            if (busKeys[i] != null && busKeys[i].equals(key)) {
                return i;
            }
        }
        if (busCount < 200) {
            busKeys[busCount] = key;
            for (int s = 0; s < totalSeats; s++) {
                busStatuses[busCount][s] = "AVAILABLE";
            }
            busCount++;
            return busCount - 1;
        }
        return 0;
    }

    public String getActiveBusKey() {
        return getCurrentKey();
    }

    public String getCurrentBusName() {
        return currentBusName;
    }

    public String getCurrentOrigin() {
        return currentOrigin;
    }

    public String getCurrentDestination() {
        return currentDestination;
    }

    public void setActiveBus(String busName) {
        setActiveBus(currentOrigin, currentDestination, busName);
    }

    public void setActiveBus(String origin, String busName) {
        setActiveBus(origin, currentDestination, busName);
    }

    public void setActiveBus(
            String origin,
            String destination,
            String busName
    ) {
        // DEFENSIVE PROGRAMMING (input validation)
        if (origin == null || origin.trim().isEmpty()) {
            origin = "Baliwag";
        }
        if (destination == null || destination.trim().isEmpty()) {
            destination = "Cubao";
        }
        boolean noBus = busName == null
                || "None".equalsIgnoreCase(busName)
                || "NONE".equalsIgnoreCase(busName);
        if (noBus) {
            boolean needsUpdate
                    = !"NONE".equalsIgnoreCase(currentBusName)
                    || !origin.equals(currentOrigin)
                    || !destination.equals(currentDestination);
            if (needsUpdate) {
                saveCurrentBusState();
                currentOrigin = origin;
                currentDestination = destination;
                currentBusName = "NONE";
                selectedSeatCount = 0;
                clearDisplayedSeats();
                updateBorderTitle();
            }
            repaint();
            return;
        }
        String newKey = origin + "|" + destination + "|" + busName;
        if (!getCurrentKey().equals(newKey)) {
            saveCurrentBusState();
            currentOrigin = origin;
            currentDestination = destination;
            currentBusName = busName;
            selectedSeatCount = 0;
            updateBorderTitle();
            loadBusState();
        }
    }

    private void clearDisplayedSeats() {
        for (JButton seat : seatButtons) {
            seat.putClientProperty("status", "AVAILABLE");
            seat.setBackground(availableSeatColor);
            seat.setForeground(darkarmygreen);
        }
    }

    private void updateBorderTitle() {
        TitledBorder border = null;
        if (getBorder() instanceof CompoundBorder) {
            CompoundBorder compound
                    = (CompoundBorder) getBorder();
            if (compound.getOutsideBorder() instanceof TitledBorder) {
                border = (TitledBorder) compound.getOutsideBorder();
            }
        }
        if (border == null) {
            return;
        }
        if ("NONE".equalsIgnoreCase(currentBusName)) {
            border.setTitle("BUS SEAT LAYOUT (Select a Bus First)");
        } else {
            border.setTitle(
                    "BUS SEAT LAYOUT (" + currentOrigin
                    + " - " + currentDestination
                    + " - " + currentBusName + ")"
            );
        }
        repaint();
    }

    private void loadBusState() {
        selectedSeatCount = 0;
        String key = getCurrentKey();
        int index = findBusIndex(key);
        String[] statuses = busStatuses[index];
        for (int i = 0; i < totalSeats; i++) {
            String status = statuses[i];
            if ("SELECTED".equals(status)) {
                status = "AVAILABLE";
                statuses[i] = "AVAILABLE";
            }
            seatButtons[i].putClientProperty("status", status);
            if ("OCCUPIED".equals(status)) {
                seatButtons[i].setBackground(occupiedSeatColor);
                seatButtons[i].setForeground(Color.WHITE);
            } else {
                seatButtons[i].setBackground(availableSeatColor);
                seatButtons[i].setForeground(darkarmygreen);
            }
        }
        repaint();
    }

    private void saveCurrentBusState() {
        if ("NONE".equalsIgnoreCase(currentBusName)) {
            return;
        }
        String key = getCurrentKey();
        int index = findBusIndex(key);
        String[] statuses = busStatuses[index];
        for (int i = 0; i < totalSeats; i++) {
            String status = (String) seatButtons[i].getClientProperty("status");
            if ("SELECTED".equals(status)) {
                statuses[i] = "AVAILABLE";
            } else {
                statuses[i] = status;
            }
        }
    }

    public String[] getSelectedSeatNumbers() {
        ArrayList<String> selectedSeats = new ArrayList<>();
        for (JButton seat : seatButtons) {
            String status = (String) seat.getClientProperty("status");
            if ("SELECTED".equals(status)) {
                selectedSeats.add(seat.getText());
            }
        }
        return selectedSeats.toArray(new String[0]);
    }

    public void confirmBookingForActiveBus() {
        if ("NONE".equalsIgnoreCase(currentBusName)) {
            return;
        }
        String key = getCurrentKey();
        int index = findBusIndex(key);
        String[] statuses = busStatuses[index];
        for (int i = 0; i < totalSeats; i++) {
            String status = (String) seatButtons[i].getClientProperty("status");
            if ("SELECTED".equals(status)) {
                status = "OCCUPIED";
                seatButtons[i].putClientProperty("status", status);
                seatButtons[i].setBackground(occupiedSeatColor);
                seatButtons[i].setForeground(Color.WHITE);
            }
            statuses[i] = status;
        }
        selectedSeatCount = 0;
        repaint();
    }

    public void setMaxAllowedSelection(int count) {
        if (count < 0) {
            maxAllowedSelection = 0;
        } else {
            maxAllowedSelection = count;
        }
    }

    private JPanel createLegendBox(String text, Color boxColor) {
        JPanel itemPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 2, 0)
        );
        itemPanel.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(9, 9));
        colorBox.setBackground(boxColor);
        colorBox.setBorder(BorderFactory.createLineBorder(yellow));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 9));
        label.setForeground(Color.WHITE);
        itemPanel.add(colorBox);
        itemPanel.add(label);
        return itemPanel;
    }

    public int getSelectedSeatCount() {
        return selectedSeatCount;
    }

    public void resetSeats() {
        if ("NONE".equalsIgnoreCase(currentBusName)) {
            return;
        }
        String key = getCurrentKey();
        int index = findBusIndex(key);
        String[] statuses = busStatuses[index];
        for (int i = 0; i < totalSeats; i++) {
            String status = (String) seatButtons[i].getClientProperty("status");
            if ("SELECTED".equals(status)) {
                seatButtons[i].putClientProperty("status", "AVAILABLE");
                seatButtons[i].setBackground(availableSeatColor);
                seatButtons[i].setForeground(darkarmygreen);
                statuses[i] = "AVAILABLE";
            }
        }
        selectedSeatCount = 0;
        repaint();
    }
}
