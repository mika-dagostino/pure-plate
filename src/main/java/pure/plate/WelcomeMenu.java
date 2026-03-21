package pure.plate;

public class WelcomeMenu {
    public static int display() {
        System.out.println("Welcome to Pure Plate!");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Quit");

        return Input.getInt("Please select an option:");
    }
}
