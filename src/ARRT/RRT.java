package ARRT;

import PPACO.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RRT {

    private static final int Trapped = 0;
    private static final int Advanced = 1;
    private static final int Reached = 2;

    Obstacle[] obstacles;
    Point startPoint;
    Point endPoint;

    double leftBorder;
    double rightBorder;
    double bottomBorder;
    double upBorder;
    int k;
    double pGoal;
    double pOutside;
    double desc;
    double step;
    double firstStep;
    double secondStep;
    double delta;
    Tree ta, tb;
    Point newPoint;
    Network network;
    double[] xFirstStep;
    double[] yFirstStep;
    double[] xSecondStep;
    double[] ySecondStep;
    int severalTimes;
    int threshold;
    int failure;

    public List<Point> paths;
    public double pathLength;

    public RRT(Obstacle[] obs, Point sp, Point ep, double[] border) {
        obstacles = obs;
        startPoint = sp;
        endPoint = ep;

        leftBorder = border[0];
        rightBorder = border[1];
        bottomBorder = border[2];
        upBorder = border[3];
        k = 25000;
        pGoal = 0.01;
        pOutside = 0.95;
        desc = (pOutside - 0.2) / k;
        step = 0.5;
        firstStep = 1.5 * step;
        secondStep = 0.75 * step;
        delta = 0.9 * step;
        xFirstStep = new double[]{firstStep, -firstStep, 0, 0};
        yFirstStep = new double[]{0, 0, firstStep, -firstStep};
        xSecondStep = new double[]{secondStep, -secondStep, 0, 0};
        ySecondStep = new double[]{0, 0, secondStep, -secondStep};
        ta = new Tree(startPoint);
        tb = new Tree(endPoint);
        severalTimes = 5;
        threshold = 5;
        failure = 0;

        paths = new ArrayList<>();
        pathLength = 0;

        network = new Network();
    }

    public void runRRT() {
        for (int i = 0; i < k; i++) {
            Point randPoint = randomConfig(ta, tb.currPoint);
            if (Trapped == extend(ta, randPoint)) {
                if (Reached == connect(tb, ta.currPoint)) {
                    path();
                    return;
                }
            }
            swap();
        }
        pathLength = -1;
        System.out.println("failed");
    }

    private Point randomConfig(Tree t, Point goalPoint) {
        Random random = new Random();
        Point rand;

        if (leftBorder == t.leftBorder && rightBorder == t.rightBorder
                && upBorder == t.upBorder && bottomBorder == t.bottomBorder) {
            rand = new Point(random.nextDouble() * (rightBorder - leftBorder) + leftBorder,
                    random.nextDouble() * (upBorder - bottomBorder) + bottomBorder);
        } else {
            double p = random.nextDouble();
            if (p <= pGoal) {
                rand = goalPoint;
            } else {
                if (p >= pOutside) {
                    rand = new Point(random.nextDouble() * (t.rightBorder - t.leftBorder) + t.leftBorder,
                            random.nextDouble() * (t.upBorder - t.bottomBorder) + t.bottomBorder);
                } else {
                    double[] ur = {(rightBorder - t.rightBorder) + (t.leftBorder - leftBorder),
                            (upBorder - t.upBorder) + (t.bottomBorder - bottomBorder)};
                    int dMax = ur[1] > ur[0] ? 1 : 0, dim;
                    double pMax = ur[dMax] / (ur[0] + ur[1]);
                    if (random.nextDouble() <= pMax) {
                        dim = dMax;
                    } else {
                        dim = 1 - dMax;
                    }
                    if (dim == 0) {
                        if (rightBorder - t.rightBorder > t.leftBorder - leftBorder) {
                            rand = new Point(random.nextDouble() * (rightBorder - t.rightBorder) + t.rightBorder,
                                    random.nextDouble() * (upBorder - bottomBorder) + bottomBorder);
                        } else {
                            rand = new Point(random.nextDouble() * (t.leftBorder - leftBorder) + leftBorder,
                                    random.nextDouble() * (upBorder - bottomBorder) + bottomBorder);
                        }
                    } else {
                        if (upBorder - t.upBorder > t.bottomBorder - bottomBorder) {
                            rand = new Point(random.nextDouble() * (rightBorder - leftBorder) + leftBorder,
                                    random.nextDouble() * (upBorder - t.upBorder) + t.upBorder);
                        } else {
                            rand = new Point(random.nextDouble() * (rightBorder - leftBorder) + leftBorder,
                                    random.nextDouble() * (t.bottomBorder - bottomBorder) + bottomBorder);
                        }
                    }
                }
            }
            updatePOutside();
        }

        return rand;
    }

    private int extend(Tree t, Point randPoint) {
        int index;

        Point nearPoint = nearestNeighbor(t, randPoint);
        if ((index = newConfig(nearPoint, randPoint)) < 0) {
            addVertexAndEdge(t, nearPoint);
            if (newPoint.equals(randPoint)) {
                return Reached;
            } else {
                return Advanced;
            }
        } else {
            Point[] points = localSampling(nearPoint);
            List<Point>[] obstacleAndFree = dividedBasedOnFree(points, obstacles[index]);
            Point obsAve = mean(obstacleAndFree[0]);
            if (network.isPointInObstacle(obsAve, obstacles[index])) {
                Point[] obsPoints = furthestPointPair(obstacleAndFree[0]);
                expandAlongLine(t, nearPoint, obsPoints, false);
            } else {
                if (Utils.calDistance(obsAve.x, obsAve.y, nearPoint.x, nearPoint.y) >= delta) {
                    newPoint = obsAve;
                    if (isPointInMap() && detectCollision(nearPoint) < 0) {
                        addVertexAndEdge(t, nearPoint);
                    }
                } else {
                    Point[] freePoints = furthestPointPair(obstacleAndFree[1]);
                    expandAlongLine(t, nearPoint, freePoints, true);
                }
            }
            return Trapped;
        }
    }

    private int connect(Tree t, Point newPoint) {
        int s;

        do {
            s = extend(t, newPoint);
        } while (s == Advanced);

        return s;
    }

    private void path() {
        Point pre = ta.currPoint;
        do {
            paths.add(pre);
            Point tempCurr = pre;
            pre = ta.edges.stream()
                    .filter(item -> tempCurr.equals(item.currPoint))
                    .collect(Collectors.toList())
                    .get(0).prePoint;
            if (tempCurr == pre) {
                break;
            }
            pathLength += Utils.calDistance(tempCurr.x, tempCurr.y, pre.x, pre.y);
        } while (true);
        pre = tb.currPoint;
        do {
            Point tempCurr = pre;
            pre = tb.edges.stream()
                    .filter(item -> tempCurr.equals(item.currPoint))
                    .collect(Collectors.toList())
                    .get(0).prePoint;
            paths.add(0, tempCurr);
            pathLength += Utils.calDistance(tempCurr.x, tempCurr.y, pre.x, pre.y);
            if (tempCurr == pre) {
                break;
            }
        } while (true);
//        System.out.println(paths);
//        System.out.println(pathLength);
    }

    private void swap() {
        if (ta.edges.size() < tb.edges.size()) {
            Tree temp = ta;
            ta = tb;
            tb = temp;
        } else {
            if (++failure >= threshold) {
                if (density(ta) > density(tb)) {
                    extend(tb, randomConfig(tb, ta.currPoint));
                } else {
                    extend(ta, randomConfig(ta, tb.currPoint));
                }
                Tree temp = ta;
                ta = tb;
                tb = temp;
                failure = 0;
            }
        }
    }

    private void updatePOutside() {
        pOutside -= desc;
    }

    private Point nearestNeighbor(Tree t, Point randPoint) {
        Point point = t.edges.get(0).currPoint;
        double temp;
        double min = Utils.calDistance(randPoint.x, randPoint.y, point.x, point.y);
        Point nearPoint = point;
        for (int i = 0; i < t.edges.size(); i++) {
            point = t.edges.get(i).currPoint;
            temp = Utils.calDistance(randPoint.x, randPoint.y, point.x, point.y);
            if (temp < min) {
                min = temp;
                nearPoint = point;
            }
        }
        return nearPoint;
    }

    private int newConfig(Point nearPoint, Point randPoint) {
        double dis;
        if ((dis = Utils.calDistance(nearPoint.x, nearPoint.y, randPoint.x, randPoint.y)) <= step) {
            newPoint = randPoint;
        } else {
            dis = step / dis;
            newPoint = new Point(nearPoint.x + (randPoint.x - nearPoint.x) * dis,
                    nearPoint.y + (randPoint.y - nearPoint.y) * dis);
        }

        return detectCollision(nearPoint);
    }

    private Point[] localSampling(Point point) {
        Point[] points = new Point[xSecondStep.length * xFirstStep.length];
        int index = 0;
        for (int i = 0; i < xFirstStep.length; i++) {
            for (int j = 0; j < xSecondStep.length; j++) {
                points[index++] = new Point(point.x + xFirstStep[i] + xSecondStep[j],
                        point.y + yFirstStep[i] + ySecondStep[j]);
            }
        }
        return points;
    }

    private List<Point>[] dividedBasedOnFree(Point[] points, Obstacle obstacle) {
        List<Point>[] obstacleAndFree = new ArrayList[2];
        obstacleAndFree[0] = new ArrayList<>();
        obstacleAndFree[1] = new ArrayList<>();
        for (Point point : points) {
            if (network.isPointInObstacle(point, obstacle)) {
                obstacleAndFree[0].add(point);
            } else {
                obstacleAndFree[1].add(point);
            }
        }
        return obstacleAndFree;
    }

    private Point mean(List<Point> obstacleSet) {
        int size = obstacleSet.size();
        double x = 0, y = 0;
        for (Point point : obstacleSet) {
            x += point.x;
            y += point.y;
        }
        return new Point(x / size, y / size);
    }

    private Point[] furthestPointPair(List<Point> points) {
        double max = 0, dis;
        Point p1, p2, mp1 = null, mp2 = null;
        for (int i = 0; i < points.size(); i++) {
            p1 = points.get(i);
            for (int j = i + 1; j < points.size(); j++) {
                p2 = points.get(j);
                if ((dis = Utils.calDistance(p1.x, p1.y, p2.x, p2.y)) > max) {
                    max = dis;
                    mp1 = p1;
                    mp2 = p2;
                }
            }
        }
        return new Point[]{mp1, mp2};
    }

    private void expandAlongLine(Tree t, Point point, Point[] line, boolean state) {
        if (line[0] == null) {
            return;
        }
        double dis = Utils.calDistance(line[0].x, line[0].y, line[1].x, line[1].y);
        dis = step / dis;
        double x = (line[1].x - line[0].x) * dis, y = (line[1].y - line[0].y) * dis;
        if (isAcuteAngle(t.edges, point, x, y)) {
            x = -x;
            y = -y;
        }
        newPoint = new Point(point.x + x, point.y + y);
        if (isPointInMap() && detectCollision(point) < 0) {
            addVertexAndEdge(t, point);
            if (state) {
                Point temp = newPoint;
                for (int i = 1; i < severalTimes; i++) {
                    newPoint = new Point(temp.x + x, temp.y + y);
                    if (isPointInMap() && detectCollision(temp) < 0) {
                        addVertexAndEdge(t, temp);
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void addVertexAndEdge(Tree t, Point prePoint) {
        t.edges.add(new Edge(newPoint, prePoint));
        t.currPoint = newPoint;
        if (newPoint.x > rightBorder) {
            rightBorder = newPoint.x;
        } else if (newPoint.x < leftBorder) {
            leftBorder = newPoint.x;
        }
        if (newPoint.y > upBorder) {
            upBorder = newPoint.y;
        } else if (newPoint.y < bottomBorder) {
            bottomBorder = newPoint.y;
        }
    }

    private boolean isAcuteAngle(List<Edge> edges, Point point, double x, double y) {
        List<Edge> edgeList = edges.stream()
                .filter(item -> point.equals(item.currPoint))
                .collect(Collectors.toList());
        Point prePoint = edgeList.get(0).prePoint;
        return (point.y - prePoint.y) * y + (point.x - prePoint.x) * x < 0;
    }

    private int detectCollision(Point prePoint) {
        Line line = new Line(prePoint, newPoint);
        for (int i = 0; i < obstacles.length; i++) {
            if (network.detectLineObstacle(line, obstacles[i])) {
                return i;
            }
            if (network.isPointInObstacle(newPoint, obstacles[i])) {
                return i;
            }
        }
        return -1;
    }

    private double density(Tree t) {
        double area = (t.rightBorder - t.leftBorder) * (t.upBorder - t.bottomBorder);
        return area == 0 ? 0 : t.edges.size() / area;
    }

    private boolean isPointInMap() {
        return isPointOutMap(newPoint);
    }

    private boolean isPointOutMap(Point point) {
        return point.x > rightBorder || point.x < leftBorder
                || point.y > upBorder || point.y < bottomBorder;
    }

}
