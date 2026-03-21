package pure.plate;

import java.sql.*;
import com.ibm.db2.jcc.DB2Driver;

public class DBConnect {
    private final Connection conn;

    public DBConnect() {
        this.conn = connectToDB();
    }

    private boolean checkSecrets() {
        try {
            Class.forName("pure.plate.Secrets");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("You need a secrets file for the DB variables that is not on git, contact me on discord for the file");
            return false;
        }
    }

    private Connection connectToDB() {
        if (!checkSecrets()) return null;

        try {
            DriverManager.registerDriver(new DB2Driver());
            String connectionString = Secrets.connectionString;
            String username = Secrets.username;
            String password = Secrets.password;
            Connection c = DriverManager.getConnection(connectionString, username, password);
            System.out.println("CONNECTED TO DB");
            return c;
        } catch (SQLException e) {
            System.out.println("FAILED TO CONNECT TO DB");
            return null;
        }
    }

    public boolean isConnected() {
        try {
            return this.conn != null && !this.conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public void runStatement() {
        if (!isConnected()) return;
    }

    public void closeDBConnection() {
        if (conn == null) return;

        try {
            conn.close();
            System.out.println("CLOSED DB CONNECTION");
        } catch (SQLException e) {
            System.out.println("FAILED TO CLOSE CONNECTION");
        }
    }
}
