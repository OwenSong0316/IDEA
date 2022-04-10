package basePSO;

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

    public static double calVerticalLine(double ax, double ay, double bx, double by, double px, double py) {
        double dap = calDistance(ax, ay, px, py);
        double dab = calDistance(ax, ay, bx, by);
        double dac = ((bx - ax) * (px - ax) + (by - ay) * (py - ay)) / dab;
        return Math.sqrt(dap * dap - dac * dac);
    }

    public static double calDegree(double x1, double y1, double x2, double y2, double d1, double d2) {
        return (int)(180 * Math.acos((x2 * x1 + y1 * y2) / (d1 * d2)) / Math.PI);
    }

    public static double calDeflection(double x1, double y1, double x2, double y2, double d1, double d2) {
        return 180 - calDegree(x1, y1, x2, y2, d1, d2);
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
