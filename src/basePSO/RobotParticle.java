package basePSO;

public class RobotParticle {

    private Robot robot; // 机器人
    private int[][] obstacles;
    private int[] size;

    public double[][] x;
    private double[][] v;
    public double cost;

    RobotParticle(Robot robot, int d, int[][] obs, int[] size) {
        this.robot = robot;
        obstacles = obs;
        this.size = size;

        x = new double[d][2];
        v = new double[d][2];
        double xPrePoint = robot.startPoint.x, yPrePoint = robot.startPoint.y,
                xCurrPoint, yCurrPoint, xPrePrePoint = 0, yPrePrePoint = 0;
        for (int i = 0; i < d; i++) {
            xCurrPoint = Math.random() * size[0];
            yCurrPoint = Math.random() * size[1];
//            while (collisionDetection(xCurrPoint, yCurrPoint, xPrePoint, yPrePoint) > 0) {
//                xCurrPoint = Math.random() * size[0];
//                yCurrPoint = Math.random() * size[1];
//            }
            x[i][0] = xCurrPoint;
            x[i][1] = yCurrPoint;
            v[i][0] = Math.random() - 0.5;
            v[i][1] = Math.random() - 0.5;
            xPrePrePoint = xPrePoint;
            yPrePrePoint =yPrePoint;
            xPrePoint = xCurrPoint;
            yPrePoint = yCurrPoint;
        }
        xCurrPoint = robot.endPoint.x;
        yCurrPoint = robot.endPoint.y;
//        while (collisionDetection(xCurrPoint, yCurrPoint, xPrePoint, yPrePoint) > 0 ||
//                collisionDetection(xPrePrePoint, yPrePrePoint, xPrePoint, yPrePoint) > 0) {
//            xPrePoint = Math.random() * size[0];
//            yPrePoint = Math.random() * size[1];
//        }
        x[d - 1][0] = xPrePoint;
        x[d - 1][1] = yPrePoint;

        calculateCost();
    }

    RobotParticle(RobotParticle particle) {
        x = new double[particle.x.length][2];
        v = new double[0][];

        cloneData(particle);
    }

