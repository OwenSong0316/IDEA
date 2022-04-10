package basePSO;

import java.util.Iterator;

public class PSO {

    private static final int ITERATION_COUNT = 300; // 默认粒子群迭代次数
    private static final int PARTICLE_NUM = 20; // 默认单机器人的粒子数

    private int[][] obstacles;
    private int[] size;
    private int d;
    private Robot[] robots;
    private int iterationCount;
    private int particleNum;

    private RobotParticle[][] robotParticleCurrPaths;
    private RobotParticle[][] robotParticleBestPaths;
    private RobotParticle[] robotBestPaths;

    PSO(int[][] obs, int[] size, int d, Robot[] robots) {
        obstacles = obs;
        this.size = size;
        this.d = d;
        this.robots = robots;
        iterationCount = ITERATION_COUNT;
        particleNum = PARTICLE_NUM;

        initPSO();
    }

    private void initPSO() {
        robotParticleCurrPaths = new RobotParticle[robots.length][particleNum];
        robotParticleBestPaths = new RobotParticle[robots.length][particleNum];
        robotBestPaths = new RobotParticle[robots.length];

        RobotParticle trp;
        Robot tr;
        for (int i = 0; i < robots.length; i++) {
            tr = robots[i];
            trp = new RobotParticle(tr, d, obstacles, size);
            robotParticleCurrPaths[i][0] = trp;
            robotParticleBestPaths[i][0] = new RobotParticle(trp);
            robotBestPaths[i] = new RobotParticle(trp);
            for (int j = 1; j < particleNum; j++) {
                trp = new RobotParticle(tr, d, obstacles, size);
                robotParticleCurrPaths[i][j] = trp;
                robotParticleBestPaths[i][j] = new RobotParticle(trp);
                if (trp.cost < robotBestPaths[i].cost) {
                    robotBestPaths[i].cloneData(trp);
                }
            }
        }

    }

    public void runPSO() {
        RobotParticle trp;
        int[] title = new int[iterationCount];
        double[][] data = new double[robots.length][iterationCount];
        String filename = "test_base";
        for (int ic = 1; ic <= iterationCount; ic++) {
//            System.out.println("curr iteration: " + ic);
            for (int i = 0; i < robots.length; i++) {
//                System.out.println("robot " + i + ": " + robots[i]);
                for (int j = 0; j < particleNum; j++) {
                    trp = robotParticleCurrPaths[i][j];
                    trp.updateParticleMsg(robotParticleBestPaths[i][j], robotBestPaths[i]);
//                    System.out.println("particle num: " + (j + 1));
                    if (trp.cost < robotParticleBestPaths[i][j].cost) {
                        robotParticleBestPaths[i][j].cloneData(trp);
                        if (trp.cost < robotBestPaths[i].cost) {
                            robotBestPaths[i].cloneData(trp);
                        }
                    }
//                    System.out.println("curr particle best cost: " + robotParticleBestPaths[i][j].cost);
                }
                title[ic - 1] = ic;
                data[i][ic - 1] = robotBestPaths[i].cost;
//                System.out.println("curr robot best cost: " + robotBestPaths[i].cost);
//                System.out.println("curr robot best path: " + robotBestPaths[i]);
            }
            Util.W = Util.W - (Util.W - 0.3) / ic;
        }
        Util.importCSVFile(title, data, filename);
    }

    public void printMsg() {
        StringBuilder sb = new StringBuilder("base_relate = [");
        for (int i = 0; i < robots.length; i++) {
            Robot robot = robots[i];
            sb.append('[');
            for (RobotParticle particle : robotParticleBestPaths[i]) {
                sb.append('[');

                sb.append('[');
                sb.append(robot.startPoint.x);
                sb.append(',');
                for (int t = 0; t < particle.x.length; t++) {
                    sb.append(particle.x[t][0]);
                    sb.append(",");
                }
                sb.append(robot.endPoint.x);
                sb.append(']');

                sb.append(',');

                sb.append('[');
                sb.append(robot.startPoint.y);
                sb.append(',');
                for (int t = 0; t < particle.x.length; t++) {
                    sb.append(particle.x[t][1]);
                    sb.append(",");
                }
                sb.append(robot.endPoint.y);
                sb.append(']');

                sb.append("],");
            }
            sb.delete(sb.length() - 1 , sb.length());
            sb.append("],");
        }
        sb.delete(sb.length() - 1 , sb.length());
        sb.append("]\nbase_relate2 = [");
        for (int i = 0; i < robots.length; i++) {
            Robot robot = robots[i];
            sb.append('[');

            sb.append('[');
            sb.append('[');
            sb.append(robot.startPoint.x);
            sb.append(',');
            for (int t = 0; t < robotBestPaths[i].x.length; t++) {
                sb.append(robotBestPaths[i].x[t][0]);
                sb.append(",");
            }
            sb.append(robot.endPoint.x);
            sb.append(']');

            sb.append(',');

            sb.append('[');
            sb.append(robot.startPoint.y);
            sb.append(',');
            for (int t = 0; t < robotBestPaths[i].x.length; t++) {
                sb.append(robotBestPaths[i].x[t][1]);
                sb.append(",");
            }
            sb.append(robot.endPoint.y);
            sb.append(']');
            sb.append(']');

            sb.append("],");
        }
        sb.delete(sb.length() - 1 , sb.length());
        sb.append(']');
        System.out.println(sb);
    }

}
