package basePSO;

public class Robot {

    public Point startPoint;
    public Point endPoint;
    public Point startVector;
    public Point endVector;

    Robot(Point sp, Point ep, Point sv, Point ev) {
        startPoint = sp;
        endPoint = ep;
        startVector = sv;
        endVector = ev;
    }

}
