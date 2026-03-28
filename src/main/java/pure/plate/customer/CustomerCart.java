package pure.plate.customer;

import pure.plate.*;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomerCart {
    private final DBConnect db;

    public CustomerCart(DBConnect db) {
        this.db = db;
    }

    protected QueryResult viewCart(int customerId, boolean doesPrint) throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/cart/view.sql"));
        QueryResult result = db.runSelectStatement(query, customerId);

        if (doesPrint) {
            Utils.printWithSeparators("Below are the items in your cart:");
            Formatting.printTable(result);
        }

        return result;
    }

    private void updateCartTotal(int customerId) throws Exception {
        String query = Files.readString(Paths.get("src/sql_queries/customer/cart/updateTotal.sql"));
        db.runStatement(query, customerId);
    }
    
    protected void orderCart(int customerId) throws Exception {
        QueryResult result = viewCart(customerId, false);
        int numberOfRows = result.rows.size();

        if (numberOfRows == 0) {
            Utils.printWithSeparators("Your cart is empty, please add items to order");
            return;
        }

        String creditCardNumber = Validator.getString("Enter your credit card number: ", "credit card number", 12, 12);
        if (creditCardNumber.equals("esc")) return;

        int paymentId = Utils.generateId();
        int orderId = Utils.generateId();

        String queryStorePaymentInformation = Files.readString(Paths.get("src/sql_queries/customer/orders/payment.sql"));
        db.runStatement(queryStorePaymentInformation, paymentId, creditCardNumber, customerId);

        String queryCreateOrder = Files.readString(Paths.get("src/sql_queries/customer/orders/create.sql"));
        db.runStatement(queryCreateOrder, orderId, paymentId, customerId, "preparing", customerId);

        String queryStoreMealItems = Files.readString(Paths.get("src/sql_queries/customer/orders/createItems.sql"));
        db.runStatement(queryStoreMealItems, orderId, customerId);

        String queryRemoveItemsFromCart = Files.readString(Paths.get("src/sql_queries/customer/cart/empty.sql"));
        db.runStatement(queryRemoveItemsFromCart, customerId);
        updateCartTotal(customerId);

        Utils.printWithSeparators("Order placed successfully.");
    }

    protected void deleteItemFromCart(int customerId) throws Exception {
        int itemId = Validator.getInt("Enter the ID of the meal you want to delete: ", "meal ID", 1, Utils.nbrOfMeals);
        if (itemId == -1) return;

        String query = Files.readString(Paths.get("src/sql_queries/customer/cart/removeMeal.sql"));
        db.runStatement(query, customerId, itemId);
        updateCartTotal(customerId);
    }

    protected void changeQuantityOfCartItem(int customerId) throws Exception {
        int itemId = Validator.getInt("Enter the ID of the meal you want to change the quantity of: ", "meal ID", 1, Utils.nbrOfMeals);
        int newQuantity = Validator.getInt("Enter the new quantity: ", "new quantity", 1, 10);

        if (newQuantity == -1 || itemId == -1) return;

        String query = Files.readString(Paths.get("src/sql_queries/customer/cart/updateQuantity.sql"));
        db.runStatement(query, newQuantity, customerId, itemId);
        updateCartTotal(customerId);
    }

    protected void addItemToCart(int customerId) throws Exception {
        int mealId = Validator.getInt("Enter the ID of the meal you want to add to the cart: ", "meal ID", 1, Utils.nbrOfMeals);
        int quantity = Validator.getInt("Enter the quantity you want to add: ", "quantity", 1, 10);

        if (quantity == -1 || mealId == -1) return;

        String query = Files.readString(Paths.get("src/sql_queries/customer/cart/addMeal.sql"));
        db.runStatement(query, customerId, mealId, quantity);
        updateCartTotal(customerId);
    }
}
