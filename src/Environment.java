import java.util.*;

/*********
 * 环境类 *
 *********
 * 包含了一切的环境信息，例如障碍物集合、整个测试环境的大小、以及由障碍物计算得出的一系列划分线信息
 */
public class Environment {

    /********************
     ***  输入参数说明  ***
     ********************/

    /***
     * 障碍物集合
     * 二维数组表示，即第一维作为障碍物集合，第二维作为集合中每个障碍物块的表示。
     * 障碍物块的表示则用一个四维向量表示(x,y,xn,yn)：
     * 其中，x和y表示该障碍物块中最左下方(oxy坐标系中)的障碍物的圆心坐标(x,y)，
     * xn代表了障碍物块在x轴方向上的障碍物数量，
     * yn代表了障碍物块在y轴方向上的障碍物数量。
     * 障碍物块默认为将不规则障碍物用相同直径的圆进行覆盖而得区域，默认覆盖效果近似于矩阵
     */
    private int[][] obstacles;
    public int xSize; // x轴方向上的阈值
    public int ySize; // y轴方向上的阈值


    /********************
     ***  生成数据说明  ***
     ********************/

    public TreeSet xLines; // 划分线集合（x轴）
    public TreeSet yLines; // 划分线集合（y轴）


    Environment(int[][] obs, int[] size) {
        obstacles = obs;
        xSize = size[0];
        ySize = size[1];

        genDivideLines();
    }

    private void genDivideLines() {
        Comparator<Line> lineComparator = new Comparator<Line>() {
            @Override
            public int compare(Line o1, Line o2) {
                return o1.value - o2.value;
            }
        };

        xLines = new TreeSet<>(lineComparator);
        xLines.add(new Line(0));
        yLines = new TreeSet<>(lineComparator);
        yLines.add(new Line(0));
        int v;
        for (int[] obstacle : obstacles) {
            xLines.add(new Line(v = obstacle[0] - Util.R));
            xLines.add(new Line(v += obstacle[2] * Util.D));
            yLines.add(new Line(v = obstacle[1] - Util.R));
            yLines.add(new Line(v += obstacle[3] * Util.D));
        }

        addRelateObstacles();
    }

    private void addRelateObstacles() {
        for (int[] obstacle : obstacles) {
            int l;
            add(xLines, l = obstacle[0] - Util.R, l + obstacle[2] * Util.D, obstacle[0], obstacle[1], obstacle[3]);
            add(yLines, l = obstacle[1] - Util.R, l + obstacle[3] * Util.D, obstacle[1], obstacle[0], obstacle[2]);
        }
    }

    private void add(TreeSet<Line> lineSet, int left, int right, int cx, int cy, int ny) {
        Line curr = lineSet.ceiling(new Line(left));
        Line next = lineSet.higher(curr);
        while (left < right) {
            List<Point> tlist = new ArrayList<>();
            for (int i = 0; i < ny; i++) {
                tlist.add(new Point(cx, cy + i * Util.D));
            }
            cx += Util.D;
            curr.obsList.addAll(tlist);
            left += Util.D;
            while (next != null && next.value < left) {
                curr = next;
                next = lineSet.higher(curr);
                curr.obsList.addAll(tlist);
            }
        }
    }

}
