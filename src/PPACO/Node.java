package PPACO;//package PPACO;
//
//public class Node {
//
//    public double x;
//    public double y;
//    public double priority;
//
//    Node(double x, double y, double pri) {
//        this.x = x;
//        this.y = y;
//        priority = pri;
//    }
//
//    Node(Point p, double pri) {
//        this(p.x, p.y, pri);
//    }
//
//    Node(Point p, Line l) {
//        if (p == null) return;
//
//        priority = (l.a.priority + l.b.priority) / 2;
//
//        if (l.a.x == l.b.x) {
//            if (p.x < l.a.x) {
//                x = p.x - Utils.SAFE_DISTANCE;
//            } else {
//                x = p.x + Utils.SAFE_DISTANCE;
//            }
//            y = p.y;
//        } else if (l.a.y == l.b.y) {
//            if (p.y < l.a.y) {
//                y = p.y - Utils.SAFE_DISTANCE;
//            } else {
//                y = p.y + Utils.SAFE_DISTANCE;
//            }
//            x = p.x;
//        } else {
//            double k = (l.a.y - l.b.y) / (l.a.x - l.b.x);
//            double ly = k * (p.x - l.a.x) + l.a.y;
//            double dab = Utils.calDistance(l.a.x, l.a.y, l.b.x, l.b.y);
//            if (k < 0) {
//                if (p.y > ly) {
//                    x = p.x + Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
//                    y = p.y + Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
//                } else {
//                    x = p.x - Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
//                    y = p.y - Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
//                }
//            } else {
//                if (p.y > ly) {
//                    x = p.x - Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
//                    y = p.y + Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
//                } else {
//                    x = p.x + Math.abs(l.a.y - l.b.y) * Utils.SAFE_DISTANCE / dab;
//                    y = p.y - Math.abs(l.a.x - l.b.x) * Utils.SAFE_DISTANCE / dab;
//                }
//            }
//        }
//    }
//
//    @Override
//    public String toString() {
//        return "Node{" +
//                "x=" + x +
//                ", y=" + y +
//                ", priority=" + priority +
//                '}';
//    }
//
//}
