package pure.plate.customer;

import pure.plate.DBConnect;
import pure.plate.QueryResult;
import pure.plate.Utils;
import pure.plate.Validator;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CustomerAuth {
    private final DBConnect db;

    public CustomerAuth(DBConnect db) {
        this.db = db;
    }

    public int login() {
        try {
            int customerId = Validator.getUserID("Enter customer ID: ");
            String password = Validator.getString("Enter password: ", "password", 6, 512);

            if (customerId == -1 || password.equals("esc")) return -1;

            String query = Files.readString(Paths.get("src/sql_queries/customer/auth/login.sql"));
            QueryResult result = db.runSelectStatement(query, customerId, password);

            if (result.rows.getFirst().getFirst().equals(1)) {
                System.out.println("\nLogin successful for user: " + customerId + "\n");
                return customerId;
            } else {
                System.out.println("\n---- Invalid customer ID or password ----\n");
                return -1;
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            return -1;
        }
    }

    public int register() {
        try {
            String name = Validator.getString("What's your name? ", "name", 3, 100);
            String email = Validator.getEmail("What's your email? ");
            String password = Validator.getString("Create a password: ", "password", 6, 512);
            String address = Validator.getString("What's your address? ", "address", 3, 300);
            String phoneNumber = Validator.getString("What's your phone number? ", "phone number", 9, 20);

            if (name.equals("esc") || email.equals("esc") || password.equals("esc") || address.equals("esc") || phoneNumber.equals("esc")) return -1;

            int userId = Utils.generateId();
            String registerUser = Files.readString(Paths.get("src/sql_queries/register_user.sql"));
            db.runStatement(registerUser, userId, address, email, phoneNumber, name, password);

            String createUser = Files.readString(Paths.get("src/sql_queries/customer/auth/register.sql"));
            db.runStatement(createUser, userId);

            int cartId = Utils.generateId();
            String createCart = Files.readString(Paths.get("src/sql_queries/customer/cart/create.sql"));
            db.runStatement(createCart, cartId, userId);

            Utils.printWithSeparators("YOUR USER ID IS: " + userId);

            return userId;
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
            return -1;
        }
    }
}
