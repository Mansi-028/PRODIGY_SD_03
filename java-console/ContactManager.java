// Task 03 - Contact Management System (Console)
// Prodigy InfoTech Software Development Internship
// Author: Mansi (GitHub: Mansi-028)
//
// A simple console-based contact manager. Contacts are stored in
// memory and persisted to a plain-text file (contacts.txt) so they
// remain available between runs.
//
// Compile: javac ContactManager.java
// Run:     java ContactManager

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class ContactManager {

    static class Contact {
        String name;
        String phone;
        String email;

        Contact(String name, String phone, String email) {
            this.name = name;
            this.phone = phone;
            this.email = email;
        }

        // simple pipe-delimited format for file storage
        String toLine() {
            return name + "|" + phone + "|" + email;
        }

        static Contact fromLine(String line) {
            String[] parts = line.split("\\|", -1);
            if (parts.length != 3) return null;
            return new Contact(parts[0], parts[1], parts[2]);
        }
    }

    static final String FILE_NAME = "contacts.txt";
    static final Pattern EMAIL_RE = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    static List<Contact> contacts = new ArrayList<>();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFromFile();
        System.out.println("===========================================");
        System.out.println("   Contact Management System  (Task 03)");
        System.out.println("===========================================");

        while (true) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addContact(); break;
                case "2": viewContacts(); break;
                case "3": editContact(); break;
                case "4": deleteContact(); break;
                case "5": searchContacts(); break;
                case "6":
                    saveToFile();
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please pick 1-6.");
            }
        }
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1. Add new contact");
        System.out.println("2. View all contacts");
        System.out.println("3. Edit contact");
        System.out.println("4. Delete contact");
        System.out.println("5. Search contacts");
        System.out.println("6. Save & exit");
        System.out.print("Choose an option: ");
    }

    static void addContact() {
        System.out.print("Name : ");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        System.out.print("Phone: ");
        String phone = sc.nextLine().trim();
        if (!isValidPhone(phone)) { System.out.println("Invalid phone (need 7-15 digits)."); return; }

        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        if (!isValidEmail(email)) { System.out.println("Invalid email address."); return; }

        contacts.add(new Contact(name, phone, email));
        saveToFile();
        System.out.println("Contact added.");
    }

    static void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("No contacts yet.");
            return;
        }
        System.out.println();
        System.out.printf("%-4s %-22s %-18s %-30s%n", "#", "Name", "Phone", "Email");
        System.out.println("--------------------------------------------------------------------------");
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            System.out.printf("%-4d %-22s %-18s %-30s%n", (i + 1), c.name, c.phone, c.email);
        }
    }

    static void editContact() {
        viewContacts();
        if (contacts.isEmpty()) return;
        int idx = readIndex("Enter contact number to edit: ");
        if (idx == -1) return;

        Contact c = contacts.get(idx);
        System.out.println("Leave a field blank to keep its current value.");

        System.out.print("Name [" + c.name + "]: ");
        String name = sc.nextLine().trim();
        System.out.print("Phone [" + c.phone + "]: ");
        String phone = sc.nextLine().trim();
        System.out.print("Email [" + c.email + "]: ");
        String email = sc.nextLine().trim();

        if (!name.isEmpty()) c.name = name;
        if (!phone.isEmpty()) {
            if (!isValidPhone(phone)) { System.out.println("Invalid phone. No changes saved."); return; }
            c.phone = phone;
        }
        if (!email.isEmpty()) {
            if (!isValidEmail(email)) { System.out.println("Invalid email. No changes saved."); return; }
            c.email = email;
        }
        saveToFile();
        System.out.println("Contact updated.");
    }

    static void deleteContact() {
        viewContacts();
        if (contacts.isEmpty()) return;
        int idx = readIndex("Enter contact number to delete: ");
        if (idx == -1) return;
        Contact removed = contacts.remove(idx);
        saveToFile();
        System.out.println("Deleted " + removed.name + ".");
    }

    static void searchContacts() {
        System.out.print("Search term: ");
        String term = sc.nextLine().trim().toLowerCase();
        if (term.isEmpty()) { System.out.println("Empty search."); return; }

        boolean found = false;
        for (int i = 0; i < contacts.size(); i++) {
            Contact c = contacts.get(i);
            if (c.name.toLowerCase().contains(term)
                || c.phone.toLowerCase().contains(term)
                || c.email.toLowerCase().contains(term)) {
                if (!found) {
                    System.out.printf("%-4s %-22s %-18s %-30s%n", "#", "Name", "Phone", "Email");
                    System.out.println("--------------------------------------------------------------------------");
                    found = true;
                }
                System.out.printf("%-4d %-22s %-18s %-30s%n", (i + 1), c.name, c.phone, c.email);
            }
        }
        if (!found) System.out.println("No matches found.");
    }

    static int readIndex(String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine().trim();
        try {
            int n = Integer.parseInt(s);
            if (n < 1 || n > contacts.size()) {
                System.out.println("Out of range.");
                return -1;
            }
            return n - 1;
        } catch (NumberFormatException e) {
            System.out.println("Not a number.");
            return -1;
        }
    }

    static boolean isValidEmail(String email) {
        return EMAIL_RE.matcher(email).matches();
    }

    static boolean isValidPhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 7 && digits.length() <= 15;
    }

    static void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Contact c = Contact.fromLine(line);
                if (c != null) contacts.add(c);
            }
        } catch (IOException e) {
            System.out.println("Could not load contacts: " + e.getMessage());
        }
    }

    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Contact c : contacts) {
                bw.write(c.toLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Could not save contacts: " + e.getMessage());
        }
    }
}
