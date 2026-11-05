package group4;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class Availability extends JPanel {
  
    private JLabel assignedLabel = new JLabel("No bus assigned");
    private JButton selectedBus = null;
    Color armyGreenBg = new Color(59, 83, 35);
    Color yellow = new Color(255, 204, 0);
    Color darkarmygreen = new Color(38, 54, 23);
    Color availableColor = new Color(220, 235, 210);
    Color selectedColor = new Color(255, 204, 0);
    Color occupiedColor = new Color(180, 50, 50);
    private String currentOrigin = "Baliwag";
    private String currentDestination = "Cubao";
    private int[] seatsLeft = new int[10];
    private JButton[] busButtons = new JButton[10];
    private static Map<String, Integer> savedSeatsLeft = new HashMap<>();

    private String getRouteBusKey(String origin, String destination, String busName) {
        return origin + "|" + destination + "|" + busName;
    }

    public Availability() {
        setLayout(new BorderLayout(3, 3));
        setBackground(armyGreenBg);
        for (int i = 0; i < seatsLeft.length; i++) {
            String busName = "BUS-" + (101 + i);
            seatsLeft[i] = savedSeatsLeft.getOrDefault(
                    getRouteBusKey(currentOrigin, currentDestination, busName),
                    66
            );
        }
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(yellow, 2),
                "BUS AVAILABILITY (66 Max)",
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
                new FlowLayout(FlowLayout.LEFT, 4, 1)
        );
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendBox("Available", availableColor));
        legendPanel.add(createLegendBox("Selected", selectedColor));
        legendPanel.add(createLegendBox("Occupied", occupiedColor));
        add(legendPanel, BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(5, 2, 4, 3));
        grid.setOpaque(false);
        for (int i = 0; i < 10; i++) {
            int busNumber = 101 + i;
            String busName = "BUS-" + busNumber;
            JButton bus = createBusButton(busName, i);
            busButtons[i] = bus;
            grid.add(bus);
        }
        add(grid, BorderLayout.CENTER);
        assignedLabel.setFont(new Font("Arial", Font.BOLD, 10));
        assignedLabel.setForeground(Color.WHITE);
        assignedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(assignedLabel, BorderLayout.SOUTH);
        refreshAllBuses();
    }

    private int getBusIndex(String busName) {
        switch (busName) {
            case "BUS-101":
                return 0;
            case "BUS-102":
                return 1;
            case "BUS-103":
                return 2;
            case "BUS-104":
                return 3;
            case "BUS-105":
                return 4;
            case "BUS-106":
                return 5;
            case "BUS-107":
                return 6;
            case "BUS-108":
                return 7;
            case "BUS-109":
                return 8;
            default:
                return 9;
        }
    }

    private JButton createBusButton(String busName, int index) {
        JButton bus = new JButton();
        bus.setLayout(new GridLayout(2, 1));
        bus.setFocusPainted(false);
        bus.setBackground(availableColor);
        bus.setBorder(BorderFactory.createLineBorder(yellow, 1));
        bus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel nameLabel = new JLabel(busName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 10));
        nameLabel.setForeground(darkarmygreen);
        JLabel statusLabel = new JLabel("66 Left", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 9));
        statusLabel.setForeground(darkarmygreen);
        bus.add(nameLabel);
        bus.add(statusLabel);
        bus.setActionCommand(busName);
        bus.addActionListener(e -> {
            int currentLeft = seatsLeft[index];
            if (currentLeft <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        busName + " is fully occupied for this route!",
                        "Bus Full",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            if (selectedBus == bus) {
                resetButtonStyle(bus, index);
                selectedBus = null;
                assignedLabel.setText("No bus assigned");
                return;
            }
            if (selectedBus != null) {
                int oldIndex = getBusIndex(
                        selectedBus.getActionCommand()
                );
                resetButtonStyle(selectedBus, oldIndex);
            }
            selectedBus = bus;
            bus.setBackground(selectedColor);
            nameLabel.setForeground(darkarmygreen);
            statusLabel.setText("SELECTED (" + currentLeft + ")");
            statusLabel.setForeground(darkarmygreen);
            assignedLabel.setText(
                    "Assigned: " + busName + " (" + currentLeft + " left)"
            );
        });
        return bus;
    }

    private void resetButtonStyle(JButton bus, int index) {
        int currentSeats = seatsLeft[index];
        if (currentSeats <= 0) {
            bus.setBackground(occupiedColor);
        } else {
            bus.setBackground(availableColor);
        }
        JLabel nameLabel = (JLabel) bus.getComponent(0);
        JLabel statusLabel = (JLabel) bus.getComponent(1);
        if (currentSeats <= 0) {
            nameLabel.setForeground(Color.WHITE);
            statusLabel.setText("OCCUPIED (0)");
            statusLabel.setForeground(Color.WHITE);
        } else {
            nameLabel.setForeground(darkarmygreen);
            statusLabel.setText(currentSeats + " Left");
            statusLabel.setForeground(darkarmygreen);
        }
    }

    private void refreshAllBuses() {
        for (int i = 0; i < 10; i++) {
            resetButtonStyle(busButtons[i], i);
        }
        selectedBus = null;
        assignedLabel.setText("No bus assigned");
        repaint();
    }

  
    public void setActiveRoute(String origin, String destination) {
     
        if (origin == null || destination == null
                || origin.trim().isEmpty()
                || destination.trim().isEmpty()) {
            return;
        }
        if (!currentOrigin.equals(origin)
                || !currentDestination.equals(destination)) {
            resetSelection();
            currentOrigin = origin;
            currentDestination = destination;
            for (int i = 0; i < 10; i++) {
                String busName = "BUS-" + (101 + i);
                seatsLeft[i] = savedSeatsLeft.getOrDefault(
                        getRouteBusKey(currentOrigin, currentDestination, busName),
                        66
                );
            }
            refreshAllBuses();
        }
    }

    public void setActiveOrigin(String origin) {
        if (origin != null && !origin.trim().isEmpty()) {
            setActiveRoute(origin, currentDestination);
        }
    }

    public String getActiveOrigin() {
        return currentOrigin;
    }

    public String getActiveDestination() {
        return currentDestination;
    }

  
    public int getAvailableSeats(String origin, String destination, String busName) {
        if (origin == null || destination == null || busName == null) {
            return 0;
        }
        if (!currentOrigin.equals(origin)
                || !currentDestination.equals(destination)) {
            return 66;
        }
        int index = getBusIndex(busName);
        return seatsLeft[index];
    }

    public boolean bookSelectedBusSeats(int passengerCount) {
     
        if (selectedBus == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select an available bus first from the panel!",
                    "Bus Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        String busName = selectedBus.getActionCommand();
        int index = getBusIndex(busName);
        int currentSeats = seatsLeft[index];
     
        if (passengerCount <= 0 || passengerCount > currentSeats) {
            JOptionPane.showMessageDialog(
                    this,
                    "Not enough seats in this bus! Only "
                    + currentSeats + " seats remaining.",
                    "Capacity Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
        seatsLeft[index] = currentSeats - passengerCount;
        savedSeatsLeft.put(
                getRouteBusKey(currentOrigin, currentDestination, busName),
                seatsLeft[index]
        );
        JButton bookedBus = selectedBus;
        selectedBus = null;
        resetButtonStyle(bookedBus, index);
        assignedLabel.setText("No bus assigned");
        if (seatsLeft[index] == 0) {
            JOptionPane.showMessageDialog(
                    this,
                    busName + " is fully booked for this route!",
                    "Bus Full",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
        return true;
    }

    public void cancelBookedSeats(int passengerCount) {
        if (selectedBus == null || passengerCount <= 0) {
            return;
        }
        String busName = selectedBus.getActionCommand();
        int index = getBusIndex(busName);
        seatsLeft[index] = Math.min(
                66,
                seatsLeft[index] + passengerCount
        );
        savedSeatsLeft.put(
                getRouteBusKey(currentOrigin, currentDestination, busName),
                seatsLeft[index]
        );
        resetButtonStyle(selectedBus, index);
    }

    private JPanel createLegendBox(String text, Color boxColor) {
        JPanel itemPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 2, 0)
        );
        itemPanel.setOpaque(false);
        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(8, 8));
        colorBox.setBackground(boxColor);
        colorBox.setBorder(BorderFactory.createLineBorder(yellow));
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 8));
        label.setForeground(Color.WHITE);
        itemPanel.add(colorBox);
        itemPanel.add(label);
        return itemPanel;
    }

    public String getSelectedBus() {
        if (selectedBus != null) {
            return selectedBus.getActionCommand();
        } else {
            return "None";
        }
    }

    public void resetSelection() {
        if (selectedBus != null) {
            int index = getBusIndex(
                    selectedBus.getActionCommand()
            );
            resetButtonStyle(selectedBus, index);
            selectedBus = null;
        }
        assignedLabel.setText("No bus assigned");
    }
}
