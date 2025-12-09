import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
public class IssueReturnGUI extends JFrame {
    JComboBox<String> comboBookId = new JComboBox<>();
    JComboBox<String> comboStudentId = new JComboBox<>();
    JTextField txtTitle = new JTextField(), txtAuthor = new JTextField(), txtYear = new JTextField();
    JTextField txtCopies = new JTextField(), txtAvailable = new JTextField();
    JTextField txtStudentName = new JTextField(), txtStudentClass = new JTextField(), txtStudentMobile = new JTextField();
    JTextField txtIssueDate = new JTextField(), txtDueDate = new JTextField();
    JButton btnIssue = new JButton("Issue Book");
    JButton btnReturn = new JButton("Return Book");
    JTable table;
    DefaultTableModel tableModel;
    public IssueReturnGUI() {
        setTitle("Issue / Return Books");
        setSize(950, 700);
        setLayout(null);
        // ===== BOOK SECTION =====
        JLabel lbBook = new JLabel("BOOK DETAILS");
        lbBook.setBounds(50, 10, 200, 25);
        add(lbBook);
        JLabel l1 = new JLabel("Book ID:"), l2 = new JLabel("Title:"), l3 = new JLabel("Author:"), 
        l4 = new JLabel("Year:"), l5 = new JLabel("Total Copies:"), l6 = new JLabel("Available:");
        l1.setBounds(50,40,100,25); comboBookId.setBounds(150,40,150,25);
        l2.setBounds(50,70,100,25); txtTitle.setBounds(150,70,200,25);
        l3.setBounds(50,100,100,25); txtAuthor.setBounds(150,100,200,25);
        l4.setBounds(50,130,100,25); txtYear.setBounds(150,130,200,25);
        l5.setBounds(50,160,100,25); txtCopies.setBounds(150,160,200,25);
        l6.setBounds(50,190,100,25); txtAvailable.setBounds(150,190,200,25);
        add(l1); add(comboBookId); add(l2); add(txtTitle); add(l3); add(txtAuthor);
        add(l4); add(txtYear); add(l5); add(txtCopies); add(l6); add(txtAvailable);
        txtTitle.setEditable(false); txtAuthor.setEditable(false); txtYear.setEditable(false);
        txtCopies.setEditable(false); txtAvailable.setEditable(false);
        // ===== STUDENT SECTION =====
        JLabel lbStudent = new JLabel("STUDENT DETAILS");
        lbStudent.setBounds(50,240,200,25); add(lbStudent);
        JLabel s1 = new JLabel("Student ID:"), s2 = new JLabel("Name:"), s3 = new JLabel("Class:"), s4 = new JLabel("Mobile No:");
        s1.setBounds(50,270,100,25); comboStudentId.setBounds(150,270,150,25);
        s2.setBounds(50,300,100,25); txtStudentName.setBounds(150,300,200,25);
        s3.setBounds(50,330,100,25); txtStudentClass.setBounds(150,330,200,25);
        s4.setBounds(50,360,100,25); txtStudentMobile.setBounds(150,360,200,25);
        add(s1); add(comboStudentId); add(s2); add(txtStudentName); add(s3); add(txtStudentClass); add(s4); add(txtStudentMobile);
        txtStudentName.setEditable(false); txtStudentClass.setEditable(false); txtStudentMobile.setEditable(false);
        // ===== ISSUE SECTION =====
        JLabel lbIssue = new JLabel("ISSUE DETAILS");
        lbIssue.setBounds(50,410,200,25); add(lbIssue);
        JLabel l3a = new JLabel("Issue Date:"), l4a = new JLabel("Due Date:");
        l3a.setBounds(50,440,100,25); txtIssueDate.setBounds(150,440,150,25);
        l4a.setBounds(50,470,100,25); txtDueDate.setBounds(150,470,150,25);
        add(l3a); add(txtIssueDate); add(l4a); add(txtDueDate);
        txtIssueDate.setEditable(false);
        btnIssue.setBounds(100,520,150,30);
        btnReturn.setBounds(300,520,150,30);
        add(btnIssue); add(btnReturn);
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        txtIssueDate.setText(inputFormat.format(new Date()));
        Date due = new Date(System.currentTimeMillis() + 7L*24*60*60*1000);
        txtDueDate.setText(inputFormat.format(due));
        // ===== TABLE =====
        tableModel = new DefaultTableModel(new String[]{
                "Book ID","Title","Student ID","Student Name","Issue Date","Due Date","Return Date","Status"},0);
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(400,40,520,500); add(scroll);
        loadBookIds();
        loadStudentIds();
        comboBookId.addActionListener(_ -> loadBookDetails());
        comboStudentId.addActionListener(_-> loadStudentDetails());
        btnIssue.addActionListener(_-> { issueBook(); loadIssuedTable(); loadBookDetails(); });
        btnReturn.addActionListener(_-> { returnBook(); loadIssuedTable(); loadBookDetails(); });
        loadIssuedTable();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);    }
    private void loadBookIds() {
        try (Connection c = Connect.ConnectToDB()) {
            PreparedStatement pst = c.prepareStatement("SELECT id FROM book");
            ResultSet rs = pst.executeQuery();
            comboBookId.removeAllItems();
            while(rs.next()) comboBookId.addItem(rs.getString("id"));
        } catch(SQLException ex) { ex.printStackTrace(); }    }
    private void loadStudentIds() {
        try (Connection c = Connect.ConnectToDB()) {
            PreparedStatement pst = c.prepareStatement("SELECT id FROM student");
            ResultSet rs = pst.executeQuery();
            comboStudentId.removeAllItems();
            while(rs.next()) comboStudentId.addItem(rs.getString("id"));
        } catch(SQLException ex) { ex.printStackTrace(); }  }
    private void loadBookDetails() {
        if(comboBookId.getSelectedItem()==null) return;
        try (Connection c = Connect.ConnectToDB()) {
            PreparedStatement pst = c.prepareStatement("SELECT * FROM book WHERE id=?");
            pst.setString(1,comboBookId.getSelectedItem().toString());
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                txtTitle.setText(rs.getString("title"));
                txtAuthor.setText(rs.getString("author"));
                txtYear.setText(rs.getString("year"));
                txtCopies.setText(rs.getString("copies"));
                // Calculate available copies
                PreparedStatement pst2 = c.prepareStatement("SELECT COUNT(*) AS issuedCount FROM issued_books WHERE bookid=? AND status='Issued'");
                pst2.setString(1, comboBookId.getSelectedItem().toString());
                ResultSet rs2 = pst2.executeQuery();
                int issuedCount = 0;
                if(rs2.next()) issuedCount = rs2.getInt("issuedCount");
                int available = rs.getInt("copies") - issuedCount;
                txtAvailable.setText(String.valueOf(available));      }
        } catch(SQLException ex){ ex.printStackTrace(); }
    }
    private void loadStudentDetails() {
        if(comboStudentId.getSelectedItem()==null) return;
        try (Connection c = Connect.ConnectToDB()) {
            PreparedStatement pst = c.prepareStatement("SELECT * FROM student WHERE id=?");
            pst.setString(1, comboStudentId.getSelectedItem().toString());
            ResultSet rs = pst.executeQuery();
            if(rs.next()){
                txtStudentName.setText(rs.getString("name"));
                txtStudentClass.setText(rs.getString("class"));
                txtStudentMobile.setText(rs.getString("mobile"));
            }
        } catch(SQLException ex){ ex.printStackTrace(); }
    }
    private void issueBook() {
        if(comboBookId.getSelectedItem()==null || comboStudentId.getSelectedItem()==null){
            JOptionPane.showMessageDialog(this,"Select Book ID and Student ID first!");
            return;
        }
        try (Connection c = Connect.ConnectToDB()) {
            // Check if copies are available
            PreparedStatement pst2 = c.prepareStatement("SELECT COUNT(*) AS issuedCount FROM issued_books WHERE bookid=? AND status='Issued'");
            pst2.setString(1, comboBookId.getSelectedItem().toString());
            ResultSet rs2 = pst2.executeQuery();
            int issuedCount = 0;
            if(rs2.next()) issuedCount = rs2.getInt("issuedCount");

            PreparedStatement pstBook = c.prepareStatement("SELECT copies FROM book WHERE id=?");
            pstBook.setString(1, comboBookId.getSelectedItem().toString());
            ResultSet rsBook = pstBook.executeQuery();
            int totalCopies = 0;
            if(rsBook.next()) totalCopies = rsBook.getInt("copies");

            if(issuedCount >= totalCopies) {
                JOptionPane.showMessageDialog(this,"No available copies to issue!");
                return;
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat mysqlFormat = new SimpleDateFormat("yyyy-MM-dd");
            String issueDate = mysqlFormat.format(inputFormat.parse(txtIssueDate.getText()));
            String dueDate = mysqlFormat.format(inputFormat.parse(txtDueDate.getText()));

            PreparedStatement pst3 = c.prepareStatement(
                "INSERT INTO issued_books (bookid, studentid, issue_date, due_date, status) VALUES (?, ?, ?, ?, 'Issued')");
            pst3.setInt(1, Integer.parseInt(comboBookId.getSelectedItem().toString()));
            pst3.setInt(2, Integer.parseInt(comboStudentId.getSelectedItem().toString()));
            pst3.setString(3, issueDate);
            pst3.setString(4, dueDate);
            pst3.executeUpdate();
            JOptionPane.showMessageDialog(this,"📘 Book Issued Successfully!");
            loadBookDetails();
            loadIssuedTable();
        } catch(Exception ex){ ex.printStackTrace(); }
    }
    private void returnBook() {
    if(comboBookId.getSelectedItem() == null || comboStudentId.getSelectedItem() == null){
        JOptionPane.showMessageDialog(this,"Select Book ID and Student ID!");
        return;
    }
    try (Connection c = Connect.ConnectToDB()) {

        PreparedStatement pst = c.prepareStatement(
            "DELETE FROM issued_books WHERE bookid=? AND studentid=? LIMIT 1"
        );
        pst.setInt(1, Integer.parseInt(comboBookId.getSelectedItem().toString()));
        pst.setInt(2, Integer.parseInt(comboStudentId.getSelectedItem().toString()));
        int rows = pst.executeUpdate();
        if(rows > 0)
            JOptionPane.showMessageDialog(this,"✅ Book Returned and removed from database!");
        else
            JOptionPane.showMessageDialog(this,"⚠️ No active issue found for this student!");
        loadBookDetails();
        loadIssuedTable();
    } catch(Exception ex){ ex.printStackTrace(); }
}
    private void loadIssuedTable() {
        try (Connection c = Connect.ConnectToDB()) {
            tableModel.setRowCount(0);
            String query = """
                SELECT i.bookid, b.title, i.studentid, s.name, i.issue_date, i.due_date, i.return_date, i.status
                FROM issued_books i
                JOIN book b ON i.bookid = b.id
                JOIN student s ON i.studentid = s.id
                ORDER BY i.id DESC
            """;
            PreparedStatement pst = c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                tableModel.addRow(new Object[]{
                    rs.getString("bookid"),
                    rs.getString("title"),
                    rs.getString("studentid"),
                    rs.getString("name"),
                    rs.getString("issue_date"),
                    rs.getString("due_date"),
                    rs.getString("return_date") == null ? "" : rs.getString("return_date"),
                    rs.getString("status")
                });
            }
        } catch(Exception ex){ ex.printStackTrace(); }
    }
    public static void main(String[] args){
        new IssueReturnGUI();
    }
}
