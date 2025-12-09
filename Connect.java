import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connect {
    private static Connection con = null;

    public static Connection ConnectToDB() {
        try {
            // Agar connection null ya closed hai, naya connection banao
            if (con == null || con.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/library?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                String user = "admin";
                String password = "1234";

                con = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Connect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return con;
    }
}
