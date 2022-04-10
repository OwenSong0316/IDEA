package PPACO;

import java.util.Arrays;

public class Obstacle {

    public Point[] obsPoints;

    public double leftBorder;
    public double rightBorder;
    public double upBorder;
    public double bottomBorder;
    public int index;

    public Obstacle(double[] data) {

        if (data.length % 2 != 0) {
            System.out.println("奇数个数据！！！waring！！！");
        }

        obsPoints = new Point[data.length / 2];
        int index = 0;
        for (int i = 0; i < obsPoints.length; i++) {
            obsPoints[i] = new Point(data[index++], data[index++]);
        }
        initBorder(); //给障碍物初始化一个边界;
    }

    private void initBorder() {
        Point temp = obsPoints[0];
        leftBorder = rightBorder = temp.x;
        upBorder = bottomBorder = temp.y;

        for (int i = 1; i < obsPoints.length; i++) {
            temp = obsPoints[i];
            if (temp.x > rightBorder){
                rightBorder = temp.x;
            } else if (temp. x < leftBorder) {
                leftBorder = temp.x;
            }
            if (temp.y > upBorder) {
                upBorder = temp.y;
            } else if (temp.y < bottomBorder) {
                bottomBorder = temp.y;
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder sbx = new StringBuilder();
        StringBuilder sby = new StringBuilder();
        for (Point point : obsPoints) {
            sbx.append(point.x);
            sbx.append(',');
            sby.append(point.y);
            sby.append(',');
        }
        return "[[" + sbx + "],[" + sby + "]]";
    }

}
