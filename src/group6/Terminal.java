package group4;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// ENCAPSULATION
public class Terminal {
    private static String current = "Baliwag";

    public static List<String[]> getAllTerminals() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{"Baliwag", "CC_BT", "baliwagstaff"});
        list.add(new String[]{"Cubao", "CC_CB", "cubaostaff"});
        list.add(new String[]{"Grace Park", "CC_GP", "graceparkstaff"});
        list.add(new String[]{"Divisoria", "CC_DV", "divisoriastaff"});

      
        File file = new File(Records.FOLDER, "custom_terminals.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split("\\|");
                    if (p.length >= 4) {
                        list.add(new String[]{p[0], p[1], p[2], p[3]});
                    } else if (p.length >= 3) {
                        list.add(new String[]{p[0], p[1], p[2], "Stop1,Stop2,Stop3,Stop4," + p[0]});
                    }
                }
            } catch (IOException e) {}
        }
        return list;
    }

    public static boolean addTerminal(String name, String opId, String pass, String stops) {
        File folder = new File(Records.FOLDER);
        if (!folder.exists()) folder.mkdirs();
        File file = new File(Records.FOLDER, "custom_terminals.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(name + "|" + opId + "|" + pass + "|" + stops);
            writer.newLine();
            savePassword(name, pass);
            setStatus(name, "ACTIVE");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static String[] getCustomStops(String terminalName) {
        File file = new File(Records.FOLDER, "custom_terminals.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split("\\|");
                    if (p.length >= 4 && p[0].equalsIgnoreCase(terminalName.trim())) {
                        return p[3].split(",");
                    }
                }
            } catch (IOException e) {}
        }
        return new String[]{terminalName};
    }

    public static String getCurrent() {
        return current;
    }

    public static void setCurrent(String terminal) {
        for (String[] t : getAllTerminals()) {
            if (t[0].equalsIgnoreCase(terminal.trim())) {
                current = t[0];
                return;
            }
        }
    }

    public static String getTitle() {
        return current + " Terminal";
    }

    public static String[] getDestinations() {
       
        if (current.equalsIgnoreCase("Baliwag")) {
            List<String> dests = new ArrayList<>();
            for (String[] t : getAllTerminals()) {
                if (!t[0].equalsIgnoreCase("Baliwag")) {
                    dests.add(t[0]);
                }
            }
            return dests.toArray(new String[0]);
        } 
       
        else {
            return new String[]{"Baliwag"};
        }
    }

    public static String getOperatorId(String terminal) {
        for (String[] t : getAllTerminals()) {
            if (t[0].equalsIgnoreCase(terminal.trim())) return t[1];
        }
        return "";
    }

    public static String findByOperatorId(String operatorId) {
        // DEFENSIVE PROGRAMMING (null check): Hindi maghahanap ng account kung walang operator ID.
        if (operatorId == null) return null;
        for (String[] t : getAllTerminals()) {
            if (t[1].equalsIgnoreCase(operatorId.trim())) return t[0];
        }
        return null;
    }

    public static String getPassword(String terminal) {
        File file = fileFor(terminal, "pass");
        String saved = readLine(file);
        if (saved != null && !saved.isEmpty()) return saved;
        for (String[] t : getAllTerminals()) {
            if (t[0].equalsIgnoreCase(terminal.trim())) return t[2];
        }
        return "staffpass";
    }

    public static boolean savePassword(String terminal, String newPassword) {
        return writeLine(fileFor(terminal, "pass"), newPassword.trim());
    }

    public static int getAttempts(String terminal) {
        try {
            return Integer.parseInt(readLine(fileFor(terminal, "attempts")));
        } catch (Exception e) {
            return 0;
        }
    }

    public static void setAttempts(String terminal, int attempts) {
        writeLine(fileFor(terminal, "attempts"), String.valueOf(attempts));
    }

    public static String getStatus(String terminal) {
        String status = readLine(fileFor(terminal, "status"));
        return (status != null && !status.isEmpty()) ? status : "ACTIVE";
    }

    public static void setStatus(String terminal, String status) {
        writeLine(fileFor(terminal, "status"), status.trim());
    }

    private static File fileFor(String terminal, String type) {
        String key = terminal.toLowerCase().replace(" ", "_");
        return new File(Records.FOLDER, "cashier_" + key + "_" + type + ".txt");
    }

    private static String readLine(File file) {
        if (!file.exists()) return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            return (line != null) ? line.trim() : null;
        } catch (IOException e) {
            return null;
        }
    }

    private static boolean writeLine(File file, String text) {
        File folder = file.getParentFile();
        // DEFENSIVE PROGRAMMING
        if (folder != null && !folder.exists()) folder.mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(text);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
