import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class StudentGUI extends JFrame {
    JTextField txtId = new JTextField(), txtName = new JTextField(), txtClass = new JTextField(), txtEmail = new JTextField(), txtMobile = new JTextField();
    JButton btnAdd = new JButton("Add"), btnUpdate = new JButton("Update"), btnDelete = new JButton("Delete"), btnSearch = new JButton("Search");

    JTable table;
    DefaultTableModel tableModel;

    public StudentGUI() {
        setTitle("Manage Students");
        setSize(700, 500);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel l1 = new JLabel("ID:"), l2 = new JLabel("Name:"), l3 = new JLabel("Class:"), l4 = new JLabel("Email:"), l5 = new JLabel("Mobile:");
        l1.setBounds(50, 30, 100, 30); txtId.setBounds(150, 30, 200, 30);
        l2.setBounds(50, 70, 100, 30); txtName.setBounds(150, 70, 200, 30);
        l3.setBounds(50, 110, 100, 30); txtClass.setBounds(150, 110, 200, 30);
        l4.setBounds(50, 150, 100, 30); txtEmail.setBounds(150, 150, 200, 30);
        l5.setBounds(50, 190, 100, 30); txtMobile.setBounds(150, 190, 200, 30);

        btnAdd.setBounds(50, 230, 80, 30);
        btnUpdate.setBounds(150, 230, 80, 30);
        btnDelete.setBounds(250, 230, 80, 30);
        btnSearch.setBounds(350, 30, 80, 30);

        add(l1); add(txtId); add(l2); add(txtName); add(l3); add(txtClass);
        add(l4); add(txtEmail); add(l5); add(txtMobile);
        add(btnAdd); add(btnUpdate); add(btnDelete); add(btnSearch);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Class", "Email", "Mobile"}, 0);
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(50, 280, 600, 160);
        add(scroll);

        loadTable();

        btnAdd.addActionListener(_ -> { addStudent(); loadTable(); });
        btnUpdate.addActionListener(_ -> { updateStudent(); loadTable(); });
        btnDelete.addActionListener(_ -> { deleteStudent(); loadTable(); });
        btnSearch.addActionListener(_ -> searchStudent());
    }

    private void addStudent() {
        try {
            // Email ko bhi required field banaya gaya hai
            if (txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()
                    || txtClass.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()
                    || txtMobile.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Name, Class, Email, and Mobile are required!");
                return;
            }

            int id = Integer.parseInt(txtId.getText().trim());
            String email = txtEmail.getText().trim();
            String mobile = txtMobile.getText().trim();

            if (!mobile.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Mobile number must be 10 digits!");
                return;
            }

            // Simple email validation
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email address!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement check = c.prepareStatement("SELECT id FROM student WHERE id=?");
            check.setInt(1, id);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "ID already exists!");
                return;
            }

            PreparedStatement pst = c.prepareStatement("INSERT INTO student(id,name,class,email,mobile) VALUES(?,?,?,?,?)");
            pst.setInt(1, id);
            pst.setString(2, txtName.getText().trim());
            pst.setString(3, txtClass.getText().trim());
            pst.setString(4, email);
            pst.setString(5, mobile);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Student Added Successfully!");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            if (txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()
                    || txtClass.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty()
                    || txtMobile.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Name, Class, Email, and Mobile are required!");
                return;
            }

            int id = Integer.parseInt(txtId.getText().trim());
            String email = txtEmail.getText().trim();
            String mobile = txtMobile.getText().trim();

            if (!mobile.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Mobile number must be 10 digits!");
                return;
            }

            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                JOptionPane.showMessageDialog(this, "Enter a valid email address!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement pst = c.prepareStatement("UPDATE student SET name=?, class=?, email=?, mobile=? WHERE id=?");
            pst.setString(1, txtName.getText().trim());
            pst.setString(2, txtClass.getText().trim());
            pst.setString(3, email);
            pst.setString(4, mobile);
            pst.setInt(5, id);

            int updated = pst.executeUpdate();
            if (updated > 0) {
                JOptionPane.showMessageDialog(this, "Student Updated Successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No student found with that ID!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            Connection c = Connect.ConnectToDB();
            PreparedStatement pst = c.prepareStatement("DELETE FROM student WHERE id=?");
            pst.setInt(1, id);
            int deleted = pst.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(this, "Student Deleted Successfully!");
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "No student found with that ID!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void searchStudent() {
        try {
            String idText = txtId.getText().trim();
            String nameText = txtName.getText().trim();

            if (idText.isEmpty() && nameText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Student ID or Name to search!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement pst;
            if (!idText.isEmpty()) {
                pst = c.prepareStatement("SELECT * FROM student WHERE id=?");
                pst.setInt(1, Integer.parseInt(idText));
            } else {
                pst = c.prepareStatement("SELECT * FROM student WHERE name LIKE ?");
                pst.setString(1, "%" + nameText + "%");
            }

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtId.setText(rs.getString("id"));
                txtName.setText(rs.getString("name"));
                txtClass.setText(rs.getString("class"));
                txtEmail.setText(rs.getString("email"));
                txtMobile.setText(rs.getString("mobile"));
            } else {
                JOptionPane.showMessageDialog(this, "Student not found!");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            Connection c = Connect.ConnectToDB();
            PreparedStatement pst = c.prepareStatement("SELECT * FROM student");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("class"),
                        rs.getString("email"),
                        rs.getString("mobile")
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtClass.setText("");
        txtEmail.setText("");
        txtMobile.setText("");
    }

    public static void main(String[] args) {
        new StudentGUI().setVisible(true);
    }
}
