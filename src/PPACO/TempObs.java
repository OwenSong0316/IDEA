package PPACO;

public class TempObs {

    public int startIndex;
    public int endIndex;

    TempObs(int si, int ei) {
        startIndex = si;
        endIndex = ei;
    }

    @Override
    public String toString() {
        return "TempObs{" +
                "startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                '}';
    }
}
