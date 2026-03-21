package pure.plate;

import java.sql.*;
import java.util.ArrayList;

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

    public ArrayList<Object> runSelectStatement(String sqlString) {
        if (!isConnected()) return new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlString);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            ArrayList<Object> results = new ArrayList<>();

            while (rs.next()) {
                if (columnCount == 1) {
                    results.add(rs.getObject(1));
                } else {
                    ArrayList<Object> row = new ArrayList<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject((i)));
                    }

                    results.add(row);
                }
            }

            rs.close();
            stmt.close();

            return results;
        } catch (SQLException e) {
            System.out.println("Error running: " + sqlString);
            return new ArrayList<>();
        }
    }

    public void runStatement(String sqlString) {
        if (!isConnected()) return;

        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sqlString);
        } catch (SQLException e) {
            System.out.println("Error running: " + sqlString);
        }
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
