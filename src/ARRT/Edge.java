package ARRT;

import PPACO.Point;

import java.util.Objects;

public class Edge {

    Point currPoint;
    Point prePoint;

    public Edge(Point cp, Point pp) {
        currPoint = cp;
        prePoint = pp;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "currPoint=" + currPoint +
                ", prePoint=" + prePoint +
                '}';
    }
}
