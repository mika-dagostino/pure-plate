package pure.plate;

public class Main {
    public static void main(String[] args) {
        // Connect to the database
        DBConnect db = new DBConnect();

        // Run the main loop
        Menu.run();

        // Disconnect from the database
        db.closeDBConnection();
    }
}