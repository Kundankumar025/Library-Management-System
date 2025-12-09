import javax.swing.*;
public class MainGUI extends JFrame {
    JButton btnStudent = new JButton("Student");
    JButton btnBook = new JButton("Book");
    JButton btnIssue = new JButton("Issue/Return");
    public MainGUI() {
        setTitle("Library Dashboard");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        btnStudent.setBounds(50, 50, 300, 40);
        btnBook.setBounds(50, 120, 300, 40);
        btnIssue.setBounds(50, 190, 300, 40);
        add(btnStudent);
        add(btnBook);
        add(btnIssue);
        btnStudent.addActionListener(_ -> new StudentGUI().setVisible(true));
        btnBook.addActionListener(_ -> new BookGUI().setVisible(true));
        btnIssue.addActionListener(_ -> new IssueReturnGUI().setVisible(true));  }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Show login page first
            new LoginGUI().setVisible(true);
        });
    }
}
