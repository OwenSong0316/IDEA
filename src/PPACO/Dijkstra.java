package PPACO;

import java.util.ArrayList;
import java.util.List;

public class Dijkstra {

    Network network;

    int startPoint;
    int endPoint;
    int pointNum;
    Point[] optionalPoints;
    double[][] distances;
    List<Integer>[] passableNet;
    boolean[] used;

    Dijkstra(Network network) {
        this.network = network;

        pointNum = network.networkNodes.size();
        optionalPoints = new Point[pointNum];
        optionalPoints = network.networkNodes.toArray(optionalPoints);
        startPoint = -1;
        endPoint = -1;
        distances = new double[pointNum][pointNum];
        passableNet = new List[pointNum];
        used = new boolean[pointNum];

        initDijkstra();
    }

    private void initDijkstra() {
        for (int i = 0; i < pointNum; i++) {
            if (startPoint == -1 && network.startPoint == optionalPoints[i]) {
                startPoint = i;
            }
            if (endPoint == -1 && network.endPoint == optionalPoints[i]) {
                endPoint = i;
            }
            passableNet[i] = new ArrayList<>();
            distances[i][i] = 0.1;
            for (int j = i + 1; j < pointNum; j++) {
                distances[j][i] = distances[i][j] =
                        Math.sqrt(Math.pow(optionalPoints[i].x - optionalPoints[j].x, 2) +
                                Math.pow(optionalPoints[i].y - optionalPoints[j].y, 2));
            }
        }

        for (int i = 0; i < pointNum; i++) {
            for (int j = i + 1; j < pointNum; j++) {
                if (network.detectCollision(new Line(optionalPoints[i], optionalPoints[j])) < 0) {
                    passableNet[i].add(j);
                    passableNet[j].add(i);
                }
            }
        }
    }

    public void runDijkstra() {
        Edge[] selected = new Edge[pointNum];
        Edge[] optionals = new Edge[pointNum];
        int currPoint = startPoint;
        selected[currPoint] = new Edge(currPoint, currPoint, 0);
        int count = 0, t;
        double min = 0, temp;
        while (count < pointNum) {
            count++;
            for (int i = 0; i < passableNet[currPoint].size(); i++) {
                if (!used[t = passableNet[currPoint].get(i)]) {
                    temp = min + distances[currPoint][t];
                    if (optionals[t] == null) {
                        optionals[t] = new Edge(t, currPoint, temp);
                    } else if (temp < optionals[t].length) {
                        optionals[t].length = temp;
                        optionals[t].pre = currPoint;
                    }
                }
            }

            temp = Double.MAX_VALUE;
            t = -1;
            for (int i = 0; i < pointNum; i++) {
                if (!used[i]) {
                    if (optionals[i] != null && optionals[i].length < temp) {
                        t = i;
                        temp = optionals[i].length;
                    }
                }
            }
            selected[t] = optionals[t];
            if (t == endPoint) {
                break;
            }
            min = temp;
            used[t] = true;
            currPoint = t;
        }
        calAndPrint(selected);
    }

    private void calAndPrint(Edge[] selected) {
        StringBuilder sbx = new StringBuilder();
        StringBuilder sby = new StringBuilder();
        print(selected, endPoint, sbx, sby);
        System.out.println();
        System.out.println("最佳路径总距离： " + selected[endPoint].length);
        System.out.println("path1 = [[" + sbx + "],[" + sby + "]]");
//        int count = 0;
//        for (int i = 0; i < passableNet.length; i++) {
//            count += passableNet[i].size();
//        }
//        System.out.println("边数: " + (count / 2));
    }

    private void print(Edge[] selected, int currPoint, StringBuilder stringBuilderX, StringBuilder stringBuilderY) {
        if (currPoint == startPoint) {
            stringBuilderX.append(optionalPoints[currPoint].x);
            stringBuilderX.append(',');
            stringBuilderY.append(optionalPoints[currPoint].y);
            stringBuilderY.append(',');
//            System.out.print(optionalPoints[currPoint]);
            return;
        }
        print(selected, selected[currPoint].pre, stringBuilderX, stringBuilderY);
        stringBuilderX.append(optionalPoints[currPoint].x);
        stringBuilderX.append(',');
        stringBuilderY.append(optionalPoints[currPoint].y);
        stringBuilderY.append(',');
//        System.out.print(" " + optionalPoints[currPoint]);
    }

}
