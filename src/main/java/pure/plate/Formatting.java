package pure.plate;
import java.util.ArrayList;

public class Formatting {
    
    public static void printTable(QueryResult result) {
        if (result.rows.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        ArrayList<String> headers = result.columns;
        ArrayList<ArrayList<Object>> data = result.rows;

        int cols = headers.size();
        int[] colWidths = new int[cols];

        // Compute max widths (headers + data)
        for (int i = 0; i < cols; i++) {
            colWidths[i] = headers.get(i).length();
        }

        for (ArrayList<Object> row : data) {
            for (int i = 0; i < cols; i++) {
                String val = row.get(i) == null ? "NULL" : row.get(i).toString();
                colWidths[i] = Math.max(colWidths[i], val.length());
            }
        }

        // Separator
        printSeparator(colWidths);

        // Print headers
        System.out.print("| ");
        for (int i = 0; i < cols; i++) {
            System.out.print(padRight(headers.get(i), colWidths[i]) + " | ");
        }
        System.out.println();

        printSeparator(colWidths);

        // Print rows
        for (ArrayList<Object> row : data) {
            System.out.print("| ");
            for (int i = 0; i < cols; i++) {
                String val = row.get(i) == null ? "NULL" : row.get(i).toString();
                System.out.print(padRight(val, colWidths[i]) + " | ");
            }
            System.out.println();
        }

        printSeparator(colWidths);
    }


    private static void printSeparator(int[] colWidths) {
        System.out.print("+");
        for (int width : colWidths) {
            System.out.print("-".repeat(width + 2) + "+");
        }
        System.out.println();
    }

    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
