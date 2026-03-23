package pure.plate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.management.Query;

    
public class Driver_Menu {
    private final DBConnect db;
    private int driverId;

    public Driver_Menu(DBConnect db) {
        this.db = db;
        this.driverId = -1;
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
        while (true) {
            try {
                    System.out.println("1. View Delivery History");
                    System.out.println("2. Get a New Delivery");
                    System.out.println("3. View Current Deliveries");
                    System.out.println("4. Logout");
                    int choice = pure.plate.Input.getInt("Enter your choice: ");
                    switch (choice) {
                        case 1 -> {
                            ViewDeliveryHistory();
                        }
                        case 2 -> {
                            GetNewDelivery();
                        }
                        case 3 -> {
                            ViewCurrentDeliveries();
                        }
                        case 4 -> {
                            System.out.println("Logging out...");
                            this.driverId = -1; // Reset driver ID on logout
                            return;
                        }
                        default -> System.out.println("Invalid choice. Please try again.");
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                } 
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
            db.runStatement(registerDriverQuery,userid, licensePlate, carModel, 35000);

            System.out.println("Driver registered successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public boolean login() { 
        try {
            String driver_email = pure.plate.Input.getString("Enter your email: ");
            String password = pure.plate.Input.getString("Enter your password: ");

            // Here you would add logic to verify the username and password
            System.out.println("user: " + driver_email + ", password: " + password);
            
            // Run the SQL query to check if the driver ID and password are correct
            String query = Files.readString(Paths.get("src/sql_queries/driver/login.sql"));
            QueryResult result = db.runSelectStatement(query, driver_email, password);

            // If the result is empty, the login failed
            if (result.rows.isEmpty()) {
                System.out.println("Invalid driver email or password.");
                return false;
            }


            this.driverId = (int) result.rows.get(0).get(0);
            System.out.println("Login successful for user: " + driverId);
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return false;
        }
    }

    public void ViewDeliveryHistory() {
        System.out.println("Viewing delivery history...");
        String query;
        
        // Sanity check
        if (this.driverId == -1) {
            System.out.println("Driver ID not set. Please login first.");
            return;
        }
        
        try {
            query = Files.readString(Paths.get("src/sql_queries/driver/delivery_history.sql"));
            QueryResult result = db.runSelectStatement(query, this.driverId);
            Formatting.printTable(result);
        } catch (IOException ex) {
            System.getLogger(Driver_Menu.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public void GetNewDelivery() {
        QueryResult requests = GetDriverRequests();
        if (requests.rows.isEmpty()) {
            System.out.println("No delivery requests available at the moment.");
        } else {
            System.out.println("Available delivery requests:");
            Formatting.printTable(requests);
            ArrayList<Integer> requestIds = new ArrayList<>();
            for (ArrayList<Object> row : requests.rows) {
                requestIds.add((Integer) row.get(0)); 
            }
            int requestId = pure.plate.Input.getInt("Enter the ID of the delivery request you want to accept or 0 to cancel: ");
            
            if (requestId == 0) {
                System.out.println("No request selected. Returning to menu.");
                return;
            }
            else if (!requestIds.contains(requestId)) {
                System.out.println("Invalid request ID.");
                return;
            }

            try {
                String acceptDeliveryQuery = Files.readString(Paths.get("src/sql_queries/driver/accept_delivery.sql"));
                db.runStatement(acceptDeliveryQuery, this.driverId, requestId);
                System.out.println("You have accepted delivery request ID: " + requestId);
            } catch (IOException e) {
                System.out.println("An error occurred while accepting the delivery request: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public QueryResult GetDriverRequests() {
        System.out.println("Fetching driver requests...");
        String query;

        try {
            query = Files.readString(Paths.get("src/sql_queries/driver/driver_requests.sql"));
            QueryResult result = db.runSelectStatement(query);
            return result;
        } catch (IOException e) {
            System.out.println("An error occurred while fetching driver requests: " + e.getMessage());
        }
        return new QueryResult(new ArrayList<>(), new ArrayList<>());
    }

    public void ViewCurrentDeliveries() {
        System.out.println("Viewing current deliveries...");
        String query;

        // Sanity check
        if (this.driverId == -1) {
            System.out.println("Driver ID not set. Please login first.");
            return;
        }

        try {
            query = Files.readString(Paths.get("src/sql_queries/driver/current_deliveries.sql"));
            QueryResult result = db.runSelectStatement(query, this.driverId);
            Formatting.printTable(result);
        } catch (IOException e) {
            System.out.println("An error occurred while fetching current deliveries: " + e.getMessage());
        }
    }

    public int generateUserId() {
        long timestamp = System.currentTimeMillis() % 1000000000; // Get the last 9 digits of the current timestamp
        return (int) timestamp;
    }

}
