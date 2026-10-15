import manager.LibraryManager;
import models.Student;
import observers.DataRefreshObserver;
import observers.EmailNotificationObserver;
import observers.LoggingObserver;
import persistence.DataManager;
import persistence.XMLParser;
import ui.AdminInterface;
import ui.LoginDialog;
import ui.StudentInterface;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class Main {
    // Sets the application icon for a window
    public static void setAppIcon(Window window) {
        try {
            URL iconURL = Main.class.getResource("/resources/icon.png");
            if (iconURL != null) {
                ImageIcon img = new ImageIcon(iconURL);
                window.setIconImage(img.getImage());
            }
        } catch (Exception e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            // Set the look and feel to the system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Custom UI settings
            UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
            UIManager.put("Button.contentAreaFilled", Boolean.TRUE);
            UIManager.put("Button.opaque", Boolean.TRUE);
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Table.selectionBackground", new Color(226, 0, 26, 40));
            UIManager.put("Table.selectionForeground", Color.BLACK);
            UIManager.put("Panel.background", Color.WHITE);
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LibraryManager manager = LibraryManager.getInstance();

        // Load application data
        try {
            System.out.println("Loading object data...");
            DataManager.loadData(manager);
            
            System.out.println("Loading student data from universite.xml...");
            System.out.println("for student user: 'student1' pass: 'pass123', \nfor admin user: admin pass: admin");
            XMLParser.loadStudentData("universite.xml", manager);
            
            // Attach students as observers
            for (Student s : manager.getStudents()) {
                manager.attach(s);
            }
        } catch (Exception e) {
            System.err.println("Error loading data:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }

        // Initialize observers
        DataRefreshObserver refreshObserver = new DataRefreshObserver();
        manager.attach(refreshObserver);
        manager.attach(new LoggingObserver());
        manager.attach(new EmailNotificationObserver(manager.getStudents()));

        // Start login process
        showLogin(manager, refreshObserver);
    }

    // Displays the login dialog and handles redirection
    public static void showLogin(LibraryManager manager, DataRefreshObserver refreshObserver) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDlg = new LoginDialog(null);
            setAppIcon(loginDlg);
            loginDlg.setVisible(true);

            if (loginDlg.isSucceeded()) {
                if (loginDlg.isAdmin()) {
                    // Open admin interface
                    AdminInterface adminUI = new AdminInterface(() -> showLogin(manager, refreshObserver));
                    setAppIcon(adminUI);
                    adminUI.setVisible(true);
                } else {
                    // Open student interface
                    Student student = loginDlg.getAuthenticatedStudent();
                    StudentInterface studentUI = new StudentInterface(student, () -> showLogin(manager, refreshObserver));
                    setAppIcon(studentUI);
                    refreshObserver.addInterface(studentUI);
                    studentUI.setVisible(true);
                }
            } else {
                System.exit(0);
            }
        });
    }
}