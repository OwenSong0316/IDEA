import java.util.TreeSet;

public class Robot { // 机器人类

    public Point startPoint;
    public Point endPoint;
    public Point startVector;
    public Point endVector;

    public boolean axis; // 划分线所属轴：x轴(false) or y轴(true)
    public boolean direct; // 在所属划分线对应的轴上，起点到终点的值是否是正向：正向(true) or 反向(false)
    public boolean pattern; // true = 两段 false = 一段
    public TreeSet<Line> relateLines;
    public int border;


    Robot(Point sp, Point ep, Point sv, Point ev, Environment env) {
        if (sp.x > ep.x) {
            direct = false;
            relateLines = (TreeSet)env.xLines.subSet(env.xLines.floor(new Line(ep.x)), new Line(sp.x));
        } else {
            direct = true;
            relateLines = (TreeSet)env.xLines.subSet(env.xLines.floor(new Line(sp.x)), new Line(ep.x));
        }
        axis = false;
        border = env.xSize;
        TreeSet<Line> lines;
        if (sp.y > ep.y) {
            lines = (TreeSet)env.yLines.subSet(env.yLines.floor(new Line(ep.y)), new Line(sp.y));
            if (lines.size() > relateLines.size()) {
                direct = false;
                axis = true;
            }
        } else {
            lines = (TreeSet)env.yLines.subSet(env.yLines.floor(new Line(sp.y)), new Line(ep.y));
            if (lines.size() > relateLines.size()) {
                direct = true;
                axis = true;
            }
        }
        if (axis) {
            sp.exchange();
            ep.exchange();
            border = env.ySize;
            relateLines = lines;
            sv.exchange();
            ev.exchange();
        }
        if (direct) {
            if (ev.x < 0) {
                ev.reverse();
                pattern = true;
            } else {
                pattern = false;
            }
            startVector = sv;
            endVector = ev;
            startPoint = sp;
            endPoint = ep;
        } else {
            if (ev.x < 0) {
                ev.reverse();
                pattern = false;
            } else {
                pattern = true;
            }
            sv.reverse();
            startVector = ev;
            endVector = sv;
            startPoint = ep;
            endPoint = sp;
        }
    }

    @Override
    public String toString() {
        return "Robot{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", startVector=" + startVector +
                ", endVector=" + endVector +
                ", axis=" + axis +
                ", direct=" + direct +
                ", pattern=" + pattern +
                ", relateLines=" + relateLines +
                ", border=" + border +
                '}';
    }
}
