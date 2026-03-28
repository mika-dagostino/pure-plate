package pure.plate.customer;

import pure.plate.DBConnect;
import pure.plate.Input;
import pure.plate.Utils;

public class CustomerMenu {
    private final CustomerController controller;
    private boolean continueLoopWelcome = true;
    private boolean continueLoopMain = false;

    public CustomerMenu(DBConnect db) {
        this.controller = new CustomerController(db);
    }

    public void displayHomeMenu() {
        while(continueLoopWelcome) {
            try {
                Utils.printMenu("Customer Registration", "Customer Login", "Exit");
                int choice = Input.getInt("Enter your choice: ");

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
                    case 3 -> this.continueLoopWelcome = false;

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
                Utils.printMenu("View Order History", "Search for meals", "View Cart", "Write a review", "View my information", "Logout");
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
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
