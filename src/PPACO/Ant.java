package PPACO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ant {

    ACO aco;

    boolean[] openTable;
    List<Integer> path;
    int currPoint;
    double fit;

    Ant(ACO aco) {
        this.aco = aco;

        path = new ArrayList<>();
        openTable = new boolean[aco.pointNum];
    }

    public void searchPath() {
        initAnt();

        while (path.size() < aco.maxStepCount) {
            int nextPoint = searchNextPoint();
            if (nextPoint == -1) {
                break;
            }
            move2NextPoint(nextPoint);
            if (currPoint == aco.endPoint) {
                break;
            }
        }

        calculateFit();
    }

    private void calculateFit() {
        if (!path.get(path.size() - 1).equals(aco.endPoint)) {
            fit = Double.MAX_VALUE;
            return;
        }

        int pre = aco.startPoint;
        fit = 0.0;
        for (Integer curr : path) {
            fit += aco.distances[pre][curr];
            pre = curr.intValue();
        }
    }

    private void initAnt() {
        for (int i = 0; i < aco.pointNum; i++) {
            openTable[i] = true;
        }
        currPoint = aco.startPoint;

        openTable[currPoint] = false;

        path.clear();
        path.add(currPoint);
    }

    private int searchNextPoint() {
        int nextPoint = -1;
        double[] selectPointProb = new double[aco.passableNet[currPoint].size()];
        double totalProb = 0.0;

        int index;
        for (int i = 0 ; i < selectPointProb.length; i++) {
            index = aco.passableNet[currPoint].get(i);
            if (openTable[index]) {
                selectPointProb[i] = Math.pow(aco.pheromones[currPoint][index], aco.alpha) *
                        Math.pow(1.0 / aco.distances[index][aco.endPoint], aco.beta);
                totalProb += selectPointProb[i];
            }
        }

        if (totalProb > 0.0) {
            double tempProb = Math.random() * totalProb;
            for (int i = 0; i < selectPointProb.length; i++) {
                if (selectPointProb[i] > 0.0) {
                    tempProb -= selectPointProb[i];
                    if (tempProb <= 0.0) {
                        nextPoint = aco.passableNet[currPoint].get(i);
                        break;
                    }
                }
            }
        }

        return nextPoint;
    }

    private void move2NextPoint(int nextPoint) {
        path.add(nextPoint);
        openTable[nextPoint] = false;
        currPoint = nextPoint;
    }

}
