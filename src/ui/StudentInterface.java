package ui;

import filters.*;
import manager.LibraryManager;
import models.Media;
import models.Student;
import observers.RefreshableUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Main interface for students to browse and search media
public class StudentInterface extends JFrame implements RefreshableUI {
    private Student student;
    private LibraryManager manager;
    private JTable mediaTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<models.Specialty> specialtyCombo;
    private JComboBox<models.Subject> subjectCombo;
    private JPanel centerCards;
    private CardLayout cardLayout;
    private JTextArea mediaContentArea;
    private Runnable logoutAction;

    public StudentInterface(Student student, Runnable logoutAction) {
        this.student = student;
        this.logoutAction = logoutAction;
        this.manager = LibraryManager.getInstance();
        
        setTitle("ISSAE Multimedia Library - Student: " + student.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        refreshDisplay();
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
        
        JLabel subTitleLabel = new JLabel("ISSAE Multimedia Library");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subTitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titleGroup = new JPanel(new BorderLayout());
        titleGroup.setOpaque(false);
        titleGroup.add(logoLabel, BorderLayout.NORTH);
        titleGroup.add(subTitleLabel, BorderLayout.SOUTH);
        headerPanel.add(titleGroup, BorderLayout.WEST);
        
        JLabel userLabel = new JLabel("Welcome, " + student.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setForeground(new Color(226, 0, 26));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(80, 25));
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(226, 0, 26)));
        logoutButton.addActionListener(e -> {
            if (logoutAction != null) {
                dispose();
                logoutAction.run();
            }
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(userLabel);
        rightPanel.add(logoutButton);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        // Filter and Search Panel
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
        resetButton.addActionListener(e -> refreshDisplay());

        topPanel.add(new JLabel("Filter by:"));
        topPanel.add(filterTypeCombo);
        topPanel.add(searchField);
        topPanel.add(specialtyCombo);
        topPanel.add(subjectCombo);
        topPanel.add(searchButton);
        topPanel.add(resetButton);

        // Media Table
        String[] columnNames = {"", "Title", "Author", "Type", "Year", "Subjects", "ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        mediaTable = new JTable(tableModel);
        
        // Hide the ID column
        mediaTable.getColumnModel().getColumn(6).setMinWidth(0);
        mediaTable.getColumnModel().getColumn(6).setMaxWidth(0);
        mediaTable.getColumnModel().getColumn(6).setPreferredWidth(0);
        
        // Icon renderer for the first column
        mediaTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        mediaTable.getColumnModel().getColumn(0).setMaxWidth(40);
        mediaTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean cellHasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, cellHasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
                String type = (String) table.getValueAt(row, 3);
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

        mediaTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        mediaTable.setRowHeight(35);
        mediaTable.setShowVerticalLines(false);
        mediaTable.setIntercellSpacing(new Dimension(0, 1));
        mediaTable.setGridColor(new Color(230, 230, 230));
        
        mediaTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        mediaTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        mediaTable.getTableHeader().setBackground(new Color(240, 240, 240));
        mediaTable.getTableHeader().setForeground(Color.BLACK);
        mediaTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        JScrollPane tableScroll = new JScrollPane(mediaTable);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());

        // Action button to view selected media details
        JButton viewDetailsButton = new JButton("Display Media");
        viewDetailsButton.setBackground(new Color(226, 0, 26));
        viewDetailsButton.setOpaque(true);
        viewDetailsButton.setBorderPainted(false);
        viewDetailsButton.setContentAreaFilled(true);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        viewDetailsButton.setPreferredSize(new Dimension(200, 45));
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.setBorder(BorderFactory.createEmptyBorder());
        viewDetailsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewDetailsButton.addActionListener(e -> showMediaDetails());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(viewDetailsButton);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(topPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        centerCards = new JPanel(cardLayout);
        centerCards.setOpaque(false);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        centerCards.add(tablePanel, "TABLE");

        // Content View Panel for showing detailed media information
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

        contentPanel.add(centerCards, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
    }

    // Handles the search/filtering logic
    private void performSearch() {
        String type = (String) filterTypeCombo.getSelectedItem();
        MediaFilter filter = null;

        switch (type) {
            case "Title":
                String titleQuery = searchField.getText().trim();
                if (!titleQuery.isEmpty()) filter = new TitleFilter(titleQuery);
                break;
            case "Author":
                String authorQuery = searchField.getText().trim();
                if (!authorQuery.isEmpty()) filter = new AuthorFilter(authorQuery);
                break;
            case "Specialty":
                models.Specialty specSelected = (models.Specialty) specialtyCombo.getSelectedItem();
                if (specSelected != null) filter = new SpecialtyFilter(specSelected);
                break;
            case "Subject":
                models.Subject subSelected = (models.Subject) subjectCombo.getSelectedItem();
                if (subSelected != null) filter = new SubjectFilter(subSelected);
                break;
        }

        if (filter != null) {
            updateTable(manager.filterMedia(filter));
        } else {
            refreshDisplay();
        }
    }

    // Displays the details of the selected media item
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
            media.incrementerAcces();
            persistence.DataManager.saveData(manager);
            media.afficher();
            
            mediaContentArea.setText(
                "ID: " + media.getId() + "\n" +
                "Title: " + media.getTitle() + "\n" +
                "Author: " + media.getAuthor() + "\n" +
                "Year: " + media.getPublicationYear() + "\n" +
                "Type: " + media.getType() + "\n" +
                media.getDisplayMedia() + "\n" +
                "Subjects: " + media.getSubjects().stream().map(models.Subject::getCode).reduce((s1, s2) -> s1 + ", " + s2).orElse("") + "\n\n" +
                "Description (Content):\n" + media.getDescription()
            );
            mediaContentArea.setCaretPosition(0);
            cardLayout.show(centerCards, "CONTENT");
        }
    }

    // Updates the table with the provided list of media
    private void updateTable(List<Media> mediaList) {
        tableModel.setRowCount(0);
        for (Media m : mediaList) {
            String subjects = m.getSubjects().stream()
                .map(models.Subject::getCode)
                .reduce((s1, s2) -> s1 + ", " + s2)
                .orElse("");
            tableModel.addRow(new Object[]{"", m.getTitle(), m.getAuthor(), m.getType(), m.getPublicationYear(), subjects, m.getId()});
        }
    }

    // Implementation of RefreshableUI update method
    @Override
    public void refreshDisplay() {
        updateTable(manager.getAllMedia());
    }
}
