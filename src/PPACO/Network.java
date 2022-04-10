package PPACO;

import basePSO.Util;

import java.util.*;

public class Network {

    private Obstacle[] obstacles;
    public Point startPoint;
    public Point endPoint;

    private Queue<Line> lines;  //开启容器，暂存判断的线段
    private List<Line>  doneLines; //已经判断的线段，避免重复判断
    public  Set<Point>  networkNodes; //暂存该环境下无碰撞的点集合

    public Network() {} //默认无参构造

    Network(Obstacle[] obs, Point sp, Point ep) { //将障碍物，起点终点输入进来
        obstacles = obs;
        startPoint = sp;
        endPoint = ep;

        networkNodes = new HashSet<>(); //暂存可行的点集，最后的输出结果

        lines = new LinkedList<>();  //保存中间变量，暂存需要判断有无碰撞的线段
        lines.offer(new Line(sp, ep)); //将初始判断线段放入temp

        doneLines = new ArrayList<>();//暂存已经判断过的线段，避免重复判断

        generateNetwork(); //产生网络
//        networkNodes.add(sp);
//        networkNodes.add(ep);
//        for (Obstacle o : obs) {
//            for (Point p : o.obsPoints) {
//                networkNodes.add(p);
//            }
//        }
    }

    private void generateNetwork(){
        Line temp;
        int index;
        while (!lines.isEmpty()) {
            if (doneLines.contains(temp = lines.poll())) { //该线是否已经被判断过
//                System.out.println("pass: " + temp);
                continue;
            }
            if ((index = detectCollision(temp)) < 0) {  //无碰撞，将线的两端点加入到待定序列中
//                System.out.println("insert:" + temp);
                networkNodes.add(temp.a);
                networkNodes.add(temp.b);
            } else { //发生碰撞
//                System.out.println("avoid:" + temp);
                avoidance(temp, obstacles[index]);
            }
            doneLines.add(temp);
        }
    }

    public int detectCollision(Line line) { //将当前直线与所有障碍物比对是否有碰撞
        for (int i = 0; i < obstacles.length; i++) {
            if (detectLineObstacle(line, obstacles[i])) { //有碰撞
                return i;
            }
        }
        return -1; //无碰撞
    }

    public boolean detectLineObstacle(Line line, Obstacle obstacle) {
        // 预筛、四条边（边界）; 为了提高效率,
        double min = Math.min(line.a.x, line.b.x);
        double max = Math.max(line.a.x, line.b.x);
        if (min > obstacle.rightBorder || max < obstacle.leftBorder) {
            return false;
        }
        min = Math.min(line.a.y, line.b.y);
        max = Math.max(line.a.y, line.b.y);
        if (min > obstacle.upBorder || max < obstacle.bottomBorder) {
            return false;
        }
        //真实判断碰撞
        int size = obstacle.obsPoints.length;
        Point pre = obstacle.obsPoints[size - 2], curr = obstacle.obsPoints[size - 1], next;
        int ai = -1, bi = -1;
        for (int i = 0; i < size; i++) {
            next = obstacle.obsPoints[i];
            if (ai == -1 && next.equals(line.a)) {
                ai = i;
            }
            if (bi == -1 && next.equals(line.b)) {
                bi = i;
            }
            if (detectLineLine(line, pre, curr, next)) {
                obstacle.index = Utils.preValue(i, size);
                return true;
            }
            pre = curr;
            curr = next;
        }

        if (ai != -1 && bi != -1 && Utils.nextValue(ai, size) != bi && Utils.preValue(ai, size) != bi) {
            return isPointInObstacle(new Point(line.a, line.b), obstacle);
        }

        return false;
    }

    public boolean isPointInObstacle(Point point, Obstacle obstacle) {
        int size = obstacle.obsPoints.length;
        Point pre = obstacle.obsPoints[size - 1], curr;
        int count = 0;
        double x;
        for (int i = 0; i < size; i++) {
            curr = obstacle.obsPoints[i];
            if (curr.y != pre.y) {
                if ((point.y <= curr.y && point.y >= pre.y) ||
                        (point.y <= pre.y && point.y >= curr.y)) {
                    x = (point.y - pre.y) * (curr.x - pre.x) / (curr.y - pre.y) + pre.x;
                    if (x > point.x) {
                        if (x == curr.x && curr.y > pre.y) {
                            count++;
                        } else if (x == pre.x && pre.y > curr.y) {
                            count++;
                        } else if ((x < (curr.x - Utils.E) && x > (pre.x + Utils.E)) ||
                                (x > (curr.x + Utils.E) && x < (pre.x - Utils.E))) {
                            count++;
                        }
                    }
                }
            }
            pre = curr;
        }

        return count % 2 == 1;
    }

    private boolean detectLineLine(Line line, Point pre, Point curr, Point next) {
        if (Utils.isPointOnSegment(curr, line)) {
            if (Utils.calPointPosWithSegment(pre, line) * Utils.calPointPosWithSegment(next, line) < 0) {
                return true;
            } else {
                return false;
            }
        }

        return Utils.isSegmentCrossSegment(curr, next, line);
    }

    private void avoidance(Line line, Obstacle obstacle) {
        List<TempObs>[] parts = preHandleBeforeAvoidance(line, obstacle);
        for (int i = 0; i < 2; i++) {
            avoidanceByOnePart(parts[i], i * 2 - 1, line, obstacle);
        }
    }

