package pure.plate;

public class Main {
    public static void main(String[] args) {
        // Connect to the database
        DBConnect db = new DBConnect();
        WelcomeMenu menu = new WelcomeMenu(db);

        // Run the main loop
        menu.display();

        // Disconnect from the database
        db.closeDBConnection();
    }
}