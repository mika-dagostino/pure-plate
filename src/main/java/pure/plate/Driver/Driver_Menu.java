package pure.plate.Driver;

public class Driver_Menu {
    public static void displayHomeMenu() {
        try {
                System.out.println("1. Driver Registration");
                System.out.println("2. Driver Login");
                System.out.println("3. Exit");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> Driver_Registration.register();
                    case 2 -> Driver_Login.login();
                    case 3 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            } 
    }

    public static void displayActionMenu() {
        try {
                System.out.println("1. View Delivery History");
                System.out.println("2. View Driver Requests");
                System.out.println("3. Logout");
                int choice = pure.plate.Input.getInt("Enter your choice: ");
                switch (choice) {
                    case 1 -> System.out.println("Viewing delivery history...");
                    case 2 -> ViewDriverRequests();
                    case 3 -> System.out.println("Logging out...");
                    case 4 -> System.out.println("Logging out...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            } 
    }

    public static void ViewDriverRequests() {
        System.out.println("Viewing driver requests...");
    }

    public static void AcceptDeliveryRequest() {
        System.out.println("Accepting delivery request...");
    }

    public static void RejectDeliveryRequest() {
        System.out.println("Rejecting delivery request...");
    }
}
