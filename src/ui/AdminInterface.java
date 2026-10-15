package ui;

import exporters.*;
import factory.*;
import manager.LibraryManager;
import models.*;
import observers.DataRefreshObserver;
import statistics.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Main management interface for administrators
public class AdminInterface extends JFrame {
    private LibraryManager manager;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private ConcreteMediaFactory factory;
    private JPanel centerCards;
    private CardLayout cardLayout;
    private JTextArea mediaContentArea;
    private JPanel statisticsPanel;
    private JPanel statsContainer;
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<models.Specialty> specialtyCombo;
    private JComboBox<models.Subject> subjectCombo;

    private Runnable logoutAction;

    public AdminInterface(Runnable logoutAction) {
        this.logoutAction = logoutAction;
        this.manager = LibraryManager.getInstance();
        initFactory();
        
        setTitle("ISSAE Multimedia Library - Admin Interface");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        refreshTable();
    }

    private void initFactory() {
        factory = new ConcreteMediaFactory();
        factory.registerCreator("Document", new DocumentCreator());
        factory.registerCreator("VideoSession", new VideoCreator());
        factory.registerCreator("OnlineQuiz", new QuizCreator());
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(226, 0, 26));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        JLabel logoLabel = new JLabel();
        try {
            java.net.URL logoURL = getClass().getResource("/resources/logo.png");
            if (logoURL != null) {
                ImageIcon logoIcon = new ImageIcon(logoURL);
                // Scaled version for the interface header
                Image img = logoIcon.getImage().getScaledInstance(-1, 40, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(img));
            } else {
                logoLabel.setText("le cnam");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            logoLabel.setText("le cnam");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            logoLabel.setForeground(Color.WHITE);
        }
        
        JLabel subTitleLabel = new JLabel("Admin Management Console");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titleGroup = new JPanel(new BorderLayout());
        titleGroup.setOpaque(false);
        titleGroup.add(logoLabel, BorderLayout.NORTH);
        titleGroup.add(subTitleLabel, BorderLayout.SOUTH);
        headerPanel.add(titleGroup, BorderLayout.WEST);

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setForeground(new Color(226, 0, 26));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(226, 0, 26)));
        logoutButton.addActionListener(e -> {
            if (logoutAction != null) {
                dispose();
                logoutAction.run();
            }
        });

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setOpaque(false);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);

        // Side Panel for Actions
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(245, 245, 245));
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(20, 15, 20, 15)));
        sidePanel.setPreferredSize(new Dimension(220, 0));

        sidePanel.add(createSectionLabel("MEDIA MANAGEMENT"));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createSidebarButton("Add New Media", e -> showAddMediaDialog(), true));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Display Media", e -> showMediaDetails(), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Remove Selected", e -> removeSelectedMedia(), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Add Student", e -> showAddStudentDialog(), false));
        
        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(createSectionLabel("ANALYSIS & DATA"));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createSidebarButton("View Statistics", e -> showStatistics(), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Save Binary Data", e -> persistence.DataManager.saveData(manager), false));
        
        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(createSectionLabel("IMPORT OPTIONS"));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createSidebarButton("Import from XML", e -> importMedia("XML"), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Import from CSV", e -> importMedia("CSV"), false));

        sidePanel.add(Box.createVerticalStrut(30));
        sidePanel.add(createSectionLabel("EXPORT OPTIONS"));
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(createSidebarButton("Export to XML", e -> exportData(new XMLExporter(), "library", "xml"), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Export to CSV", e -> exportData(new CSVExporter(), "library", "csv"), false));
        sidePanel.add(Box.createVerticalStrut(5));
        sidePanel.add(createSidebarButton("Export to JSON", e -> exportData(new JSONExporter(), "library", "json"), false));

        // Center Table
        String[] columnNames = {"", "Title", "Author", "Type", "Year", "Access Count", "ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        mediaTable = new JTable(tableModel);

        // Hide the ID column
        mediaTable.getColumnModel().getColumn(6).setMinWidth(0);
        mediaTable.getColumnModel().getColumn(6).setMaxWidth(0);
        mediaTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        
        // Custom Renderer for the Icon Column (Index 0)
        mediaTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        mediaTable.getColumnModel().getColumn(0).setMaxWidth(40);
        mediaTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, cellHasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                String type = (String) table.getValueAt(row, 3); // Type is at index 3
                if ("Document".equals(type)) {
                    label.setText("📄");
                } else if ("VideoSession".equals(type)) {
                    label.setText("🎬");
                } else if ("OnlineQuiz".equals(type)) {
                    label.setText("📝");
                }
                return label;
            }
        });

        mediaTable.setRowHeight(35);
        mediaTable.setShowVerticalLines(false);
        mediaTable.setIntercellSpacing(new Dimension(0, 1));
        mediaTable.setGridColor(new Color(230, 230, 230));
        
        mediaTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        mediaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        mediaTable.getTableHeader().setBackground(new Color(240, 240, 240));
        mediaTable.getTableHeader().setForeground(Color.BLACK);
        mediaTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        
        // Top Panel: Search and Filters
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topPanel.setBackground(new Color(245, 245, 245));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        filterTypeCombo = new JComboBox<>(new String[]{"Title", "Author", "Specialty", "Subject"});
        filterTypeCombo.setPreferredSize(new Dimension(120, 30));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));

        specialtyCombo = new JComboBox<>();
        specialtyCombo.setPreferredSize(new Dimension(200, 30));
        for (models.Specialty s : manager.getSpecialties()) {
            specialtyCombo.addItem(s);
        }
        specialtyCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof models.Specialty) {
                    value = ((models.Specialty) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        subjectCombo = new JComboBox<>();
        subjectCombo.setPreferredSize(new Dimension(250, 30));
        for (models.Specialty spec : manager.getSpecialties()) {
            for (models.Subject sub : spec.getSubjects()) {
                subjectCombo.addItem(sub);
            }
        }
        subjectCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof models.Subject) {
                    models.Subject s = (models.Subject) value;
                    value = s.getCode() + " (" + s.getSpecialty().getName() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        specialtyCombo.setVisible(false);
        subjectCombo.setVisible(false);

        filterTypeCombo.addActionListener(e -> {
            String selected = (String) filterTypeCombo.getSelectedItem();
            searchField.setVisible("Title".equals(selected) || "Author".equals(selected));
            specialtyCombo.setVisible("Specialty".equals(selected));
            subjectCombo.setVisible("Subject".equals(selected));
            topPanel.revalidate();
            topPanel.repaint();
        });

        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(226, 0, 26));
        searchButton.setOpaque(true);
        searchButton.setBorderPainted(false);
        searchButton.setContentAreaFilled(true);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setFocusPainted(false);
        searchButton.setBorder(BorderFactory.createEmptyBorder());
        searchButton.addActionListener(e -> performSearch());
        
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(Color.WHITE);
        resetButton.setOpaque(true);
        resetButton.setContentAreaFilled(true);
        resetButton.setForeground(Color.BLACK);
        resetButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        resetButton.setPreferredSize(new Dimension(80, 30));
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> refreshTable());

        topPanel.add(new JLabel("Filter by:"));
        topPanel.add(filterTypeCombo);
        topPanel.add(searchField);
        topPanel.add(specialtyCombo);
        topPanel.add(subjectCombo);
        topPanel.add(searchButton);
        topPanel.add(resetButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        // topPanel is now added inside the TABLE card of centerCards
        
        // Center Panel with CardLayout
        cardLayout = new CardLayout();
        centerCards = new JPanel(cardLayout);
        centerCards.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(topPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(mediaTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        centerCards.add(tablePanel, "TABLE");

        // Content View Panel
        JPanel contentViewPanel = new JPanel(new BorderLayout());
        contentViewPanel.setBackground(Color.WHITE);
        contentViewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mediaContentArea = new JTextArea();
        mediaContentArea.setEditable(false);
        mediaContentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mediaContentArea.setLineWrap(true);
        mediaContentArea.setWrapStyleWord(true);
        JScrollPane contentScroll = new JScrollPane(mediaContentArea);
        contentScroll.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JButton backButton = new JButton("← Back to List");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setForeground(new Color(226, 0, 26));
        backButton.setBackground(Color.WHITE);
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 0, 26)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> cardLayout.show(centerCards, "TABLE"));

        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backPanel.setOpaque(false);
        backPanel.add(backButton);

        contentViewPanel.add(backPanel, BorderLayout.NORTH);
        contentViewPanel.add(contentScroll, BorderLayout.CENTER);
        
        centerCards.add(contentViewPanel, "CONTENT");

        // Statistics Panel
        statisticsPanel = new JPanel(new BorderLayout());
        statisticsPanel.setBackground(Color.WHITE);
        statisticsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton statsBackButton = new JButton("← Back to List");
        statsBackButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsBackButton.setForeground(new Color(226, 0, 26));
        statsBackButton.setBackground(Color.WHITE);
        statsBackButton.setOpaque(true);
        statsBackButton.setContentAreaFilled(true);
        statsBackButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 0, 26)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        statsBackButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        statsBackButton.setFocusPainted(false);
        statsBackButton.addActionListener(e -> cardLayout.show(centerCards, "TABLE"));

        JPanel statsBackPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsBackPanel.setOpaque(false);
        statsBackPanel.add(statsBackButton);

        statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        statsContainer.setBackground(Color.WHITE);
        JScrollPane statsScroll = new JScrollPane(statsContainer);
        statsScroll.setBorder(null);
        statsScroll.getVerticalScrollBar().setUnitIncrement(16);

        statisticsPanel.add(statsBackPanel, BorderLayout.NORTH);
        statisticsPanel.add(statsScroll, BorderLayout.CENTER);

        centerCards.add(statisticsPanel, "STATISTICS");

        centerPanel.add(centerCards, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(Color.GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createSidebarButton(String text, java.awt.event.ActionListener listener, boolean primary) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", primary ? Font.BOLD : Font.PLAIN, 13));
        
        if (primary) {
            button.setBackground(new Color(226, 0, 26));
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setContentAreaFilled(true);
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder());
        } else {
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
            button.setContentAreaFilled(true);
            button.setBorderPainted(true);
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        }
        
        button.addActionListener(listener);
        return button;
    }

    private void showAddMediaDialog() {
        JDialog dialog = new JDialog(this, "Add New Media", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Document", "VideoSession", "OnlineQuiz"});
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField yearField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JTextField extraField = new JTextField(); // Used for pages or duration
        JTextField extraField2 = new JTextField(); // Used for difficulty

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Media Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        titleField.setColumns(20);
        formPanel.add(titleField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        authorField.setColumns(20);
        formPanel.add(authorField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Publication Year:"), gbc);
        gbc.gridx = 1;
        yearField.setColumns(20);
        formPanel.add(yearField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descArea.setRows(3);
        descArea.setColumns(20);
        formPanel.add(new JScrollPane(descArea), gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel extraLabel = new JLabel("Number of Pages:");
        formPanel.add(extraLabel, gbc);
        gbc.gridx = 1;
        extraField.setColumns(20);
        formPanel.add(extraField, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        JLabel extraLabel2 = new JLabel("");
        extraLabel2.setVisible(false);
        extraField2.setVisible(false);
        formPanel.add(extraLabel2, gbc);
        gbc.gridx = 1;
        extraField2.setColumns(20);
        formPanel.add(extraField2, gbc);

        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            if ("Document".equals(selected)) {
                extraLabel.setText("Number of Pages:");
                extraLabel2.setVisible(false);
                extraField2.setVisible(false);
            } else if ("VideoSession".equals(selected)) {
                extraLabel.setText("Duration (min):");
                extraLabel2.setVisible(false);
                extraField2.setVisible(false);
            } else if ("OnlineQuiz".equals(selected)) {
                extraLabel.setText("Estimated Duration (min):");
                extraLabel2.setText("Difficulty Level:");
                extraLabel2.setVisible(true);
                extraField2.setVisible(true);
            }
            dialog.revalidate();
        });

        // Subject selection
        DefaultListModel<Subject> listModel = new DefaultListModel<>();
        for (Specialty spec : manager.getSpecialties()) {
            for (Subject sub : spec.getSubjects()) {
                listModel.addElement(sub);
            }
        }
        JList<Subject> subjectList = new JList<>(listModel);
        subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Subject s = (Subject) value;
                return super.getListCellRendererComponent(list, s.getCode() + " (" + s.getSpecialty().getName() + ")", index, isSelected, cellHasFocus);
            }
        });

        JPanel mainForm = new JPanel(new BorderLayout());
        mainForm.add(formPanel, BorderLayout.NORTH);
        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.setBorder(BorderFactory.createTitledBorder("Select Subjects (Ctrl+Click)"));
        subPanel.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        mainForm.add(subPanel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Add Media");
        saveBtn.setBackground(new Color(226, 0, 26));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setContentAreaFilled(true);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder());
        saveBtn.addActionListener(e -> {
            try {
                Map<String, Object> attrs = new HashMap<>();
                attrs.put("title", titleField.getText());
                attrs.put("author", authorField.getText());
                attrs.put("publicationYear", Integer.parseInt(yearField.getText()));
                attrs.put("description", descArea.getText());

                String type = (String) typeCombo.getSelectedItem();
                if ("Document".equals(type)) {
                    attrs.put("numberOfPages", Integer.parseInt(extraField.getText()));
                } else if ("VideoSession".equals(type)) {
                    attrs.put("duration", Integer.parseInt(extraField.getText()));
                } else if ("OnlineQuiz".equals(type)) {
                    attrs.put("estimatedDuration", Integer.parseInt(extraField.getText()));
                    attrs.put("difficultyLevel", extraField2.getText());
                }

                Media media = factory.createMedia(type, attrs);
                for (Subject sub : subjectList.getSelectedValuesList()) {
                    media.addSubject(sub);
                    sub.addMedia(media);
                }

                manager.addMedia(media);
                persistence.DataManager.saveData(manager);
                refreshTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        saveBtn.setPreferredSize(new Dimension(0, 40));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dialog.add(mainForm, BorderLayout.CENTER);
        dialog.add(saveBtn, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showAddStudentDialog() {
        JDialog dialog = new JDialog(this, "Add New Student", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 550);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        
        JComboBox<Specialty> specCombo = new JComboBox<>();
        for (Specialty spec : manager.getSpecialties()) {
            specCombo.addItem(spec);
        }
        specCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Specialty) {
                    value = ((Specialty) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        userField.setColumns(20);
        formPanel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passField.setColumns(20);
        formPanel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Specialty:"), gbc);
        gbc.gridx = 1;
        formPanel.add(specCombo, gbc);

        // Subject selection
        DefaultListModel<Subject> listModel = new DefaultListModel<>();
        specCombo.addActionListener(e -> {
            listModel.clear();
            Specialty selected = (Specialty) specCombo.getSelectedItem();
            if (selected != null) {
                for (Subject sub : selected.getSubjects()) {
                    listModel.addElement(sub);
                }
            }
        });
        
        // Initial load
        if (specCombo.getItemCount() > 0) {
            specCombo.setSelectedIndex(0);
            Specialty selected = (Specialty) specCombo.getSelectedItem();
            for (Subject sub : selected.getSubjects()) {
                listModel.addElement(sub);
            }
        }

        JList<Subject> subjectList = new JList<>(listModel);
        subjectList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Subject s = (Subject) value;
                return super.getListCellRendererComponent(list, s.getCode(), index, isSelected, cellHasFocus);
            }
        });

        JPanel mainForm = new JPanel(new BorderLayout());
        mainForm.add(formPanel, BorderLayout.NORTH);
        JPanel subPanel = new JPanel(new BorderLayout());
        subPanel.setBorder(BorderFactory.createTitledBorder("Select Subjects (Ctrl+Click)"));
        subPanel.add(new JScrollPane(subjectList), BorderLayout.CENTER);
        mainForm.add(subPanel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("Add Student");
        saveBtn.setBackground(new Color(226, 0, 26));
        saveBtn.setOpaque(true);
        saveBtn.setBorderPainted(false);
        saveBtn.setContentAreaFilled(true);
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(BorderFactory.createEmptyBorder());
        saveBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            Specialty specialty = (Specialty) specCombo.getSelectedItem();
            List<Subject> subjects = subjectList.getSelectedValuesList();

            if (username.isEmpty() || password.isEmpty() || specialty == null) {
                JOptionPane.showMessageDialog(dialog, "Please fill all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (username.equalsIgnoreCase("admin")) {
                JOptionPane.showMessageDialog(dialog, "Cannot create a student with username 'admin'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student newStudent = new Student(username, password, specialty);
            for (Subject s : subjects) {
                newStudent.addSubject(s);
            }

            manager.addStudent(newStudent);
            persistence.XMLParser.saveStudentData("universite.xml", manager);
            persistence.DataManager.saveData(manager);
            
            JOptionPane.showMessageDialog(this, "Student added successfully and saved to universite.xml and binary data!");
            dialog.dispose();
        });

        saveBtn.setPreferredSize(new Dimension(0, 40));
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        mainForm.add(saveBtn, BorderLayout.SOUTH);
        dialog.add(mainForm);
        dialog.setVisible(true);
    }

    private void showMediaDetails() {
        int selectedRow = mediaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a media from the table.");
            return;
        }
        String mediaId = (String) tableModel.getValueAt(selectedRow, 6);
        Media media = manager.getAllMedia().stream()
            .filter(m -> m.getId().equals(mediaId))
            .findFirst().orElse(null);

        if (media != null) {
            mediaContentArea.setText(
                "ID: " + media.getId() + "\n" +
                "Title: " + media.getTitle() + "\n" +
                "Author: " + media.getAuthor() + "\n" +
                "Year: " + media.getPublicationYear() + "\n" +
                "Type: " + media.getType() + "\n" +
                media.getDisplayMedia() + "\n" +
                "Access Count: " + media.getAccessCount() + "\n" +
                "Subjects: " + media.getSubjects().stream().map(models.Subject::getCode).reduce((s1, s2) -> s1 + ", " + s2).orElse("") + "\n\n" +
                "Description (Content):\n" + media.getDescription()
            );
            mediaContentArea.setCaretPosition(0);
            cardLayout.show(centerCards, "CONTENT");
        }
    }

    private void removeSelectedMedia() {
        int selectedRow = mediaTable.getSelectedRow();
        if (selectedRow != -1) {
            String id = (String) tableModel.getValueAt(selectedRow, 6);
            Media media = manager.getAllMedia().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
            if (media != null) {
                manager.removeMedia(media);
                persistence.DataManager.saveData(manager);
                refreshTable();
            }
        }
    }

    private void showStatistics() {
        statsContainer.removeAll();
        
        // Header
        JLabel header = new JLabel("Library Statistics Dashboard");
        header.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        statsContainer.add(header);

        // Global Stats Card
        int totalAccess = manager.getAllMedia().stream().mapToInt(Media::getAccessCount).sum();
        int totalMedia = manager.getAllMedia().size();
        int totalStudents = manager.getStudents().size();

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        summaryPanel.setOpaque(false);
        summaryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        summaryPanel.add(createStatCard("Total Accesses", String.valueOf(totalAccess), new Color(226, 0, 26)));
        summaryPanel.add(createStatCard("Total Media", String.valueOf(totalMedia), new Color(70, 70, 70)));
        summaryPanel.add(createStatCard("Total Students", String.valueOf(totalStudents), new Color(100, 100, 100)));
        statsContainer.add(summaryPanel);
        statsContainer.add(Box.createVerticalStrut(30));

        // Charts Section
        statsContainer.add(createSectionHeader("Most Accessed Media (Top 5)"));
        List<Media> topMedia = manager.getAllMedia().stream()
                .sorted((m1, m2) -> Integer.compare(m2.getAccessCount(), m1.getAccessCount()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());

        int maxAccess = topMedia.isEmpty() ? 0 : topMedia.get(0).getAccessCount();
        for (Media m : topMedia) {
            statsContainer.add(createBarChartItem(m.getTitle(), m.getAccessCount(), maxAccess));
        }

        statsContainer.add(Box.createVerticalStrut(30));
        statsContainer.add(createSectionHeader("Access by Specialty"));
        for (Specialty spec : manager.getSpecialties()) {
            int specAccess = manager.getAllMedia().stream()
                    .filter(m -> m.getSubjects().stream().anyMatch(s -> s.getSpecialty().equals(spec)))
                    .mapToInt(Media::getAccessCount).sum();
            statsContainer.add(createBarChartItem(spec.getName(), specAccess, totalAccess));
        }

        statsContainer.revalidate();
        statsContainer.repaint();
        cardLayout.show(centerCards, "STATISTICS");
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(180, 100));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLbl.setForeground(Color.GRAY);
        
        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLbl.setForeground(color);
        
        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLbl, BorderLayout.CENTER);
        return card;
    }

    private JLabel createSectionHeader(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    private JPanel createBarChartItem(String label, int value, int max) {
        JPanel container = new JPanel(new BorderLayout(10, 5));
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(800, 45));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel titleLbl = new JLabel(label);
        titleLbl.setPreferredSize(new Dimension(200, 20));
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JProgressBar bar = new JProgressBar(0, Math.max(1, max));
        bar.setValue(value);
        bar.setStringPainted(true);
        bar.setString(String.valueOf(value));
        bar.setForeground(new Color(226, 0, 26));
        bar.setBackground(new Color(245, 245, 245));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(400, 20));

        container.add(titleLbl, BorderLayout.WEST);
        container.add(bar, BorderLayout.CENTER);
        return container;
    }

    private void importMedia(String format) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select " + format + " file to import");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                List<Media> imported;
                if ("XML".equals(format)) {
                    imported = persistence.MediaImporter.importFromXML(file);
                } else {
                    imported = persistence.MediaImporter.importFromCSV(file);
                }

                for (Media m : imported) {
                    manager.addMedia(m);
                }
                
                persistence.DataManager.saveData(manager);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Successfully imported " + imported.size() + " media items.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error importing data: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportData(Exporter exporter, String baseFilename, String extension) {
        try {
            File exportDir = new File("exports");
            if (!exportDir.exists()) {
                exportDir.mkdir();
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String filename = "exports" + File.separator + baseFilename + "_" + timestamp + "." + extension;

            exporter.export(manager.getAllMedia(), filename);
            JOptionPane.showMessageDialog(this, "Exported successfully to " + filename);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error during export: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String type = (String) filterTypeCombo.getSelectedItem();
        filters.MediaFilter filter = null;

        switch (type) {
            case "Title":
                String titleQuery = searchField.getText().trim();
                if (!titleQuery.isEmpty()) filter = new filters.TitleFilter(titleQuery);
                break;
            case "Author":
                String authorQuery = searchField.getText().trim();
                if (!authorQuery.isEmpty()) filter = new filters.AuthorFilter(authorQuery);
                break;
            case "Specialty":
                models.Specialty specSelected = (models.Specialty) specialtyCombo.getSelectedItem();
                if (specSelected != null) filter = new filters.SpecialtyFilter(specSelected);
                break;
            case "Subject":
                models.Subject subSelected = (models.Subject) subjectCombo.getSelectedItem();
                if (subSelected != null) filter = new filters.SubjectFilter(subSelected);
                break;
        }

        if (filter != null) {
            updateTable(manager.filterMedia(filter));
        } else {
            refreshTable();
        }
    }

    private void updateTable(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media m : mediaList) {
            tableModel.addRow(new Object[]{"", m.getTitle(), m.getAuthor(), m.getType(), m.getPublicationYear(), m.getAccessCount(), m.getId()});
        }
    }

    private void refreshTable() {
        updateTable(manager.getAllMedia());
    }
}
