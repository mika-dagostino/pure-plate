package pure.plate;

public class Utils {
    public static final int nbrOfMeals = 104;

    public static void printMenu(String... options) {
        System.out.println("\n================================================");
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ": " + options[i]);
        }
        System.out.println("================================================\n");
    }

    public static void printMenuWithTitle(String title, String... options) {
        System.out.println("\n================================================");
        System.out.println(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ": " + options[i]);
        }
        System.out.println("================================================\n");
    }

    public static void printWithSeparators(String text) {
        System.out.println("\n================================================");
        System.out.println(text);
        System.out.println("================================================\n");
    }

    public static int generateId() {
        long timestamp = System.currentTimeMillis() % 1000000000;
        return (int) timestamp;
    }
}
