package PPACO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ACO {

    static final double ALPHA = 1;
    static final double BETA = 4;
    static final double RHO = 0.5;
    static final double Q = 100;
    static final int ITERATION = 200;
    static final int ANT_NUM = 20;
    static final int MAX_STEP_COUNT = 50;

    Point[] optionalPoints;
    int startPoint;
    int endPoint;

    int maxStepCount;
    int pointNum;
    int antNum;

    double alpha;
    double beta;
    double q;
    double rho;
    int iteration;

    double[][] distances;
    double[][] pheromones;
    List<Integer>[] passableNet;
    Ant[] ants;

    List<Integer> bestPath;
    double bestFit;

    ACO(Network network) {
        this(network, ANT_NUM, MAX_STEP_COUNT);
    }

    ACO(Network network, int antNum, int maxStepCount) {
        this(network, antNum, maxStepCount, ALPHA, BETA, Q, RHO, ITERATION);
    }

    ACO(Network network, int an, int msc, double a, double b, double q, double r, int it) {
        pointNum = network.networkNodes.size();
        optionalPoints = new Point[pointNum];
        optionalPoints = network.networkNodes.toArray(optionalPoints);
        startPoint = -1;
        endPoint = -1;

        alpha = a;
        beta = b;
        this.q = q;
        rho = r;
        iteration = it;
        maxStepCount = Math.min(msc, pointNum);
        antNum = an;

        initACO(network);
    }

    public void initACO(Network network) {
        initNetworkMsg(network);
        initSolution();
        initAnts();
    }

    public void runACO() {
        for (int i = 1; i <= iteration; i++) {
            for (Ant ant : ants) {
                ant.searchPath();
                if (ant.fit < bestFit) {
                    bestFit = ant.fit;
                    bestPath.clear();
                    bestPath.addAll(ant.path);
                }
            }
            updatePheromones();
//            System.out.println("迭代次数：" + i + " 最佳路径总距离：" + bestFit);
//            for (Integer integer : bestPath) {
//                System.out.print(optionalPoints[integer] + " ");
//            }
//            System.out.println();
        }
    }

    private void updatePheromones() {
        double[][] tempPheromone = new double[pointNum][pointNum];
        for (Ant ant : ants) {
            int pre = startPoint, curr;
            for (int i = 1; i < ant.path.size(); i++) {
                curr = ant.path.get(i);
                tempPheromone[pre][curr] += q / ant.fit;
                tempPheromone[curr][pre] = tempPheromone[pre][curr];
            }
        }

        for (int i = 0; i < pointNum; i++) {
            for (int j = i + 1; j < pointNum; j++) {
                pheromones[i][j] = pheromones[i][j] * rho + tempPheromone[i][j];
                pheromones[j][i] = pheromones[i][j];
            }
        }
    }

    private void initNetworkMsg(Network network) {
        distances = new double[pointNum][pointNum];
        pheromones = new double[pointNum][pointNum];
        passableNet = new List[pointNum];

        for (int i = 0; i < pointNum; i++) {
            if (startPoint == -1 && network.startPoint == optionalPoints[i]) {
                startPoint = i;
            }
            if (endPoint == -1 && network.endPoint == optionalPoints[i]) {
                endPoint = i;
            }
            passableNet[i] = new ArrayList<>();
            distances[i][i] = 0.1;
            for (int j = i + 1; j < pointNum; j++) {
                distances[j][i] = distances[i][j] =
                        Math.sqrt(Math.pow(optionalPoints[i].x - optionalPoints[j].x, 2) +
                                Math.pow(optionalPoints[i].y - optionalPoints[j].y, 2));
            }
        }

        for (int i = 0; i < pointNum; i++) {
            for (int j = i + 1; j < pointNum; j++) {
                pheromones[j][i] = pheromones[i][j] = 1.0;
            }
        }

        for (int i = 0; i < pointNum; i++) {
            for (int j = i + 1; j < pointNum; j++) {
                if (network.detectCollision(new Line(optionalPoints[i], optionalPoints[j])) < 0) {
                    passableNet[i].add(j);
                    passableNet[j].add(i);
                }
            }
        }
    }

    private void initSolution() {
        ants = new Ant[antNum];
        bestPath = new ArrayList<>();
        bestFit = Double.MAX_VALUE;
    }

    private void initAnts() {
        for (int i = 0; i < antNum; i++) {
            ants[i] = new Ant(this);
        }
    }

}
