package pure.plate;

public class Validator {
    public static int getUserID(String prompt) {
        boolean isValidField = false;
        int userId = -1;

        while (!isValidField) {
            int enteredUserId = Input.getInt(prompt);
            if ((enteredUserId > 0 && enteredUserId < 999999999) || enteredUserId == -1) {
                isValidField = true;
                userId = enteredUserId;
            } else {
                System.out.println("\nYour User ID must be between 0 and 999,999,999\n");
            }
        }

        return userId;
    }

    public static int getInt(String prompt, String name, int minValue, int maxValue) {
        boolean isValidField = false;
        int number = -1;

        while (!isValidField) {
            int enteredNumber = Input.getInt(prompt);
            if ((enteredNumber >= minValue && enteredNumber <= maxValue) || enteredNumber == -1) {
                isValidField = true;
                number = enteredNumber;
            } else {
                System.out.println("\nThe " + name +  " must be between " + minValue + " and " + maxValue + "\n");
            }
        }

        return number;
    }

    public static String getString(String prompt, String name, int minLength, int maxLength) {
        boolean isValidField = false;
        String txt = "";

        while (!isValidField) {
            String enteredTxt = Input.getString(prompt);
            int txtLength = enteredTxt.length();
            if ((txtLength >= minLength && txtLength <= maxLength) || enteredTxt.equals("esc")) {
                isValidField = true;
                txt = enteredTxt;
            } else {
                if (minLength == maxLength) System.out.println("\nThe " + name +  " must be " + maxLength + " characters long\n");
                else System.out.println("\nThe " + name +  " must be between " + minLength + " and " + maxLength + " characters long\n");            }
        }

        return txt;
    }

    public static String getEmail(String prompt) {
        boolean isValidField = false;
        String txt = "";

        while (!isValidField) {
            String enteredTxt = Input.getString(prompt);
            int txtLength = enteredTxt.length();
            if ((txtLength >= 5 && enteredTxt.contains(".") && enteredTxt.contains("@")) || enteredTxt.equals("esc")) {
                isValidField = true;
                txt = enteredTxt;
            } else {
                System.out.println("\nYour email is invalid, please try again\n");
            }
        }

        return txt;
    }
}
