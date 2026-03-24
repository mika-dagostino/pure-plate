package pure.plate;

public class Customer_Menu {
    private final CustomerController controller;
    private boolean continueLoopWelcome = true;
    private boolean continueLoopMain = false;

    public Customer_Menu(DBConnect db) {
        this.controller = new CustomerController(db);
    }

    public void displayHomeMenu() {
        while(continueLoopWelcome) {
            try {
                System.out.println("\n================================================");
                System.out.println("1. Customer Registration");
                System.out.println("2. Customer Login");
                System.out.println("3. Exit");
                System.out.println("================================================\n");

                int choice = pure.plate.Input.getInt("Enter your choice: ");

                switch (choice) {
                    case 1 -> {
                        boolean response = controller.register();
                        if (response) {
                            this.continueLoopMain = true;
                            this.continueLoopWelcome = false;
                            displayActionMenu();
                        }
                    }
                    case 2 -> {
                        boolean response = controller.login();
                        if (response) {
                            this.continueLoopMain = true;
                            this.continueLoopWelcome = false;
                            displayActionMenu();
                        }
                    }
                    case 3 -> {
                        this.continueLoopWelcome = false;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    public void displayActionMenu() {
        while(continueLoopMain) {
            try {
                System.out.println("\n================================================");
                System.out.println("1. View Order History");
                System.out.println("2. Search for meals");
                System.out.println("3. View Cart");
                System.out.println("4. Write a review");
                System.out.println("5. View my information");
                System.out.println("6. Logout");
                System.out.println("================================================\n");


                int choice = Input.getInt("Enter your choice: ");

                switch (choice) {
                    case 1 -> controller.viewOrderHistory();
                    case 2 -> controller.searchForMeals();
                    case 3 -> controller.viewCart();
                    case 4 -> controller.writeReview();
                    case 5 -> controller.viewMyInformation();
                    case 6 -> {
                        controller.logout();
                        System.out.println("Logging out...");
                        this.continueLoopWelcome = true;
                        this.continueLoopMain = false;
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
