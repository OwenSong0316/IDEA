public class Point {

    public int x;
    public int y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void exchange() {
        int t = x;
        x = y;
        y = t;
    }

    public void reverse() {
        x = -x;
        y = -y;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