    private void avoidanceByOnePart(List<TempObs> part, int p, Line line, Obstacle obstacle) {
        Point best = null, curr;
        double max = 0, dis;
        int size = obstacle.obsPoints.length;
        TempObs tempObs;
        for (int i = 0; i < part.size(); i++) {
            tempObs = part.get(i);
            for (int j = tempObs.startIndex; j != tempObs.endIndex; j = Utils.nextValue(j, size)) {
                curr = obstacle.obsPoints[j];
                if (Utils.calPointPosWithSegment(curr, line) == p) {
                    dis = Utils.calPointDisWithLine(curr, line);
                    if (dis > max) {
                        max = dis;
                        best = curr;
                    }
                }
            }
        }

        Point point = new Point(best, line);
        Point inPoint = new Point(line.a, line.b);
        avoidanceByOnePartAgain(part, new Line(line.a, point, line.u, best), inPoint, obstacle);
        avoidanceByOnePartAgain(part, new Line(point, line.b, best, line.v), inPoint, obstacle);
    }

    private void avoidanceByOnePartAgain(List<TempObs> part, Line line, Point inPoint, Obstacle obstacle) {
        List<TempObs> temp = new ArrayList<>();
        TempObs tempObs;
        int size = obstacle.obsPoints.length;
        Point pre, curr, next;
        int si, ei;
        boolean isUse;
        int t, v;
        for (int i = 0; i < part.size(); i++) {
            tempObs = part.get(i);
            ei = si = tempObs.startIndex;
            pre = obstacle.obsPoints[Utils.preValue(si, size)];
            curr = obstacle.obsPoints[si];
            isUse = Utils.isPointOnSegmentWithBorder(pre, line) ?
            Utils.calPointPosWithSegment(inPoint, line) * Utils.calPointPosWithSegment(curr, line) < 0:
            false;
            t = 0;
            while (ei != tempObs.endIndex) {
                ei = Utils.nextValue(ei, size);
                next = obstacle.obsPoints[ei];
                if (Utils.isPointOnSegmentWithBorder(curr, line)) {
                    if (t == 0) {
                        t = Utils.calPointPosWithSegment(pre, line);
                    }
                    v = t * Utils.calPointPosWithSegment(next, line);

                    if (v != 0) {
                        t = 0;
                        if (v < 0) {
                            if (isUse) {
                                temp.add(new TempObs(si, Utils.preValue(ei, size)));
                            }
                            si = ei;
                            isUse = !isUse;
                        }
                    }
                } else {
                    if (Utils.isSegmentCrossSegment(curr, pre, line)) {
                        if (isUse) {
                            temp.add(new TempObs(si, Utils.preValue(ei, size)));
                        }
                        isUse = !isUse;
                        si = Utils.preValue(ei, size);
                    }
                }
                pre = curr;
                curr = next;
            }
            if (isUse) {
                temp.add(new TempObs(si, ei));
            }
        }

        if (temp.size() != 0) {
            int p = Utils.calPointPosWithSegment(obstacle.obsPoints[temp.get(0).startIndex], line);
            avoidanceByOnePart(temp, p, line, obstacle);
        } else {
            lines.offer(line);
        }
    }

    private List<TempObs>[] preHandleBeforeAvoidance(Line line, Obstacle obstacle) {
        List<TempObs>[] parts = new List[2];
        parts[0] = new ArrayList<>();
        parts[1] = new ArrayList<>();
        int size = obstacle.obsPoints.length;
        int index = obstacle.index;
        Point pre = obstacle.obsPoints[index];
        Point curr = obstacle.obsPoints[index = Utils.nextValue(index, size)];
        int si = index, ei;
        Point next;
        index = Utils.nextValue(index, size);
        int p = Utils.calPointPosWithSegment(curr, line) > 0 ? 1: 0;
        int t = 0, v;
        for (int i = 0; i < size; i++) {
            next = obstacle.obsPoints[index];
            ei = index;
            if (Utils.isPointOnSegmentWithBorder(curr, line)) {
                if (t == 0) {
                    t = Utils.calPointPosWithSegment(pre, line);
                }
                v = t * Utils.calPointPosWithSegment(next, line);

                if (v != 0) {
                    if (v < 0) {
                        if (t * Utils.calPointPosWithSegment(obstacle.obsPoints[si], line) == 1) {
                            parts[p].add(new TempObs(si, Utils.preValue(ei, size)));
                            p = 1 - p;
                        }
                        si = index;
                    }
                    t = 0;
                }
            } else {
                if (Utils.isSegmentCrossSegment(curr, next, line)) {
                    if (Utils.calPointPosWithSegment(curr, line) *
                            Utils.calPointPosWithSegment(obstacle.obsPoints[si], line) == 1) {
                        parts[p].add(new TempObs(si, ei));
                        p = 1 - p;
                    }
                    si = index;
                }
            }
            index = Utils.nextValue(index, size);
            pre = curr;
            curr = next;
        }

        return parts;
    }

    @Override
    public String toString() {
        return "Network{" +
                "lines=" + lines +
                ", networkNodes=" + networkNodes +
                '}';
    }

}
