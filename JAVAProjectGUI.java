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

    // Fancy Light Color Palette
    private final Color PRIMARY_BG = new Color(245, 247, 250);
    private final Color SECONDARY_BG = new Color(255, 255, 255);
    private final Color STUDENT_COLOR = new Color(52, 152, 219);
    private final Color DOCTOR_COLOR = new Color(46, 204, 113);
    private final Color FACULTY_COLOR = new Color(155, 89, 182);
    private final Color TEXT_COLOR = new Color(30, 30, 30);

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
        studentModel = new DefaultTableModel(new String[]{"ID", "Student Name", "Phone", "Email"}, 0);
        studentTable = createStyledTable(studentModel, STUDENT_COLOR);
        tabbedPane.addTab("🎓 Students", createEntryPanel(new String[]{"ID", "Name", "Phone", "Email"}, studentModel, studentTable, STUDENT_COLOR));

        // 2. Doctors Tab
        doctorModel = new DefaultTableModel(new String[]{"ID", "Doctor Name", "Phone", "Email", "Specialization"}, 0);
        doctorTable = createStyledTable(doctorModel, DOCTOR_COLOR);
        tabbedPane.addTab("👨‍🏫 Doctors", createEntryPanel(new String[]{"ID", "Name", "Phone", "Email", "Specialization"}, doctorModel, doctorTable, DOCTOR_COLOR));

        // 3. Faculties Tab
        facultyModel = new DefaultTableModel(new String[]{"ID", "Faculty Name", "Phone", "Email", "Department"}, 0);
        facultyTable = createStyledTable(facultyModel, FACULTY_COLOR);
        tabbedPane.addTab("🏢 Faculties", createEntryPanel(new String[]{"ID", "Name", "Phone", "Email", "Department"}, facultyModel, facultyTable, FACULTY_COLOR));

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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 40));
        btn.setBorder(BorderFactory.createEmptyBorder());
        return btn;
    }

    private JPanel createEntryPanel(String[] labels, DefaultTableModel model, JTable table, Color themeColor) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(SECONDARY_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Input Area Container
        JPanel inputContainer = new JPanel(new BorderLayout(10, 10));
        inputContainer.setBackground(SECONDARY_BG);

        // Fields Panel
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 3, 15, 15)); // 3 columns, dynamic rows
        fieldsPanel.setBackground(SECONDARY_BG);
        
        JTextField[] textFields = new JTextField[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
            fieldPanel.setBackground(SECONDARY_BG);
            
            JLabel label = new JLabel(labels[i] + ":");
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setForeground(TEXT_COLOR);
            
            textFields[i] = new JTextField();
            textFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            textFields[i].setBackground(Color.WHITE);
            textFields[i].setForeground(TEXT_COLOR);
            textFields[i].setCaretColor(TEXT_COLOR);
            textFields[i].setPreferredSize(new Dimension(150, 35));
            textFields[i].setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            fieldPanel.add(label, BorderLayout.NORTH);
            fieldPanel.add(textFields[i], BorderLayout.CENTER);
            fieldsPanel.add(fieldPanel);
        }

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonsPanel.setBackground(SECONDARY_BG);

        JButton addButton = createStyledButton("Add", themeColor);
        JButton updateButton = createStyledButton("Update", new Color(41, 128, 185)); // Blue
        JButton deleteButton = createStyledButton("Delete", new Color(192, 57, 43)); // Red

        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);

        inputContainer.add(fieldsPanel, BorderLayout.CENTER);
        inputContainer.add(buttonsPanel, BorderLayout.SOUTH);

        // Table Area
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(236, 240, 241), 2));
        scrollPane.getViewport().setBackground(SECONDARY_BG);

        panel.add(inputContainer, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Table Selection Action
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                for (int i = 0; i < textFields.length; i++) {
                    Object val = model.getValueAt(table.getSelectedRow(), i);
                    textFields[i].setText(val != null ? val.toString() : "");
                }
            }
        });

        // Button Actions
        addButton.addActionListener(e -> {
            boolean isValid = true;
            Object[] rowData = new Object[textFields.length];
            for (int i = 0; i < textFields.length; i++) {
                String text = textFields[i].getText().trim();
                if (text.isEmpty()) isValid = false;
                rowData[i] = text;
            }

            if (isValid) {
                model.addRow(rowData);
                for (JTextField tf : textFields) tf.setText("");
                saveData();
                JOptionPane.showMessageDialog(this, "Successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                boolean isValid = true;
                Object[] rowData = new Object[textFields.length];
                for (int i = 0; i < textFields.length; i++) {
                    String text = textFields[i].getText().trim();
                    if (text.isEmpty()) isValid = false;
                    rowData[i] = text;
                }

                if (isValid) {
                    for (int i = 0; i < textFields.length; i++) {
                        model.setValueAt(rowData[i], selectedRow, i);
                    }
                    for (JTextField tf : textFields) tf.setText("");
                    table.clearSelection();
                    saveData();
                    JOptionPane.showMessageDialog(this, "Successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Please fill all fields to update!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to update!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    model.removeRow(selectedRow);
                    for (JTextField tf : textFields) tf.setText("");
                    saveData();
                    JOptionPane.showMessageDialog(this, "Successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a row to delete!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model, Color headerColor) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(SECONDARY_BG);
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
        for(int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            writer.println("[STUDENTS]");
            for (int i = 0; i < studentModel.getRowCount(); i++) writer.println(String.join("|", getRowData(studentModel, i)));
            
            writer.println("[DOCTORS]");
            for (int i = 0; i < doctorModel.getRowCount(); i++) writer.println(String.join("|", getRowData(doctorModel, i)));
            
            writer.println("[FACULTIES]");
            for (int i = 0; i < facultyModel.getRowCount(); i++) writer.println(String.join("|", getRowData(facultyModel, i)));
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
        }
    }

    private String[] getRowData(DefaultTableModel model, int row) {
        String[] data = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            Object val = model.getValueAt(row, i);
            data[i] = val != null ? val.toString() : "";
        }
        return data;
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
                
                String[] parts = line.split("\\|");
                if (currentSection.equals("[STUDENTS]")) addRowFromParts(studentModel, parts);
                else if (currentSection.equals("[DOCTORS]")) addRowFromParts(doctorModel, parts);
                else if (currentSection.equals("[FACULTIES]")) addRowFromParts(facultyModel, parts);
            }
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());
        }
    }

    private void addRowFromParts(DefaultTableModel model, String[] parts) {
        Object[] row = new Object[model.getColumnCount()];
        if (parts.length == 1) { // Migrate old data
            row[0] = "N/A";
            row[1] = parts[0];
            for (int i = 2; i < row.length; i++) row[i] = "N/A";
        } else {
            for (int i = 0; i < parts.length && i < row.length; i++) row[i] = parts[i];
            for (int i = parts.length; i < row.length; i++) row[i] = "";
        }
        model.addRow(row);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JAVAProjectGUI app = new JAVAProjectGUI();
            app.setVisible(true);
        });
    }
}
