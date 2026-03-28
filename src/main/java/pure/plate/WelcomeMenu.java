package pure.plate;

import pure.plate.customer.CustomerMenu;

import java.nio.file.Files;
import java.nio.file.Paths;

public class WelcomeMenu {
    private DBConnect db;
    
    public WelcomeMenu(DBConnect db) {
        this.db = db;
    }

    public void display() {
        while (true) {
            System.out.println("\n================================================");
            System.out.println("Welcome to Pure Plate! What kind of user are you?");
            System.out.println("1. Customer");
            System.out.println("2. Driver");
            System.out.println("3. Quit");
            System.out.println("================================================\n");

            int choice = Input.getInt("Please select an option: ");
            switch (choice) {
                case 1 -> {
                    CustomerMenu cust_menu = new CustomerMenu(this.db);
                    cust_menu.displayHomeMenu();
                }
                case 2 -> {
                    Driver_Menu driver_menu = new Driver_Menu(this.db);
                    driver_menu.displayHomeMenu();
                }
                case 3 -> {
                    this.db.closeDBConnection();
                    System.exit(0);
                }
                case 4 -> { 
                    try {
                        String query = Files.readString(Paths.get("src/sql_queries/customer/order_history.sql"));
                        System.out.println(this.db.runSelectStatement(query, 8));}
                    catch (Exception e) {
                        System.out.println("Error reading SQL file: " + e.getMessage());
                    } 

                    }
                default -> {
                    System.out.println("Invalid option, please try again.");
                }
            }
        }
    }
}
