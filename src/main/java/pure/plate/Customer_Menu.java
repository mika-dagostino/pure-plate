package pure.plate;

public class Customer_Menu {
    private final DBConnect db;

    public Customer_Menu(DBConnect db) {
        this.db = db;
    }

    public void displayHomeMenu() {
        try {
                System.out.println("1. Customer Registration");
                System.out.println("2. Customer Login");
                System.out.println("3. Exit");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> register();
                    case 2 -> login();
                    case 3 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            } 
    }

    public static void displayActionMenu() {
        try {
                System.out.println("1. View Order History");
                System.out.println("2. List all meals available");
                System.out.println("3. View Cart");
                System.out.println("4. Logout");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> System.out.println("Viewing order history...");
                    case 2 -> System.out.println("Listing all meals available...");
                    case 3 -> System.out.println("Viewing cart...");
                    case 4 -> System.out.println("Logging out...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            } 
    }

    
    public boolean login() {
 
        try {
            System.out.print("Enter Customer ID: ");
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
                this.driver_id = driver_id;
                return true;
            } else {
                System.out.println("Invalid driver ID or password.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return false;
        }

        try {
            System.out.print("Enter username: ");
            String username = pure.plate.Input.getString("Enter username: ");
            System.out.print("Enter password: ");
            String password = pure.plate.Input.getString("Enter password: ");
            // Here you would add logic to verify the username and password
            System.out.println("user: " + username + ", password: " + password);

            System.out.println("Login successful for user: " + username);
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return false;
        }


    }

    public boolean register() {
        try {
            String name = pure.plate.Input.getString("Enter your name: ");
            String email = pure.plate.Input.getString("Enter your email: ");
            String password = pure.plate.Input.getString("Enter your password: ");
            // Here you can add code to save the customer's information to a database or file
            System.out.println(("Registering: " + name + " " + email + " " + password));
            System.out.println("Customer registered successfully!");

            return true;
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public static void ViewCart() {
        System.out.println("Viewing cart...");
    }

    public static void AddToCart() {
        System.out.println("Adding to cart...");
    }

    public static void DeleteFromCart() {
        System.out.println("Deleting from cart...");
    }
    
    public static void OrderCart() {
        System.out.println("Ordering cart...");
    }

    public static void GetPayementInfo() {
        System.out.println("Getting payment info...");
    }
    
}
