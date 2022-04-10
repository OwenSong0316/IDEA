import java.util.LinkedList;
import java.util.List;

public class Line {

    public int value;
    public List<Point> obsList;

    Line(int v) {
        value = v;
        obsList = new LinkedList<>();
    }

    @Override
    public String toString() {
        return "Line{" +
                "value=" + value +
                ", obsList=" + obsList +
                '}';
    }
}
