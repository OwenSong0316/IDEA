import java.io.*;

public class Util {

    public static final int OMIT_DEGREE = 175;
    public static final int SUITABLE_DEGREE = 120;
    public static final int CHANGE_DEGREE = 45;
    public static final int R = 2;
    public static final int D = 4;
    public static final int TURN_R = 3;
    public static final double SAFE_DISTANCE = 3;
    public static final double C1 = 2; // 默认自身认知项参数
    public static final double C2 = 2; // 默认群体认知项参数
    public static double W = 0.9;
    public static String PATH = "/Users/xubin/Documents/idea/PaperExperiments/documents/";

    public static double calDistance(double ax, double ay, double bx, double by) {
        return Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    }

    public static int calDegree(double x1, double y1, double x2, double y2, double d1, double d2) {
        return 180 - (int)(180 * Math.acos((x2 * x1 + y1 * y2) / (d1 * d2)) / Math.PI);
//         (180 * radian / Math.PI);
    }

    public static int judgeSafety(double ax, double ay, double bx, double by, double px, double py) {
        return calVerticalLine(ax, ay, bx, by, px, py) >= SAFE_DISTANCE ? 0 : 1;
    }

    private static double calVerticalLine(double ax, double ay, double bx, double by, double px, double py) {
        double dap = calDistance(ax, ay, px, py);
        double dab = calDistance(ax, ay, bx, by);
        double dac = ((bx - ax) * (px - ax) + (by - ay) * (py - ay)) / dab;
        return Math.sqrt(dap * dap - dac * dac);
    }

    public static double[] genRelatePointOnCircle(double cx, double cy, double px, double py) {
        double k = TURN_R / calDistance(cx, cy, px, py);
        return new double[]{cx - k * (cx - px), cy - k * (cy - py)};
    }

    public static double[] genNewPointOnCircle(double cx, double cy, double ax, double ay, double bx, double by) {
        double tx = ay - cy, ty = ax - cx;
        int t;
        if ((t = calDegree(-tx, ty, bx - cx, by - cy, R, R)) < 90) {
            return new double[]{cx - tx, cy + ty};
        }
        return new double[]{cx + tx, cy - ty};
    }

    public static int obtuseAnglePrediction(double ax, double ay, double bx, double by,
                                            double cx, double cy, double tx, double ty,
                                            double nx, double ny) {
        if (acuteAnglePrediction(ax, ay, tx, ty, cx, cy, nx, ny) == 1) {
            return 1;
        }
        return acuteAnglePrediction(tx, ty, bx, by, cx, cy, nx, ny);
    }

    public static int acuteAnglePrediction(double ax, double ay, double bx, double by,
                                           double cx, double cy, double nx, double ny) {
        if (haveSamePoint(ax, ay, bx, by, cx, cy, nx, ny)) {
            if (calVerticalLine(ax, ay, bx, by, nx, ny) <= R) {
                return 1;
            }
        }
        return 0;
    }

    private static boolean haveSamePoint(double ax, double ay, double bx, double by,
                                          double cx, double cy, double nx, double ny) {
        if (cx == nx) {
            if ((ax < cx && bx < cx) || (ax > cx && bx > cx)) {
                return false;
            } else {
                double k = (by - ay) / (bx - ax);
                double y = ay + k * (cx - ax);
                if ((y < cy && y > ny) || (y < ny && y > cy)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else if (ax == bx) {
            if ((cx > ax && nx > ax) || (cx < ax && nx < ax)) {
                return false;
            } else {
                double k = (ny - cy) / (nx - cx);
                double y = cy + k * (ax - cx);
                if ((y < ay && y > by) || (y < by && y > ay)) {
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            double k1 = (by - ay) / (bx - ax);
            double b1 = ay - k1 * ax;
            double k2 = (cy - ny) / (cx - nx);
            double b2 = cy - k2 * cx;
            if (k1 == k2) {
                return false;
            }
            double x = (b1 - b2) / (k2 - k1);
            if (((x <= ax && x >= bx) || (x <= bx && x >= ax)) && ((x <= cx && x >= nx) || (x <= nx && x >= cx))) {
                return true;
            } else {
                return false;
            }
        }
    }

    // 2 ; 1.5
    public static double factorConversion(double degree) {
        return degree / 180 * 20 + 5;
    }

    public static File importCSVFile(int[] title, double[][] data, String filename) {
        return createAndWriteCSVFile(title, data, PATH, filename);
    }

    private static File createAndWriteCSVFile(int[] title, double[][] data, String path, String filename) {

        File csvFile = null;
        BufferedWriter csvWtriter = null;
        try {
            csvFile = new File(path + filename + ".csv");
            csvFile.createNewFile();

            // GB2312使正确读取分隔符","
            csvWtriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), "GB2312"), 1024);
            // 写入文件头部
            writeRow(title, csvWtriter);

            // 写入文件内容
            for (double[] row : data) {
                writeRow(row, csvWtriter);
            }
            csvWtriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                csvWtriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    private static void writeRow(int[] row, BufferedWriter csvWriter) throws IOException {
        for (int data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

    private static void writeRow(double[] row, BufferedWriter csvWriter) throws IOException {
        for (double data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

}
