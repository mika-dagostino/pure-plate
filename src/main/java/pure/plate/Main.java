package pure.plate;

public class Main {
    public static void main(String[] args) {
        // Connect to the database
        DBConnect db = new DBConnect();
        WelcomeMenu menu = new WelcomeMenu(db);

        if (!db.isConnected()) {
            System.out.println("\n\n==============================================================");
            System.out.println("There was an error connecting to the DB, please try again later :(");
            System.out.println("==============================================================\n\n");

            return;
        }

        // Run the main loop
        menu.display();

        // Disconnect from the database
        db.closeDBConnection();
    }
}