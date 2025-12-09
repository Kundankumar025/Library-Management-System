import javax.swing.*;
import java.awt.event.*;

public class LoginGUI extends JFrame implements ActionListener {
    JTextField txtUsername = new JTextField();
    JPasswordField txtPassword = new JPasswordField();
    JButton btnLogin = new JButton("Login");

    public LoginGUI() {
        setTitle("Login");
        setSize(500, 350);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel l1 = new JLabel("Username:");
        JLabel l2 = new JLabel("Password:");
        l1.setBounds(30, 30, 80, 30);
        l2.setBounds(30, 70, 80, 30);
        txtUsername.setBounds(120, 30, 180, 30);
        txtPassword.setBounds(120, 70, 180, 30);
        btnLogin.setBounds(120, 120, 100, 30);

        add(l1); add(l2); add(txtUsername); add(txtPassword); add(btnLogin);

        btnLogin.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (username.equals("admin") && password.equals("1234")) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                this.dispose(); // close login window
                new MainGUI().setVisible(true); // open dashboard
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password!");
            }
        }
    }
}
