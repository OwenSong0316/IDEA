package PPACO;

public class Utils {

    public static final double SAFE_DISTANCE = 0;
    public static final double E = 0.000000000001;

    Utils() {

    }

//    public static Point searchCrossPoint(double ax, double ay, double bx, double by,
//                                         double cx, double cy, double dx, double dy) {
//        boolean state1 = false, state2 = false;
//        double k1 = Double.MAX_VALUE, k2 = Double.MAX_VALUE, b1, b2;
//
//        if (ax - bx == 0) {
//            state1 = true;
//        } else {
//            k1 = (by - ay) / (bx - ax);
//        }
//        if (cx - dx == 0) {
//            state2 = true;
//        } else {
//            k2 = (dy - cy) / (dx - cx);
//        }
//
//        if (state1 == state2 || k1 == k2) {
//            return new Point(cx, cy);
//        }
//
//        if (state1) {
//            b2 = cy - k2 * cx;
//            return new Point(ax, ax * k2 + b2);
//        }
//
//        if (state2) {
//            b1 = ay - k1 * ax;
//            return new Point(cx, cx * k1 + b1);
//        }
//        b1 = ay - k1 * ax;
//        b2 = cy - k2 * cx;
//        return new Point((b2 - b1) / (k1 - k2), (b1 * k2 + b2 * k1) / (k1 + k2));
//    }

    public static int calPointPosWithSegment(Point p, Line l) {
        if (l.a.x == l.b.x) {
            if (p.x < l.a.x) {
                return -1;
            } else if (p.x == l.a.x) {
                return 0;
            } else {
                return 1;
            }
        } else {
            double k = (l.a.y - l.b.y) / (l.a.x - l.b.x);
            double y = k * (p.x - l.a.x) + l.a.y;
            if (p.y > y) {
                return 1;
            } else if (p.y < y) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static boolean isPointOnSegment(Point p, Line l) {
        if (l.a.x == l.b.x) {
            if (p.x == l.a.x) {
                return (p.y < l.a.y && p.y > l.b.y) || (p.y > l.a.y && p.y < l.b.y);
            } else {
                return false;
            }
        }
        if (l.a.y == l.b.y) {
            if (p.y == l.a.y) {
                return (p.x < l.a.x && p.x > l.b.x) || (p.x > l.a.x && p.x < l.b.x);
            } else {
                return false;
            }
        }
        if ((p.x < l.a.x && p.x > l.b.x) || (p.x < l.b.x && p.x > l.a.x)) {
            double k = (l.a.y - l.b.y) / (l.a.x - l.b.x);
            return (k * (p.x - l.a.x) + l.a.y) == p.y;
        }
        return false;
    }

    public static boolean isPointOnSegmentWithBorder(Point p, Line l) {
        if ((p.y < l.a.y && p.y < l.b.y) || (p.y > l.a.y && p.y > l.b.y)) {
            return false;
        }
        if ((p.x < l.a.x && p.x < l.b.x) || (p.x > l.a.x && p.x > l.b.x)) {
            return false;
        }
        if (l.a.x == l.b.x || l.a.y == l.b.y) {
            return true;
        }
        double k = (l.a.y - l.b.y) / (l.a.x - l.b.x);
        return (k * (p.x - l.a.x) + l.a.y) == p.y;
    }

    public static boolean isSegmentCrossSegment(Point a, Point b, Line l) {
        double k1 = Double.MAX_VALUE, k2 = Double.MAX_VALUE;
        if (a.x != b.x) {
            k1 = (a.y - b.y) / (a.x - b.x);
        }
        if (l.a.x != l.b.x) {
            k2 = (l.a.y - l.b.y) / (l.a.x - l.b.x);
        }
        if (a.x == l.a.x && a.y == l.a.y) {
            return false;
        }
        if (a.x == l.b.x && a.y == l.b.y) {
            return false;
        }
        if (b.x == l.a.x && b.y == l.a.y) {
            return false;
        }
        if (b.x == l.b.x && b.y == l.b.y) {
            return false;
        }
        if (k1 != k2) {
            if (k1 == Double.MAX_VALUE) {
                if ((a.x < l.a.x && a.x > l.b.x) || (a.x < l.b.x && a.x > l.a.x)) {
                    double y = (l.b.y - l.a.y) * (a.x - l.a.x) / (l.b.x - l.a.x) + l.a.y;
                    if ((y < a.y && y > b.y) || (y < b.y && y > a.y)) {
                        return true;
                    }
                }
            } else if (k2 == Double.MAX_VALUE) {
                if ((l.a.x < a.x && l.a.x > b.x) || (l.a.x < b.x && l.a.x > a.x)) {
                    double y = (b.y - a.y) * (l.a.x - a.x) / (b.x - a.x) + a.y;
                    if ((y < l.a.y && y > l.b.y) || (y < l.b.y && y > l.a.y)) {
                        return true;
                    }
                }
            } else {
                double x = ((l.a.y - k2 * l.a.x) - (a.y - k1 * a.x)) / (k1 - k2);
                if (((x < (a.x - E) && x > (b.x + E)) || (x > (a.x + E) && x < (b.x - E))) &&
                        ((x < (l.a.x - E) && x > (l.b.x + E)) || (x > (l.a.x + E) && x < (l.b.x - E)))) {
                    return true;
                }
            }
        }
        return false;
    }

//    public static boolean isPointOnSegment(Node a, Node b, Point c, Point d) {
//        return isPointOnSegment(a.x, a.y, b.x, b.y, c.x, c.y, d.x, d.y);
//    }
//
//    private static boolean isPointOnSegment(double ax, double ay, double bx, double by,
//                                            double cx, double cy, double dx, double dy) {
//        Point crossPoint = searchCrossPoint(ax, ay, bx, by, cx, cy, dx, dy);
//        if (ax == bx) {
//            if ((crossPoint.y > ay && crossPoint.y < by) ||
//                    (crossPoint.y < ay && crossPoint.y > by)) {
//                return true;
//            }
//        } else if ((crossPoint.x > ax && crossPoint.x < bx) ||
//                (crossPoint.x < ax && crossPoint.x > bx)) {
//            return true;
//        }
//        return false;
//    }

    public static int nextValue(int v, int mod) {
        return (v + 1) % mod;
    }

    public static int preValue(int v, int mod) {
        return (v + mod - 1) % mod;
    }

    public static double calPointDisWithLine(Point p, Line l) {
        double dap = calDistance(l.a.x, l.a.y, p.x, p.y);
        double dab = calDistance(l.a.x, l.a.y, l.b.x, l.b.y);
        double dac = ((l.b.x - l.a.x) * (p.x - l.a.x) + (l.b.y - l.a.y) * (p.y - l.a.y)) / dab;
        return Math.sqrt(dap * dap - dac * dac);
    }

    public static double calDistance(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    }

}
