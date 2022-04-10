package IAPF;

import PPACO.Point;

import java.util.List;

public class Obstacle {

    public Point[] points;
    public double r;

    Obstacle(double[] data, double r) {
        if (data.length % 2 != 0) {
            System.out.println("奇数个数据！！！waring！！！");
        }
        points = new Point[data.length / 2];
        int index = 0;
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(data[index++], data[index++]);
        }

        this.r = r;
    }

    @Override
    public String toString() {
        StringBuilder sbx = new StringBuilder();
        StringBuilder sby = new StringBuilder();
        for (Point point : points) {
            sbx.append(point.x);
            sbx.append(',');
            sby.append(point.y);
            sby.append(',');
        }
        return "[[" + sbx + "],[" + sby + "]]";
    }
}
