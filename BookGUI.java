import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class BookGUI extends JFrame {
    JTextField txtId = new JTextField(), txtTitle = new JTextField(), txtAuthor = new JTextField(), txtYear = new JTextField(), txtCopies = new JTextField();
    JButton btnAdd = new JButton("Add"), btnUpdate = new JButton("Update"), btnDelete = new JButton("Delete"), btnSearch = new JButton("Search"), btnRefresh = new JButton("Refresh");
    JTable table;
    DefaultTableModel model;

    public BookGUI() {
        setTitle("Manage Books");
        setSize(700, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel l1 = new JLabel("Book ID:"), l2 = new JLabel("Title:"), l3 = new JLabel("Author:"), l4 = new JLabel("Year:"), l5 = new JLabel("Copies:");
        l1.setBounds(50, 30, 100, 30); txtId.setBounds(150, 30, 200, 30);
        l2.setBounds(50, 70, 100, 30); txtTitle.setBounds(150, 70, 200, 30);
        l3.setBounds(50, 110, 100, 30); txtAuthor.setBounds(150, 110, 200, 30);
        l4.setBounds(50, 150, 100, 30); txtYear.setBounds(150, 150, 200, 30);
        l5.setBounds(50, 190, 100, 30); txtCopies.setBounds(150, 190, 200, 30);

        btnAdd.setBounds(50, 240, 80, 30);
        btnUpdate.setBounds(150, 240, 80, 30);
        btnDelete.setBounds(250, 240, 80, 30);
        btnSearch.setBounds(370, 30, 80, 30);
        btnRefresh.setBounds(480, 30, 100, 30);

        add(l1); add(txtId); add(l2); add(txtTitle); add(l3); add(txtAuthor);
        add(l4); add(txtYear); add(l5); add(txtCopies);
        add(btnAdd); add(btnUpdate); add(btnDelete); add(btnSearch); add(btnRefresh);

        // Table for live data
        model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Year", "Copies", "Status"}, 0);
        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(50, 300, 600, 220);
        add(sp);
        // Load initial data
        loadBooks();
        // Button Actions
        btnAdd.addActionListener(_-> addBook());
        btnUpdate.addActionListener(_-> updateBook());
        btnDelete.addActionListener(_ -> deleteBook());
        btnSearch.addActionListener(_-> searchBook());
        btnRefresh.addActionListener(_-> loadBooks());
        // Table click to fill text fields
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int i = table.getSelectedRow();
                txtId.setText(model.getValueAt(i, 0).toString());
                txtTitle.setText(model.getValueAt(i, 1).toString());
                txtAuthor.setText(model.getValueAt(i, 2).toString());
                txtYear.setText(model.getValueAt(i, 3).toString());
                txtCopies.setText(model.getValueAt(i, 4).toString());
            }
        });

        setVisible(true);
    }
    // Load all books into the JTable
    private void loadBooks() {
        try {
            Connection c = Connect.ConnectToDB();
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM book");
            model.setRowCount(0); // Clear previous data
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("year"),
                        rs.getString("copies"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading data: " + ex.getMessage());
        }
    }
    private void addBook() {
        try {
            if (txtId.getText().trim().isEmpty() || txtTitle.getText().trim().isEmpty()
                    || txtAuthor.getText().trim().isEmpty() || txtYear.getText().trim().isEmpty()
                    || txtCopies.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement check = c.prepareStatement("SELECT * FROM book WHERE id=?");
            check.setString(1, txtId.getText().trim());
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Book ID already exists!");
                return;
            }

            PreparedStatement pst = c.prepareStatement("INSERT INTO book(id, title, author, year, copies, status) VALUES(?,?,?,?,?, 'Available')");
            pst.setString(1, txtId.getText().trim());
            pst.setString(2, txtTitle.getText().trim());
            pst.setString(3, txtAuthor.getText().trim());
            pst.setInt(4, Integer.parseInt(txtYear.getText().trim()));
            pst.setInt(5, Integer.parseInt(txtCopies.getText().trim()));
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Book Added Successfully!");
            clearFields();
            loadBooks(); // Auto refresh
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateBook() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Book ID to update!");
                return;
            }

            // ✅ Validate all fields before updating
            if (txtTitle.getText().trim().isEmpty() || txtAuthor.getText().trim().isEmpty() ||
                txtYear.getText().trim().isEmpty() || txtCopies.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all input fields!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement pst = c.prepareStatement("UPDATE book SET title=?, author=?, year=?, copies=? WHERE id=?");
            pst.setString(1, txtTitle.getText().trim());
            pst.setString(2, txtAuthor.getText().trim());
            pst.setInt(3, Integer.parseInt(txtYear.getText().trim()));
            pst.setInt(4, Integer.parseInt(txtCopies.getText().trim()));
            pst.setString(5, txtId.getText().trim());

            int updated = pst.executeUpdate();
            if (updated > 0)
                JOptionPane.showMessageDialog(this, "Book Updated Successfully!");
            else
                JOptionPane.showMessageDialog(this, "Book ID not found!");

            clearFields();
            loadBooks(); //  Auto refresh after update
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteBook() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Book ID to delete!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement pst = c.prepareStatement("DELETE FROM book WHERE id=?");
            pst.setString(1, txtId.getText().trim());
            int deleted = pst.executeUpdate();

            if (deleted > 0)
                JOptionPane.showMessageDialog(this, "Book Deleted Successfully!");
            else
                JOptionPane.showMessageDialog(this, "Book ID not found!");

            clearFields();
            loadBooks(); //  Auto refresh after delete
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void searchBook() {
        try {
            String id = txtId.getText().trim();
            String title = txtTitle.getText().trim();

            if (id.isEmpty() && title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter Book ID or Title to search!");
                return;
            }

            Connection c = Connect.ConnectToDB();
            PreparedStatement pst;
            if (!id.isEmpty()) {
                pst = c.prepareStatement("SELECT * FROM book WHERE id=?");
                pst.setString(1, id);
            } else {
                pst = c.prepareStatement("SELECT * FROM book WHERE title LIKE ?");
                pst.setString(1, "%" + title + "%");
            }

            ResultSet rs = pst.executeQuery();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("year"),
                        rs.getString("copies"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtYear.setText("");
        txtCopies.setText("");
    }

    public static void main(String[] args) {
        new BookGUI();
    }
}
