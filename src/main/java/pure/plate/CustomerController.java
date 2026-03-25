package pure.plate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigDecimal;

public class CustomerController {
    private final DBConnect db;
    private int customerId;

    public CustomerController(DBConnect db) {
        this.db = db;
    }

    public boolean login() {
        try {
            int customerId = Input.getInt("Enter customer ID: ");
            String password = Input.getString("Enter password: ");

            String query = Files.readString(Paths.get("src/sql_queries/customer/login.sql"));
            QueryResult result = db.runSelectStatement(query, customerId, password);

            if (result.rows.getFirst().getFirst().equals(1)) {
                System.out.println("\nLogin successful for user: " + customerId + "\n");
                this.customerId = customerId;
                return true;
            } else {
                System.out.println("\n----> Invalid customer ID or password <----\n");
                return false;
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return false;
        }
    }

    public boolean register() {
        try {
            String name = Input.getString("Enter your name: ");
            String email = Input.getString("Enter your email: ");
            String password = Input.getString("Create a password: ");
            String address = Input.getString("Enter your address: ");
            String phoneNumber = Input.getString("Enter your phone number: ");

            int userId = generateId();

            String queryRegisterUser = Files.readString(Paths.get("src/sql_queries/register_user.sql"));
            db.runStatement(queryRegisterUser, userId, address, email, phoneNumber, name, password);

            String query = Files.readString(Paths.get("src/sql_queries/customer/register.sql"));
            db.runStatement(query, userId, 0);

            this.customerId = userId;

            System.out.println("\n--------------------------------------------");
            System.out.println("YOUR USER ID IS: " + this.customerId);
            System.out.println("\n--------------------------------------------");

            return true;
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        this.customerId = -1;
        this.db.closeDBConnection();
    }

    public void viewCart() throws Exception {
        boolean isLooping = true;

        while(isLooping) {
            String query = Files.readString(Paths.get("src/sql_queries/customer/view_cart.sql"));
            QueryResult result = db.runSelectStatement(query, this.customerId);
            System.out.println("\nBelow are the items in your cart:");
            Formatting.printTable(result);

            System.out.println("\nPlease select an option: ");
            System.out.println("1. Order cart");
            System.out.println("2. Delete item from cart");
            System.out.println("3. Change quantity of a cart item");
            System.out.println("4. Go Back to main menu");

            int choice = Input.getInt("Enter your choice: ");

            switch (choice) {
                case 1 -> orderCart();
                case 2 -> deleteItemFromCart();
                case 3 -> changeQuantityOfCartItem();
                case 4 -> isLooping = false;
            }
        }
    }

    private void changeQuantityOfCartItem() throws Exception {
        int itemId = Input.getInt("Enter the ID of the item you want to change the quantity of: ");
        int newQuantity = Input.getInt("Enter the new quantity: ");

        if (newQuantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        String query = Files.readString(Paths.get("src/sql_queries/customer/change_quantity_of_cart_item.sql"));
        db.runStatement(query, newQuantity, this.customerId, itemId);
    }

    private void deleteItemFromCart() throws Exception {
        int itemId = Input.getInt("Enter the ID of the item you want to delete: ");
        String query = Files.readString(Paths.get("src/sql_queries/customer/delete_item_from_cart.sql"));
        db.runStatement(query, this.customerId, itemId);
    }

    private void orderCart() throws Exception {
        String creditCardNumber = Input.getString("Enter your credit card number: ");

        int paymentId = generateId();
        int orderId = generateId();
        int deliveryDriverId = 1;

        String queryGetTotalPrice = Files.readString(Paths.get("src/sql_queries/customer/get_total_price.sql"));
        QueryResult result = db.runSelectStatement(queryGetTotalPrice, this.customerId);
        BigDecimal totalPrice = (BigDecimal) result.rows.getFirst().getFirst();

        System.out.println(totalPrice);

        // Store payment information
        String queryStorePaymentInformation = Files.readString(Paths.get("src/sql_queries/customer/store_payment_information.sql"));
        db.runStatement(queryStorePaymentInformation, paymentId, totalPrice, creditCardNumber);

        System.out.println("111111");

        // Create the new order
        String queryCreateOrder = Files.readString(Paths.get("src/sql_queries/customer/create_order.sql"));
        db.runStatement(queryCreateOrder, orderId, paymentId, this.customerId, deliveryDriverId, "preparing", totalPrice);

        System.out.println("222222");

        // Store each meal item in the database
        String queryStoreMealItems = Files.readString(Paths.get("src/sql_queries/customer/store_meal_items.sql"));
        db.runStatement(queryStoreMealItems, orderId, this.customerId);

        System.out.println("333333");

        // Remove all items from the cart
        String queryRemoveItemsFromCart = Files.readString(Paths.get("src/sql_queries/customer/remove_items_from_cart.sql"));
        db.runStatement(queryRemoveItemsFromCart, this.customerId);

        System.out.println("Order placed successfully.");
    }

    public void viewOrderHistory() throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/order_history.sql"));
        QueryResult result = db.runSelectStatement(query, this.customerId);
        Formatting.printTable(result);
    }

    public void searchForMeals() throws Exception {
        String searchTerm = Input.getString("Enter the search term (type 'all' to search all meals): ");
        if (searchTerm.equals("all")) {
            searchTerm = "%";
        }
        String query = Files.readString(Paths.get("src/sql_queries/customer/search_for_meals.sql"));
        QueryResult result = db.runSelectStatement(query, searchTerm, searchTerm);
        Formatting.printTable(result);

        System.out.println("What would you like to do: ");
        System.out.println("1. Add an item to the cart");
        System.out.println("2. Go Back to main menu");

        int choice = Input.getInt("Enter your choice: ");

        if (choice == 1) {
            addItemToCart();
        }
    }

    private void addItemToCart() throws Exception {
        int mealId = Input.getInt("Enter the ID of the meal you want to add to the cart: ");
        int quantity = Input.getInt("Enter the quantity you want to add: ");

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than 0.");
            return;
        }

        String query = Files.readString(Paths.get("src/sql_queries/customer/add_item_to_cart.sql"));
        db.runStatement(query, this.customerId, mealId, quantity);
    }

    public void writeReview() throws Exception {
        String querySeeMealsOrdered = Files.readString(Paths.get("src/sql_queries/customer/see_meals_ordered.sql"));
        QueryResult result = db.runSelectStatement(querySeeMealsOrdered, this.customerId);
        Formatting.printTable(result);
        
        int mealId = Input.getInt("Enter the ID of the meal you want to write a review for: ");
        String review = Input.getString("Enter your review: ");
        int rating = Input.getInt("Enter your rating (1-5): ");

        if (rating < 1 || rating > 5) {
            System.out.println("Invalid rating. Please enter a rating between 1 and 5.");
            return;
        }

        String queryCheckReview = Files.readString(Paths.get("src/sql_queries/customer/check_review.sql"));
        QueryResult result1 = db.runSelectStatement(queryCheckReview, this.customerId, mealId);
        int firstValue = (int) result1.rows.getFirst().getFirst();
        if (firstValue > 0) {
            System.out.println("You have already written a review for this meal.");
            return;
        }

        String queryCheckMeal = Files.readString(Paths.get("src/sql_queries/customer/check_meal.sql"));
        QueryResult result2 = db.runSelectStatement(queryCheckMeal, this.customerId, mealId);
        int firstValue2 = (int) result2.rows.getFirst().getFirst();
        if (firstValue2 == 0) {
            System.out.println("You have not ordered this meal yet.");
            return;
        }

        String queryWriteReview = Files.readString(Paths.get("src/sql_queries/customer/write_review.sql"));
        db.runStatement(queryWriteReview, this.customerId, mealId, review, rating);
    }

    public void viewMyInformation() throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/my_information.sql"));
        QueryResult result = db.runSelectStatement(query, this.customerId);
        Formatting.printTable(result);
    }

    public int generateId() {
        long timestamp = System.currentTimeMillis() % 1000000000; 
        return (int) timestamp;
    }
}
