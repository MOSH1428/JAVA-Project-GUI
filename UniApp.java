import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UniApp {

    // Database Connection Parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/UniversityDB";
    private static final String USER = "root";
    private static final String PASS = "plmoknijb55AA@";

    public static void main(String[] args) {
        // Initialize DB and Tables
        initializeDatabase();
        
        // Start the GUI
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("University Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 500);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Students Tab
        tabbedPane.addTab("Students", createStudentPanel());

        // 2. Doctors Tab
        tabbedPane.addTab("Doctors", createDoctorPanel());

        // 3. Faculties Tab
        tabbedPane.addTab("Faculties", createFacultyPanel());

        // 4. View Data Tab
        tabbedPane.addTab("View Data", createViewDataPanel());

        frame.add(tabbedPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createStudentPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Student Name:");
        JTextField nameField = new JTextField();

        JLabel facultyLabel = new JLabel("Faculty ID:");
        JTextField facultyField = new JTextField();

        JButton addButton = new JButton("Add Student");
        addButton.setBackground(new Color(41, 128, 185));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String facultyIdStr = facultyField.getText();
            
            if(!name.trim().isEmpty() && !facultyIdStr.trim().isEmpty()) {
                try {
                    int facultyId = Integer.parseInt(facultyIdStr);
                    insertStudent(name, facultyId);
                    nameField.setText("");
                    facultyField.setText("");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(panel, "Faculty ID must be a number!");
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter all fields!");
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(facultyLabel);
        panel.add(facultyField);
        panel.add(new JLabel()); 
        panel.add(addButton);

        return panel;
    }

    private static JPanel createDoctorPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Doctor Name:");
        JTextField nameField = new JTextField();

        JLabel deptLabel = new JLabel("Department / Specialization:");
        JTextField deptField = new JTextField();

        JButton addButton = new JButton("Add Doctor");
        addButton.setBackground(new Color(39, 174, 96));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String dept = deptField.getText();
            if(!name.trim().isEmpty() && !dept.trim().isEmpty()) {
                insertDoctor(name, dept);
                nameField.setText("");
                deptField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter all details!");
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(deptLabel);
        panel.add(deptField);
        panel.add(new JLabel()); 
        panel.add(addButton);

        return panel;
    }

    private static JPanel createFacultyPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Faculty Name:");
        JTextField nameField = new JTextField();

        JButton addButton = new JButton("Add Faculty");
        addButton.setBackground(new Color(142, 68, 173));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 14));

        addButton.addActionListener(e -> {
            String name = nameField.getText();
            if(!name.trim().isEmpty()) {
                insertFaculty(name);
                nameField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Please enter faculty name!");
            }
        });

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(new JLabel()); 
        panel.add(addButton);

        return panel;
    }

    // --- Database Operations (JDBC) ---

    private static void initializeDatabase() {
        try {
            // Explicitly load the driver to ensure it's on the classpath
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            String baseUrl = "jdbc:mysql://localhost:3306/";
            try (Connection con = DriverManager.getConnection(baseUrl, USER, PASS);
                 Statement stmt = con.createStatement()) {
                
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS UniversityDB");
                
                try (Connection dbCon = getConnection();
                     Statement dbStmt = dbCon.createStatement()) {
                    
                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS Faculties (ID INT AUTO_INCREMENT PRIMARY KEY, Faculty_Name VARCHAR(255))");
                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS Students (ID INT AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(255), Faculty_ID INT)");
                    dbStmt.executeUpdate("CREATE TABLE IF NOT EXISTS Doctors (ID INT AUTO_INCREMENT PRIMARY KEY, Name VARCHAR(255), Department VARCHAR(255))");
                }
            }
            System.out.println("Database and Tables initialized (MySQL).");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL Driver not found! Please ensure mysql-connector.jar is in the classpath.");
        } catch (SQLException e) {
            System.err.println("DB Init Error: " + e.getMessage());
        }
    }

    private static Connection getConnection() throws SQLException {
        Connection con = DatabaseConnection.getConnection();
        if (con == null) throw new SQLException("Could not establish connection via DatabaseConnection.");
        return con;
    }

    private static JPanel createViewDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane innerTab = new JTabbedPane();

        JTable studentTable = new JTable();
        JTable doctorTable = new JTable();
        JTable facultyTable = new JTable();

        innerTab.addTab("Students", new JScrollPane(studentTable));
        innerTab.addTab("Doctors", new JScrollPane(doctorTable));
        innerTab.addTab("Faculties", new JScrollPane(facultyTable));

        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.addActionListener(e -> {
            loadTableData(studentTable, "SELECT * FROM Students", new String[]{"ID", "Name", "Faculty ID"});
            loadTableData(doctorTable, "SELECT * FROM Doctors", new String[]{"ID", "Name", "Department"});
            loadTableData(facultyTable, "SELECT * FROM Faculties", new String[]{"ID", "Faculty Name"});
        });

        panel.add(innerTab, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);

        refreshBtn.doClick(); // Initial load

        return panel;
    }

    private static void loadTableData(JTable table, String query, String[] columnNames) {
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            int columnCount = columnNames.length;
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }
                model.addRow(row);
            }
            table.setModel(model);
        } catch (SQLException e) {
            System.err.println("Load Data Error: " + e.getMessage());
        }
    }

    private static void insertStudent(String name, int facultyId) {
        String sql = "INSERT INTO Students (Name, Faculty_ID) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, facultyId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Student added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void insertDoctor(String name, String department) {
        String sql = "INSERT INTO Doctors (Name, Department) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, department);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Doctor added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void insertFaculty(String name) {
        String sql = "INSERT INTO Faculties (Faculty_Name) VALUES (?)";
        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Faculty added successfully!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
