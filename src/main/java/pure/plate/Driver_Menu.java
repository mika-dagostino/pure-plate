package pure.plate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

    
public class Driver_Menu {
    private final DBConnect db;
    private int driverId;

    public Driver_Menu(DBConnect db) {
        this.db = db;
    }

    public void displayHomeMenu() {
        while (true) {
            try {
                    System.out.println("1. Driver Registration");
                    System.out.println("2. Driver Login");
                    System.out.println("3. Exit");
                    int choice = pure.plate.Input.getInt("Enter your choice: ");
                    switch (choice) {
                        case 1 -> {
                            boolean success = register();
                            if (success) {
                                System.out.println("Please login to continue.");
                            } else {
                                System.out.println("Registration failed. Please try again.");
                            }
                        }
                        
                        case 2 -> {
                            boolean success = login();
                            if (success) {
                                System.out.println("Login successful!");
                                displayActionMenu();
                            } else {
                                System.out.println("Login failed. Please try again.");
                            }
                        }
                        
                        case 3 -> {
                            System.out.println("Exiting...");
                            return;
                        }
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                } 
        }
    }

    public void displayActionMenu() {
        try {
                System.out.println("1. View Delivery History");
                System.out.println("2. View Driver Requests");
                System.out.println("3. Logout");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> {
                        ViewDeliveryHistory();
                    }
                    case 2 -> {
                        ViewDriverRequests();
                    }
                    case 3 -> System.out.println("Logging out...");
                    case 4 -> System.out.println("Logging out...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            } 
    }

    public boolean register() {
        try {
            String name = pure.plate.Input.getString("Enter your name: ");
            String email = pure.plate.Input.getString("Enter your email: ");
            String address = pure.plate.Input.getString("Enter your address: ");
            String phoneNumber = pure.plate.Input.getString("Enter your phone number: ");
            String licensePlate = pure.plate.Input.getString("Enter your license plate: ");
            String carModel = pure.plate.Input.getString("Enter your car model: ");
            String password = pure.plate.Input.getString("Enter your password: ");
            int userid = generateUserId();
            
            System.out.println(("Registering: " + name + " " + email + " " + password));

            // Register the user information
            String registerUserQuery = Files.readString(Paths.get("src/sql_queries/register_user.sql"));
            db.runStatement(registerUserQuery, userid, address, email, phoneNumber, name, password);
            
            // Register the driver-specific information
            String registerDriverQuery = Files.readString(Paths.get("src/sql_queries/driver/register_driver.sql"));
            db.runStatement(registerDriverQuery,licensePlate, carModel);

            System.out.println("Driver registered successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public boolean login() { 
        try {
            System.out.print("Enter driver ID: ");
            int driver_id = pure.plate.Input.getInt("Enter driver ID: ");
            System.out.print("Enter password: ");
            String password = pure.plate.Input.getString("Enter password: ");
            // Here you would add logic to verify the username and password
            System.out.println("user: " + driver_id + ", password: " + password);
            
            // Run the SQL query to check if the driver ID and password are correct
            String query = Files.readString(Paths.get("src/sql_queries/driver/login.sql"));
            ArrayList<Object> result = db.runSelectStatement(query, driver_id, password);
            if (result.get(0).equals(1)) {
                System.out.println("Login successful for user: " + driver_id);
                this.driverId = driver_id;
                return true;
            } else {
                System.out.println("Invalid driver ID or password.");
                return false;
            }
        } catch (IOException e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return false;
        }
    }

    public void ViewDeliveryHistory() {
        System.out.println("Viewing delivery history...");
        String query;
        try {
            query = Files.readString(Paths.get("src/sql_queries/customer/order_history.sql"));
            System.out.println(db.runSelectStatement(query, 8));
        } catch (IOException ex) {
            System.getLogger(Driver_Menu.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void ViewDriverRequests() {
        System.out.println("Viewing driver requests...");
    }

    public static void AcceptDeliveryRequest() {
        System.out.println("Accepting delivery request...");
    }

    public static void RejectDeliveryRequest() {
        System.out.println("Rejecting delivery request...");
    }

    public int generateUserId() {
        long timestamp = System.currentTimeMillis() % 1000000000; // Get the last 9 digits of the current timestamp
        return (int) timestamp;
    }
}
