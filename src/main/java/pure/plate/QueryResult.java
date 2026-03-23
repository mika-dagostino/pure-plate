package pure.plate;
import java.util.ArrayList;

public class QueryResult {
    public ArrayList<String> columns;
    public ArrayList<ArrayList<Object>> rows;

    public QueryResult(ArrayList<String> columns, ArrayList<ArrayList<Object>> rows) {
        this.columns = columns;
        this.rows = rows;
    }
}
