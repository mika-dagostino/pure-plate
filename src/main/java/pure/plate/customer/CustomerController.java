package pure.plate.customer;

import pure.plate.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomerController {
    private int customerId;

    private final DBConnect db;
    private final CustomerAuth auth;
    private final CustomerCart cart;

    public CustomerController(DBConnect db) {
        this.db = db;
        this.customerId = -1;
        this.auth = new CustomerAuth(db);
        this.cart = new CustomerCart(db);
    }

    public boolean login() {
        int customerId = auth.login();
        if (customerId != -1) {
            this.customerId = customerId;
        }

        return customerId != -1;
    }

    public boolean register() {
        int customerId = auth.register();
        if (customerId != -1) {
            this.customerId = customerId;
        }

        return customerId != -1;
    }

    public void logout() {
        this.customerId = -1;
    }

    public void viewCart() throws Exception {
        boolean isLooping = true;
        boolean printCart = true;

        while(isLooping) {
            cart.viewCart(this.customerId, printCart);

            Utils.printMenuWithTitle("Please select an option:", "Order cart", "Delete item from cart", "Change quantity of a cart item", "Go Back to main menu");
            int choice = Input.getInt("Enter your choice: ");
            printCart = true;

            switch (choice) {
                case 1 -> {
                    cart.orderCart(this.customerId);
                    printCart = false;
                }
                case 2 -> cart.deleteItemFromCart(this.customerId);
                case 3 -> cart.changeQuantityOfCartItem(this.customerId);
                case 4 -> isLooping = false;
            }
        }
    }

    public void viewOrderHistory() throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/orders/history.sql"));
        QueryResult result = db.runSelectStatement(query, this.customerId);
        Formatting.printTable(result);
    }

    public void searchForMeals() throws Exception {
        String searchTerm = Input.getString("Enter the search term (type 'all' to search all meals): ");
        String query = Files.readString(Paths.get("src/sql_queries/customer/meals/search.sql"));
        if (searchTerm.equals("all")) {
            searchTerm = "%";
        }

        QueryResult result = db.runSelectStatement(query, searchTerm, searchTerm);
        Formatting.printTable(result);

        Utils.printMenuWithTitle("What would you like to do:", "Add an item to the cart", "Go Back to main menu");

        int choice = Input.getInt("Enter your choice: ");

        if (choice == 1) cart.addItemToCart(this.customerId);

    }

    public void writeReview() throws Exception {
        String querySeeMealsOrdered = Files.readString(Paths.get("src/sql_queries/customer/reviews/orderedMeals.sql"));
        QueryResult result = db.runSelectStatement(querySeeMealsOrdered, this.customerId);
        Formatting.printTable(result);
        
        int mealId = Validator.getInt("Enter the ID of the meal you want to write a review for: ", "meal ID", 1, Utils.nbrOfMeals);
        String review = Validator.getString("Enter your review: ", "review", 3, 1000);
        int rating = Validator.getInt("Enter your rating (1-5): ", "rating", 1, 5);

        if (rating == -1 || mealId == -1 || review.equals("esc")) return;

        String queryCheckReview = Files.readString(Paths.get("src/sql_queries/customer/reviews/checkReviews.sql"));
        QueryResult result1 = db.runSelectStatement(queryCheckReview, this.customerId, mealId);
        int firstValue = (int) result1.rows.getFirst().getFirst();
        if (firstValue > 0) {
            System.out.println("You have already written a review for this meal.");
            return;
        }

        String queryCheckMeal = Files.readString(Paths.get("src/sql_queries/customer/reviews/checkMeal.sql"));
        QueryResult result2 = db.runSelectStatement(queryCheckMeal, this.customerId, mealId);
        int firstValue2 = (int) result2.rows.getFirst().getFirst();
        if (firstValue2 == 0) {
            System.out.println("You have not ordered this meal yet.");
            return;
        }

        String queryWriteReview = Files.readString(Paths.get("src/sql_queries/customer/reviews/create.sql"));
        db.runStatement(queryWriteReview, this.customerId, mealId, review, rating);
    }

    public void viewMyInformation() throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/user/info.sql"));
        QueryResult result = db.runSelectStatement(query, this.customerId);
        Formatting.printTable(result);
    }
}
