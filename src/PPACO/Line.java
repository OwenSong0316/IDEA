package PPACO;

import java.util.Objects;

public class Line {

    public Point a;
    public Point b;

    public Point u;
    public Point v;

    public Line(Point a, Point b) {
        this(a, b, a, b);
    }

    Line(Point a, Point b, Point u, Point v) {
        this.a = a;
        this.b = b;

        this.u = u;
        this.v = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return (Objects.equals(u, line.u) && Objects.equals(v, line.v)) ||
                (Objects.equals(u, line.v) && Objects.equals(v, line.u));
    }

    @Override
    public int hashCode() {
        return Objects.hash(u, v);
    }

    @Override
    public String toString() {
        return "Line{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

}
