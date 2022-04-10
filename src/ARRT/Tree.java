package ARRT;

import PPACO.Point;

import java.util.ArrayList;
import java.util.List;

public class Tree {

    List<Edge> edges;
    Point currPoint;

    double leftBorder;
    double rightBorder;
    double upBorder;
    double bottomBorder;

    public Tree(Point point) {
        currPoint = point;
        edges = new ArrayList<>();
        edges.add(new Edge(currPoint, currPoint));

        leftBorder = rightBorder = point.x;
        upBorder = bottomBorder = point.y;
    }

    @Override
    public String toString() {
        return "Tree{" +
                "edges=" + edges +
                ", currPoint=" + currPoint +
                '}';
    }
}