    public void cloneData(RobotParticle particle) {
        robot = particle.robot;
        obstacles = particle.obstacles;
        size = particle.size;
        cost = particle.cost;
        double[][] tx = particle.x;
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < 2; j++) {
                x[i][j] = tx[i][j];
            }
        }
    }

    public void updateParticleMsg(RobotParticle pBest, RobotParticle gBest) {
        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < 2; j++) {
                v[i][j] = Util.W * v[i][j]
                        + Util.C1 * Math.random() * (pBest.x[i][j] - x[i][j])
                        + Util.C2 * Math.random() * (gBest.x[i][j] - x[i][j]);
                if (v[i][j] > 10) v[i][j] = 10;
                else if (v[i][j] < -10) v[i][j] = -10;
                x[i][j] += v[i][j];
                if (x[i][j] > size[j]) {
                    x[i][j] = size[j];
                } else if (x[i][j] < 0) {
                    x[i][j] = 0;
                }
            }
        }

        calculateCost();
    }

    public void calculateCost() {
        double pathLen = 0, turnCount = 0, unsuitedTurnCount = 0, collisionCount = 0.1;

        double xCurrPoint = x[0][0], yCurrPoint = x[0][1];
        double xNextPoint = robot.startPoint.x, yNextPoint = robot.startPoint.y;
        double xPreVector = xCurrPoint - xNextPoint, yPreVector = yCurrPoint - yNextPoint;
        double xCurrVector, yCurrVector;
        double deflection, preDis, currDis;

        preDis = Util.calDistance(xNextPoint, yNextPoint, xCurrPoint, yCurrPoint);
        pathLen += preDis;
        if ((deflection = Util.calDeflection(xPreVector, yPreVector, robot.startVector.x, robot.startPoint.y,
                preDis, Util.calDistance(0, 0, robot.startVector.x, robot.startVector.y))) < Util.OMIT_DEGREE) {
            turnCount++;
            if (deflection < Util.SUITABLE_DEGREE) {
                unsuitedTurnCount++;
            }
        }
        collisionCount += collisionDetection(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);

        for (int i = 1; i < x.length; i++) {
            xNextPoint = x[i][0];
            yNextPoint = x[i][1];
            xCurrVector = xNextPoint - xCurrPoint;
            yCurrVector = yNextPoint - xCurrPoint;
            currDis = Util.calDistance(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
            pathLen += currDis;
            if ((deflection = Util.calDeflection(xPreVector, yPreVector, xCurrVector,
                    yCurrVector, preDis, currDis)) < Util.OMIT_DEGREE) {
                turnCount++;
                if (deflection < Util.SUITABLE_DEGREE) {
                    unsuitedTurnCount++;
                }
            }
            collisionCount += collisionDetection(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);

            xPreVector = xCurrVector;
            yPreVector = yCurrVector;
            xCurrPoint = xNextPoint;
            yCurrPoint = yNextPoint;
            preDis = currDis;
        }

        xNextPoint = robot.endPoint.x;
        yNextPoint = robot.endPoint.y;
        xCurrVector = xNextPoint - xCurrPoint;
        yCurrVector = yNextPoint - yCurrPoint;
        currDis = Util.calDistance(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
        pathLen += currDis;
        if ((deflection = Util.calDeflection(xPreVector, yPreVector, xCurrVector,
                yCurrVector, preDis, currDis)) < Util.OMIT_DEGREE) {
            turnCount++;
            if (deflection < Util.SUITABLE_DEGREE) {
                unsuitedTurnCount++;
            }
        }
        collisionCount += collisionDetection(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);

        if ((deflection = Util.calDeflection(xCurrVector, yCurrVector, robot.endVector.x, robot.endVector.y,
                currDis, Util.calDistance(0, 0, robot.endVector.x, robot.endVector.y))) < Util.OMIT_DEGREE) {
            turnCount++;
            if (deflection < Util.SUITABLE_DEGREE) {
                unsuitedTurnCount++;
            }
        }

        cost = (pathLen + turnCount * 2 + unsuitedTurnCount * 4) * collisionCount;
    }

    private int collisionDetection(double xCurrPoint, double yCurrPoint, double xPrePoint, double yPrePoint) {
        int count = 0;
        int x, y;
        for (int[] obstacle : obstacles) {
            x = obstacle[0];
            for (int i = 0; i < obstacle[2]; i++) {
                y = obstacle[1];
                for (int j = 0; j < obstacle[3]; j++) {
                    double currDis = Util.calDistance(xCurrPoint, yCurrPoint, x, y);
                    double preDis = Util.calDistance(xPrePoint, yPrePoint, x, y);
                    if (currDis >= Util.R && preDis >= Util.R) {
                        double dis = Util.calVerticalLine(xCurrPoint, yCurrPoint, xPrePoint, yPrePoint, x, y);
                        if (dis < Util.R) {
                            if (Util.calDegree(xCurrPoint - x, yCurrPoint - y, xPrePoint - x,
                                    yPrePoint - y, currDis, preDis) >= 90) {
                                count++;
                            } else {
                                double abDis = Util.calDistance(xCurrPoint, yCurrPoint, xPrePoint, yPrePoint);
                                if ((Util.calDegree(x - xCurrPoint, y - yCurrPoint, xPrePoint - xCurrPoint,
                                        yPrePoint - yCurrPoint, currDis, abDis) < 90) &&
                                        (Util.calDegree(x - xPrePoint, y - yPrePoint, xCurrPoint - xPrePoint,
                                                yCurrPoint -yPrePoint, preDis, abDis) < 90)) {
                                    count++;
                                }
                            }

                        }
                    } else {
                        count++;
                    }
                    y += Util.D;
                }
                x += Util.D;
            }
        }
        return count;
    }

}
