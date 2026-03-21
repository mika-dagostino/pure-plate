package pure.plate;

import java.util.HashMap;

public class WelcomeMenu {
    private static String loggedInUser = null;
    private static String userType = null;

    public static int display() {
        System.out.println("Welcome to Pure Plate!");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Quit");

        return Input.getInt("Please select an option:");
    }

    public static void loginUser(String userId, String type) {
        loggedInUser = userId;
        userType = type;
    }

    public static void logoutUser()  {
        loggedInUser = null;
        userType = null;
    }

    public static HashMap<String, String> getCurrentUser() {
        if (loggedInUser == null || userType == null) return null;
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", loggedInUser);
        map.put("type", userType);

        return map;
    }
}
