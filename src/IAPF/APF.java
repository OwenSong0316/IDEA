package IAPF;

import PPACO.Line;
import PPACO.Point;
import PPACO.Utils;

import java.util.ArrayList;
import java.util.List;

public class APF {

    Obstacle[] obstacles;
    Point startPoint;
    Point endPoint;

    double rs;
    double rp;
    double step;

    List<Point> paths;
    Point currPoint;

    double fit;

    APF(Obstacle[] obs, Point sp, Point ep, double rs, double rp) {
        obstacles = obs;
        startPoint = sp;
        endPoint = ep;

        this.rs = rs;
        this.rp = rp;
        step = 0.1;

        paths = new ArrayList<>();
        paths.add(sp);
        currPoint = startPoint;

        fit = 0;
    }

    public void runAPF() {
        step2(endPoint);
        calFit();
    }

    private void calFit() {
        StringBuilder sbx = new StringBuilder();
        StringBuilder sby = new StringBuilder();
        Point pre = startPoint;
        for (Point curr : paths) {
            fit += Utils.calDistance(pre.x, pre.y, curr.x, curr.y);
            pre = curr;
            sbx.append(curr.x);
            sbx.append(',');
            sby.append(curr.y);
            sby.append(',');
        }
        System.out.println("path3 = [[" + sbx + "],[" + sby + "]]");
        System.out.println(fit);
    }

    private void step2(Point nextPoint) {
        Line line = new Line(currPoint, nextPoint);
        int minObstacle = 0, minPoint = 0;
        double minDis = rs, dis;
        Obstacle obstacle;
        Point point;
        for (int i = 0; i < obstacles.length; i++) {
            obstacle = obstacles[i];
            for (int j = 0; j < obstacle.points.length; j++) {
                point = obstacle.points[j];
                if ((dis = Utils.calPointDisWithLine(point, line) - obstacle.r) < minDis) {
                    if (isDropPointInLine(point, line)) {
                        minDis = dis;
                        minObstacle = i;
                        minPoint = j;
                    }
                }
            }
        }

        // step 3
        if (minDis < rs) {
            obstacle = obstacles[minObstacle];
            point = obstacle.points[minPoint];
            if ((dis = Utils.calDistance(currPoint.x, currPoint.y, point.x, point.y) - obstacle.r - rp) > 0) {
                dis = dis / Utils.calDistance(currPoint.x, currPoint.y, endPoint.x, endPoint.y);
                point = new Point(currPoint.x + (endPoint.x - currPoint.x) * dis,
                        currPoint.y + (endPoint.y - currPoint.y) * dis);
                step2(point);
            }
            try {
                step5(obstacle, obstacle.points[minPoint], nextPoint);
            } catch (StackOverflowError error) {
                return;
            }
        } else {
            paths.add(nextPoint);
            currPoint = nextPoint;
        }
    }

    private void step5(Obstacle obstacle, Point point, Point nextPoint) {
//        calFit();
        Line line = new Line(currPoint, point);
        List<Integer> part1 = new ArrayList<>();
        List<Integer> part2 = new ArrayList<>();
        int p;
        for (int i = 0; i < obstacle.points.length; i++) {
            if ((p = Utils.calPointPosWithSegment(obstacle.points[i], line)) == 0) {
                part1.add(i);
                part2.add(i);
            } else if (p == 1) {
                part1.add(i);
            } else {
                part2.add(i);
            }
        }

        List<Integer> part = part1;
        if (part1.size() < part2.size()) {
            p = 1;
        } else if (part2.size() < part1.size()) {
            part = part2;
            p = -1;
        } else {
            p = 0;
        }

        int degree = calDegree(point);
        double distance = Utils.calDistance(currPoint.x, currPoint.y, point.x, point.y);
        int add = 1;
        Point tp = calPoint(degree + add, distance);
        if (p != 0) {
            if (Utils.calPointPosWithSegment(tp, line) != p) {
                add = -1;
            }
        } else {
            Line tl = new Line(currPoint, nextPoint);
            double td = Utils.calPointDisWithLine(tp, tl);
            tp = calPoint(degree - add, distance);
            if (td > Utils.calPointDisWithLine(tp, tl)) {
                add = -1;
            }
        }

        boolean state1 = true, state2;
        while (state1) {
            degree += add;
            tp = calPoint(degree, distance);
            line = new Line(currPoint, tp);
            state2 = true;
            for (int i = 0; i < part.size(); i++) {
                if ((Utils.calPointDisWithLine(obstacle.points[part.get(i)], line) - obstacle.r) < rs) {
                    state2 = false;
                    break;
                }
            }
            if (state2) {
                state1 = false;
            }
        }

        step2(tp);
        step2(nextPoint);
    }

    private boolean isDropPointInLine(Point point, Line line) {
        Point dropPoint = calDropPoint(point, line);
        if ((dropPoint.x > line.a.x && dropPoint.x > line.b.x) ||
                (dropPoint.x < line.a.x && dropPoint.x < line.b.x)) {
            return false;
        }
        if ((dropPoint.y > line.a.y && dropPoint.y > line.b.y) ||
                (dropPoint.y < line.a.y && dropPoint.y < line.b.y)) {
            return false;
        }
        return true;
    }

    private Point calDropPoint(Point point, Line line) {
        double a = line.b.y - line.a.y;
        double b = line.a.x - line.b.x;
        double c = line.b.x * line.a.y - line.a.x * line.b.y;
        double t = a * a + b * b;
        return new Point((b * b * point.x - a * b * point.y - a * c) / t,
                (a * a * point.y - a * b * point.x - b * c) / t);
    }

    private Point calPoint(int degree, double distance) {
        double d = Math.toRadians(degree);
        return new Point(currPoint.x + distance * Math.cos(d), currPoint.y + distance * Math.sin(d));
    }

    private int calDegree(Point point) {
        if (point.x == currPoint.x) {
            if (point.y > currPoint.y) {
                return 90;
            } else {
                return 270;
            }
        }
        double k = (point.y - currPoint.y) / (point.x - currPoint.x);
        int degree = (int)Math.toDegrees(Math.atan(k));
        if (k > 0) {
            if (point.x > currPoint.x) {
                return degree;
            } else {
                return 180 + degree;
            }
        } else {
            if (point.x > currPoint.x) {
                return 360 + degree;
            } else {
                return 180 + degree;
            }
        }
    }

}
