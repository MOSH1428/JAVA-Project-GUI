import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;

public class JAVAProjectGUI extends JFrame {

    private JTable studentTable, doctorTable, facultyTable;
    private DefaultTableModel studentModel, doctorModel, facultyModel;
    private final String FILE_NAME = "university_data.txt";

    // Color Palette
    private final Color PRIMARY_BG = new Color(245, 247, 250);
    private final Color SECONDARY_BG = new Color(255, 255, 255);
    private final Color STUDENT_COLOR = new Color(52, 152, 219);
    private final Color DOCTOR_COLOR = new Color(46, 204, 113);
    private final Color FACULTY_COLOR = new Color(155, 89, 182);
    private final Color TEXT_COLOR = new Color(44, 62, 80);

    public JAVAProjectGUI() {
        setTitle("University Management System");
        setSize(900, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PRIMARY_BG);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(SECONDARY_BG);
        tabbedPane.setForeground(TEXT_COLOR);

        // 1. Students Tab
        studentModel = new DefaultTableModel(new String[]{"Student Name"}, 0);
        studentTable = createStyledTable(studentModel, STUDENT_COLOR);
        tabbedPane.addTab("🎓 Students", createEntryPanel("Student Name:", "Add Student", studentModel, studentTable, STUDENT_COLOR));

        // 2. Doctors Tab
        doctorModel = new DefaultTableModel(new String[]{"Doctor Name"}, 0);
        doctorTable = createStyledTable(doctorModel, DOCTOR_COLOR);
        tabbedPane.addTab("👨‍🏫 Doctors", createEntryPanel("Doctor Name:", "Add Doctor", doctorModel, doctorTable, DOCTOR_COLOR));

        // 3. Faculties Tab
        facultyModel = new DefaultTableModel(new String[]{"Faculty Name"}, 0);
        facultyTable = createStyledTable(facultyModel, FACULTY_COLOR);
        tabbedPane.addTab("🏢 Faculties", createEntryPanel("Faculty Name:", "Add Faculty", facultyModel, facultyTable, FACULTY_COLOR));

        // Styling the main frame
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(PRIMARY_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel("University Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel);
        
        loadData();
    }

    private JPanel createEntryPanel(String labelText, String buttonText, DefaultTableModel model, JTable table, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(SECONDARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Area
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        inputPanel.setBackground(SECONDARY_BG);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 16));
        label.setForeground(TEXT_COLOR);
        
        JTextField textField = new JTextField(25);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setPreferredSize(new Dimension(250, 40));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton addButton = new JButton(buttonText);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(themeColor);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.setPreferredSize(new Dimension(150, 40));
        addButton.setBorder(BorderFactory.createEmptyBorder());

        inputPanel.add(label);
        inputPanel.add(textField);
        inputPanel.add(addButton);

        // Table Area
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241), 2));
        scrollPane.getViewport().setBackground(SECONDARY_BG);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button Action
        addButton.addActionListener(e -> {
            String name = textField.getText().trim();
            if (!name.isEmpty()) {
                model.addRow(new Object[]{name});
                textField.setText("");
                saveData();
                
                // Show custom styled toast/dialog
                JOptionPane.showMessageDialog(this, 
                    "Successfully added: " + name, 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid name!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(TEXT_COLOR);
        table.setGridColor(new Color(236, 240, 241));
        table.setShowVerticalLines(false);
        table.setSelectionBackground(headerColor.brighter());
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(headerColor);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(100, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        return table;
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("[STUDENTS]");
            for (int i = 0; i < studentModel.getRowCount(); i++) writer.println(studentModel.getValueAt(i, 0));
            
            writer.println("[DOCTORS]");
            for (int i = 0; i < doctorModel.getRowCount(); i++) writer.println(doctorModel.getValueAt(i, 0));
            
            writer.println("[FACULTIES]");
            for (int i = 0; i < facultyModel.getRowCount(); i++) writer.println(facultyModel.getValueAt(i, 0));
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private void loadData() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String currentSection = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.startsWith("[")) {
                    currentSection = line;
                    continue;
                }
                
                if (currentSection.equals("[STUDENTS]")) studentModel.addRow(new Object[]{line});
                else if (currentSection.equals("[DOCTORS]")) doctorModel.addRow(new Object[]{line});
                else if (currentSection.equals("[FACULTIES]")) facultyModel.addRow(new Object[]{line});
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JAVAProjectGUI app = new JAVAProjectGUI();
            app.setVisible(true);
        });
    }
}
