package pure.plate.Customer;

public class Customer_Menu {

    public static void displayHomeMenu() {
        try {
                System.out.println("1. Customer Registration");
                System.out.println("2. Customer Login");
                System.out.println("3. Exit");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> Customer_Registration.register();
                    case 2 -> Customer_Login.login();
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
