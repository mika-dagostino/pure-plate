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
        this.driverId = -1;
    }

    public void displayHomeMenu() {
        while (true) {
            try {
                    Utils.printMenu("Driver Registration", "Driver Login", "Exit");

                    int choice = pure.plate.Input.getInt("Enter your choice: ");
                    switch (choice) {
                        case 1 -> {
                            boolean success = register();
                            if (success) {
                                displayActionMenu();
                            } else {
                                System.out.println("Registration failed. Please try again.");
                            }
                        }
                        
                        case 2 -> {
                            boolean success = login();
                            if (success) {
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
                    Utils.printMenu("View Delivery History", "Get a new Delivery", "View Current Deliveries", "Update a Delivery Status", "Logout");

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
                            UpdateDeliveryStatus();
                        }
                        case 5 -> {
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
            String name = pure.plate.Validator.getString("Enter your name: ", "Name", 3, 100);
            String email = pure.plate.Validator.getEmail("Enter your email: ");
            String address = pure.plate.Validator.getString("Enter your address: ", "Address", 1, 200);
            String phoneNumber = pure.plate.Validator.getString("Enter your phone number: ", "Phone Number", 1, 20);
            String licensePlate = pure.plate.Validator.getString("Enter your license plate: ", "License Plate", 1, 20);
            String carModel = pure.plate.Validator.getString("Enter your car model: ", "Car Model", 1, 100);
            String password = pure.plate.Validator.getString("Enter your password: ", "Password", 6, 100);
            int userid = generateUserId();
            
            Utils.printWithSeparators("Successfully registered driver " + name + "\nWith email: " + email);

            // Register the user information
            String registerUserQuery = Files.readString(Paths.get("src/sql_queries/register_user.sql"));
            db.runStatement(registerUserQuery, userid, address, email, phoneNumber, name, password);
            
            // Register the driver-specific information
            String registerDriverQuery = Files.readString(Paths.get("src/sql_queries/driver/register_driver.sql"));
            db.runStatement(registerDriverQuery,userid, licensePlate, carModel, 35000);

            this.driverId = userid; 
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

            // Run the SQL query to check if the driver ID and password are correct
            String query = Files.readString(Paths.get("src/sql_queries/driver/login.sql"));
            QueryResult result = db.runSelectStatement(query, driver_email, password);

            // If the result is empty, the login failed
            if (result.rows.isEmpty()) {
                System.out.println("Invalid driver email or password.");
                return false;
            }

            Utils.printWithSeparators("Login successful! \nWelcome back, " + result.rows.get(0).get(1) + "!");
            this.driverId = (int) result.rows.get(0).get(0);
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

    public void UpdateDeliveryStatus() {
        System.out.println("Updating delivery status...");
        String query;

        // Sanity check
        if (this.driverId == -1) {
            System.out.println("Driver ID not set. Please login first.");
            return;
        }

        try {
            query = Files.readString(Paths.get("src/sql_queries/driver/current_deliveries.sql"));
            QueryResult result = db.runSelectStatement(query, this.driverId);
            if (result.rows.isEmpty()) {
                System.out.println("No current deliveries to update.");
                return;
            }
            Formatting.printTable(result);
            ArrayList<Integer> deliveryIds = new ArrayList<>();
            for (ArrayList<Object> row : result.rows) {
                deliveryIds.add((Integer) row.get(0)); 
            }
            int deliveryId = pure.plate.Input.getInt("Enter the ID of the delivery you want to update or 0 to cancel: ");
            
            if (deliveryId == 0) {
                System.out.println("No delivery selected. Returning to menu.");
                return;
            }
            else if (!deliveryIds.contains(deliveryId)) {
                System.out.println("Invalid delivery ID.");
                return;
            }
            
            
            System.out.println("Choose the new status for the delivery:");
            Utils.printMenu("in_delivery", "delivered", "cancelled");
            String newStatus = pure.plate.Input.getString("Choose an option: ");
            switch (newStatus) {
                case "1" -> newStatus = "in_delivery";
                case "2" -> newStatus = "delivered";
                case "3" -> newStatus = "cancelled";
                default -> {
                    System.out.println("Invalid status choice. Returning to menu.");
                    return;
                }
            }

            String updateQuery = Files.readString(Paths.get("src/sql_queries/driver/update_delivery_status.sql"));
            db.runStatement(updateQuery, newStatus, deliveryId, this.driverId);
            Utils.printWithSeparators("Delivery status updated successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred while updating delivery status: " + e.getMessage());
        }
    }

    public int generateUserId() {
        long timestamp = System.currentTimeMillis() % 1000000000; // Get the last 9 digits of the current timestamp
        return (int) timestamp;
    }

}
