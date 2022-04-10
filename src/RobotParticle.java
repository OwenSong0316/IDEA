import java.util.Arrays;
import java.util.Iterator;

public class RobotParticle {

    public Robot robot; // 机器人

    public double[] x; // 机器人在划分线上的值的集合
    public double[] v;
    private int[] deflection;
    private double[] segLen;
    public double xPoint;
    public double yPoint;
    public double cost;

    RobotParticle(Robot robot) {
        this.robot = robot;

        x = new double[robot.relateLines.size()];
        v = new double[x.length];
        deflection = new int[x.length + 1];
        segLen = new double[x.length];
        for (int i = 1; i < x.length; i++) {
            x[i] = Math.random() * robot.border;
            v[i] = Math.random() - 0.5;
        }

        calculateCost();
    }

    RobotParticle(RobotParticle particle) {
        x = new double[particle.x.length];
        v = new double[0];
        deflection = new int[0];
        segLen = new double[0];

        cloneData(particle);
    }

    public void calculateCost() {
        double pathLen = 0, turnCount = 0, unsuitedTurnCount = 0, collisionCount = 0.1,
                backingPathLen = 0, backingCollisionCount, backingFactor, backingCost = 0;

        Line currLine, nextLine;
        int index = 0;
        double xCurrPoint = robot.startPoint.x, yCurrPoint = robot.startPoint.y;
        double xNextPoint, yNextPoint;
        double xPreVector = robot.startVector.x, yPreVector = robot.startVector.y;
        double xCurrVector, yCurrVector;
        double preDis = Util.calDistance(0, 0, xPreVector, yPreVector), currDis;

        Iterator iterator = robot.relateLines.iterator();
        currLine = (Line)iterator.next();
        while (iterator.hasNext()) {
            nextLine = (Line)iterator.next();
            xNextPoint = nextLine.value;
            yNextPoint = x[index + 1];
            currDis = Util.calDistance(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
            xCurrVector = xNextPoint - xCurrPoint;
            yCurrVector = yNextPoint - yCurrPoint;
            deflection[index] = Util.calDegree(xPreVector, yPreVector, xCurrVector, yCurrVector, preDis, currDis);
            pathLen += (segLen[index] = currDis);
            if (deflection[index] < Util.OMIT_DEGREE) {
                turnCount++;
                if (deflection[index] < Util.SUITABLE_DEGREE) {
                    unsuitedTurnCount++;
                }
            }
            collisionCount += collisionDetection(currLine, xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);

            index++;
            xCurrPoint = xNextPoint;
            yCurrPoint = yNextPoint;
            preDis = currDis;
            xPreVector = xCurrVector;
            yPreVector = yCurrVector;
            currLine = nextLine;
        }

        xNextPoint = robot.endPoint.x;
        yNextPoint = robot.endPoint.y;
        currDis = Util.calDistance(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
        deflection[index] = Util.calDegree(xPreVector, yPreVector, xCurrVector = (xNextPoint - xCurrPoint),
                yCurrVector = (yNextPoint - yCurrPoint), preDis, currDis);
        pathLen += (segLen[index] = currDis);
        if (deflection[index] < Util.OMIT_DEGREE) {
            turnCount++;
            if (deflection[index] < Util.SUITABLE_DEGREE) {
                unsuitedTurnCount++;
            }
        }
        collisionCount += collisionDetection(currLine, xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
        deflection[++index] = Util.calDegree(xCurrVector, yCurrVector, robot.endVector.x, robot.endVector.y,
                currDis, Util.calDistance(0, 0, robot.endVector.x, robot.endVector.y));
        if (deflection[index] < Util.OMIT_DEGREE) {
            turnCount++;
            if (deflection[index] < Util.SUITABLE_DEGREE) {
                unsuitedTurnCount++;
            }
        }

        if (robot.pattern) {
            index = 1;
            double xPrePoint = robot.startPoint.x, yPrePoint = robot.startPoint.y;
            double len = robot.direct ? pathLen - segLen[0] : segLen[0];
            double tempCost;
            backingCost = Double.MAX_VALUE;

            iterator = robot.relateLines.iterator();
            Line preLine = (Line)iterator.next();
            xCurrPoint = currLine.value;
            yCurrPoint = x[index];
            currLine = (Line)iterator.next();
            while (iterator.hasNext()) {
                nextLine = (Line)iterator.next();
                xNextPoint = nextLine.value;
                yNextPoint = x[index + 1];
                if (robot.direct) {
                    len -= segLen[index];
                } else {
                    len += segLen[index];
                }
                boolean deflectionState = deflection[index] > 90 ? true : false;
                backingCollisionCount = 1 + collisionPrediction(currLine, xCurrPoint, yCurrPoint, xPrePoint,
                        yPrePoint, xNextPoint, yNextPoint, deflectionState);
                backingCollisionCount += collisionPrediction(preLine, xCurrPoint, yCurrPoint, xPrePoint,
                        yPrePoint, xNextPoint, yNextPoint, deflectionState);
                backingFactor = Util.factorConversion(deflection[index]);
                tempCost = len * backingFactor * backingCollisionCount;
                if (tempCost < backingCost) {
                    backingPathLen = len;
                    backingCost = tempCost;
                    xPoint = xCurrPoint;
                    yPoint = yCurrPoint;
                }

                index++;
                xPrePoint = xCurrPoint;
                yPrePoint = yCurrPoint;
                xCurrPoint = xNextPoint;
                yCurrPoint = yNextPoint;
                preLine = currLine;
                currLine = nextLine;
            }

            xNextPoint = robot.endPoint.x;
            yNextPoint = robot.endPoint.y;
            if (robot.direct) {
                len -= segLen[index];
            } else {
                len += segLen[index];
            }
            boolean deflectionState = deflection[index] > 90 ? true : false;
            backingCollisionCount = 1 + collisionPrediction(currLine, xCurrPoint, yCurrPoint, xPrePoint,
                    yPrePoint, xNextPoint, yNextPoint, deflectionState);
            backingCollisionCount += collisionPrediction(preLine, xCurrPoint, yCurrPoint, xPrePoint,
                    yPrePoint, xNextPoint, yNextPoint, deflectionState);
            backingFactor = Util.factorConversion(deflection[index]);
            tempCost = len * backingFactor * backingCollisionCount;
            if (tempCost < backingCost) {
                backingPathLen = len;
                backingCost = tempCost;
                xPoint = xCurrPoint;
                yPoint = yCurrPoint;
            }
        }

//        cost = pathLen + turnCount * 5 + unsuitedTurnCount * 10 + collisionCount * 15; // 1
//        cost = (pathLen + turnCount * 5 + unsuitedTurnCount * 10) * collisionCount; // 2
//        cost = pathLen * turnCount * unsuitedTurnCount * collisionCount; // 3
//        cost = (pathLen + turnCount * 2 + unsuitedTurnCount * 4) * collisionCount;
        // 2 4
        cost = (pathLen + turnCount * 2 + unsuitedTurnCount * 4 - backingPathLen + backingCost) * collisionCount;
    }

    private int collisionDetection(Line currLine, double xCurrPoint, double yCurrPoint,
                                   double xNextPoint, double yNextPoint) {
        if (currLine.obsList.size() == 0) return 0;
        int count = 0;
        for (Point p : currLine.obsList) {
            count += Util.judgeSafety(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint, p.x, p.y);
        }
        return count;
    }

    private int collisionPrediction(Line currLine, double xCurrPoint, double yCurrPoint, double xPrePoint,
                                   double yPrePoint, double xNextPoint, double yNextPoint, boolean deflectionState) {
        if (currLine.obsList.size() == 0) return 0;
        int count = 0;
        boolean initState = true;
        double xPointA = 0, yPointA = 0, xPointB = 0, yPointB = 0, xPointC = 0, yPointC = 0;
        for (Point p : currLine.obsList) {
            if (Util.calDistance(p.x, p.y, xCurrPoint, yCurrPoint) < Util.R + Util.TURN_R) {
                if (initState) {
                    double[] temp = Util.genRelatePointOnCircle(xCurrPoint, yCurrPoint, xPrePoint, yPrePoint);
                    xPointA = temp[0];
                    yPointA = temp[1];
                    temp = Util.genRelatePointOnCircle(xCurrPoint, yCurrPoint, xNextPoint, yNextPoint);
                    xPointB = temp[0];
                    yPointB = temp[1];
                    if (deflectionState) {
                        temp = Util.genNewPointOnCircle(xCurrPoint, yCurrPoint,
                                xPrePoint, yPrePoint, xNextPoint, yNextPoint);
                        xPointC = temp[0];
                        yPointC = temp[1];
                    }
                    initState = false;
                }
                if (deflectionState) {
                    count += Util.obtuseAnglePrediction(xPointA, yPointA, xPointB, yPointB,
                            xCurrPoint, yCurrPoint, xPointC, yPointC, p.x, p.y);
                } else {
                    count += Util.acuteAnglePrediction(xPointA, yPointA, xPointB, yPointB,
                            xCurrPoint, yCurrPoint, p.x, p.y);
                }
            }
        }
        return count;
    }

    public void cloneData(RobotParticle robotParticle) {
        robot = robotParticle.robot;
        xPoint = robotParticle.xPoint;
        yPoint = robotParticle.yPoint;
        cost = robotParticle.cost;
        double[] tx = robotParticle.x;
        for (int i = 0; i < x.length; i++) {
            x[i] = tx[i];
        }
    }

    public void updateParticleMsg(RobotParticle pBest, RobotParticle gBest) {
        for (int i = 0; i < x.length; i++) {
            v[i] = Util.W * v[i]
                    + Util.C1 * Math.random() * (pBest.x[i] - x[i])
                    + Util.C2 * Math.random() * (gBest.x[i] - x[i]);
            if (v[i] > 10) v[i] = 10;
            else if (v[i] < -10) v[i] = -10;
            x[i] += v[i];
            if (x[i] > robot.border) {
                x[i] = robot.border;
            } else if (x[i] < 0) {
                x[i] = 0;
            }
        }

        calculateCost();
    }

    @Override
    public String toString() {
        return "RobotParticle{" +
                "x=" + Arrays.toString(x) +
                ", v=" + Arrays.toString(v) +
                ", deflection=" + Arrays.toString(deflection) +
                ", cost=" + cost +
                '}';
    }

}
