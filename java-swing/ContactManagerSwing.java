// Task 03 - Contact Management System (Swing GUI)
// Prodigy InfoTech Software Development Internship
// Author: Mansi (GitHub: Mansi-028)
//
// GUI version using Java Swing. Contacts persist to contacts.txt
// in the working directory.
//
// Compile: javac ContactManagerSwing.java
// Run:     java ContactManagerSwing

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class ContactManagerSwing extends JFrame {

    static class Contact {
        String name, phone, email;
        Contact(String n, String p, String e) { name = n; phone = p; email = e; }
        String toLine() { return name + "|" + phone + "|" + email; }
        static Contact fromLine(String line) {
            String[] parts = line.split("\\|", -1);
            if (parts.length != 3) return null;
            return new Contact(parts[0], parts[1], parts[2]);
        }
    }

    private static final String FILE_NAME = "contacts.txt";
    private static final Pattern EMAIL_RE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final List<Contact> contacts = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Name", "Phone", "Email"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JTextField nameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField searchField = new JTextField();
    private int editingIndex = -1; // -1 means add mode
    private final JButton submitBtn = new JButton("Add Contact");
    private final JButton cancelBtn = new JButton("Cancel");

    public ContactManagerSwing() {
        super("Contact Manager — Task 03");
        loadFromFile();
        buildUI();
        refreshTable("");
    }

    private void buildUI() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // header
        JLabel header = new JLabel("Contact Management System", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        header.setBorder(BorderFactory.createEmptyBorder(12, 0, 4, 0));
        add(header, BorderLayout.NORTH);

        // form on the left
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Contact Details"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Name:"), g);
        g.gridx = 1; g.weightx = 1; form.add(nameField, g);
        g.gridx = 0; g.gridy = 1; g.weightx = 0; form.add(new JLabel("Phone:"), g);
        g.gridx = 1; g.weightx = 1; form.add(phoneField, g);
        g.gridx = 0; g.gridy = 2; g.weightx = 0; form.add(new JLabel("Email:"), g);
        g.gridx = 1; g.weightx = 1; form.add(emailField, g);

        JPanel formBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        cancelBtn.setVisible(false);
        formBtns.add(cancelBtn);
        formBtns.add(submitBtn);
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        form.add(formBtns, g);

        // search + table
        JPanel right = new JPanel(new BorderLayout(6, 6));
        JPanel searchRow = new JPanel(new BorderLayout(6, 6));
        searchRow.add(new JLabel("Search:"), BorderLayout.WEST);
        searchRow.add(searchField, BorderLayout.CENTER);
        right.add(searchRow, BorderLayout.NORTH);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel rowActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        JButton editBtn = new JButton("Edit Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        rowActions.add(editBtn);
        rowActions.add(deleteBtn);
        right.add(rowActions, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, form, right);
        split.setDividerLocation(280);
        split.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(split, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "Made by Mansi — Prodigy InfoTech Software Development Internship — Task 03",
                SwingConstants.CENTER);
        footer.setFont(footer.getFont().deriveFont(11f));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        add(footer, BorderLayout.SOUTH);

        // listeners
        submitBtn.addActionListener(e -> onSubmit());
        cancelBtn.addActionListener(e -> resetForm());
        editBtn.addActionListener(e -> onEditSelected());
        deleteBtn.addActionListener(e -> onDeleteSelected());
        searchField.getDocument().addDocumentListener(new SimpleDocListener(
                () -> refreshTable(searchField.getText().trim())));
    }

    private void onSubmit() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showError("All fields are required.");
            return;
        }
        if (!isValidPhone(phone)) { showError("Invalid phone (7-15 digits)."); return; }
        if (!isValidEmail(email)) { showError("Invalid email address."); return; }

        if (editingIndex == -1) {
            contacts.add(new Contact(name, phone, email));
        } else {
            Contact c = contacts.get(editingIndex);
            c.name = name; c.phone = phone; c.email = email;
        }
        saveToFile();
        resetForm();
        refreshTable(searchField.getText().trim());
    }

    private void onEditSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Please select a contact first."); return; }
        int realIdx = findContactIndexByDisplayRow(row);
        if (realIdx == -1) return;
        Contact c = contacts.get(realIdx);
        nameField.setText(c.name);
        phoneField.setText(c.phone);
        emailField.setText(c.email);
        editingIndex = realIdx;
        submitBtn.setText("Save Changes");
        cancelBtn.setVisible(true);
    }

    private void onDeleteSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Please select a contact first."); return; }
        int realIdx = findContactIndexByDisplayRow(row);
        if (realIdx == -1) return;
        Contact c = contacts.get(realIdx);
        int res = JOptionPane.showConfirmDialog(this,
                "Delete contact \"" + c.name + "\"?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (res != JOptionPane.YES_OPTION) return;
        contacts.remove(realIdx);
        if (editingIndex == realIdx) resetForm();
        saveToFile();
        refreshTable(searchField.getText().trim());
    }

    private int findContactIndexByDisplayRow(int row) {
        String name = (String) model.getValueAt(row, 0);
        String phone = (String) model.getValueAt(row, 1);
        String email = (String) model.getValueAt(row, 2);
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            if (c.name.equals(name) && c.phone.equals(phone) && c.email.equals(email)) return i;
        }
        return -1;
    }

    private void resetForm() {
        nameField.setText(""); phoneField.setText(""); emailField.setText("");
        editingIndex = -1;
        submitBtn.setText("Add Contact");
        cancelBtn.setVisible(false);
    }

    private void refreshTable(String search) {
        model.setRowCount(0);
        String s = search.toLowerCase();
        List<Contact> sorted = new ArrayList<>(contacts);
        sorted.sort(Comparator.comparing(c -> c.name.toLowerCase()));
        for (Contact c : sorted) {
            if (s.isEmpty()
                    || c.name.toLowerCase().contains(s)
                    || c.phone.toLowerCase().contains(s)
                    || c.email.toLowerCase().contains(s)) {
                model.addRow(new Object[]{c.name, c.phone, c.email});
            }
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static boolean isValidEmail(String e) { return EMAIL_RE.matcher(e).matches(); }
    private static boolean isValidPhone(String p) {
        String d = p.replaceAll("[^0-9]", "");
        return d.length() >= 7 && d.length() <= 15;
    }

    private void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Contact c = Contact.fromLine(line);
                if (c != null) contacts.add(c);
            }
        } catch (IOException ignored) {}
    }

    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Contact c : contacts) { bw.write(c.toLine()); bw.newLine(); }
        } catch (IOException ignored) {}
    }

    // tiny helper to react to all text-field edits
    static class SimpleDocListener implements javax.swing.event.DocumentListener {
        private final Runnable r;
        SimpleDocListener(Runnable r) { this.r = r; }
        public void insertUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { r.run(); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ContactManagerSwing().setVisible(true));
    }
}
