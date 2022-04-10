package PPACO;

import java.util.Objects;

public class Point {

    public double x;
    public double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Point(Point a, Point b) {
        x = (a.x + b.x) / 2;
        y = (a.y + b.y) / 2;
    }

    Point(Point p, Line l) {
        if (p == null) return;

        if (l.a.x == l.b.x) {
            if (p.x < l.a.x) {
                x = p.x - Utils.SAFE_DISTANCE;
            } else {
                x = p.x + Utils.SAFE_DISTANCE;
            }
            y = p.y;
        } else if (l.a.y == l.b.y) {
            if (p.y < l.a.y) {
                y = p.y - Utils.SAFE_DISTANCE;
            } else {
                y = p.y + Utils.SAFE_DISTANCE;
            }
            x = p.x;
        } else {
            double k = (l.a.y - l.b.y) / (l.a.x - l.b.x);
            double ly = k * (p.x - l.a.x) + l.a.y;
            double dab = Utils.calDistance(l.a.x, l.a.y, l.b.x, l.b.y);
            if (k < 0) {
                if (p.y > ly) {
                    x = p.x + Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
                    y = p.y + Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
                } else {
                    x = p.x - Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
                    y = p.y - Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
                }
            } else {
                if (p.y > ly) {
                    x = p.x - Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
                    y = p.y + Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
                } else {
                    x = p.x + Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
                    y = p.y - Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
