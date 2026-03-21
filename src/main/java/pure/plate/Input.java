package pure.plate;

import java.util.Scanner;

public class Input {

    public static final Scanner sc = new Scanner(System.in);

    public static int getInt(String label) {
        System.out.print(label);
        return sc.nextInt();
    }
}
