package pure.plate.Customer;

public class Customer_Registration {
    
    public static void register() {
        try {
            String name = pure.plate.Input.getString("Enter your name: ");
            String email = pure.plate.Input.getString("Enter your email: ");
            String password = pure.plate.Input.getString("Enter your password: ");
            // Here you can add code to save the customer's information to a database or file
            System.out.println(("Registering: " + name + " " + email + " " + password));
            System.out.println("Customer registered successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
        }
    }
    
}
