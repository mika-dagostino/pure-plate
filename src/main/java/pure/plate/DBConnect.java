package pure.plate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    public ArrayList<Object> runSelectStatement(String sqlString, Object... params) {
        if (!isConnected()) return new ArrayList<>();

        try {
            PreparedStatement stmt = conn.prepareStatement(sqlString);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }


            ResultSet rs = stmt.executeQuery();
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
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void runStatement(String sqlString, Object... params) {
        if (!isConnected()) return;

        try (PreparedStatement stmt = conn.prepareStatement(sqlString)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Statement executed successfully, rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.out.println("Error running: " + sqlString);
            e.printStackTrace();
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
