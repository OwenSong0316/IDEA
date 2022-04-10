public class MainStart {

    public static void main(String[] args) {
        int[][] obs = {
                {5, 5, 2, 2},
                {17, 14, 4, 3},
                {14, 46, 8, 5},
                {57, 27, 2, 10},
                {79, 94, 1, 1},
                {86, 25, 1, 1}
        };
        int[] size = {100, 100};
        Environment env = new Environment(obs, size);
        Robot[] robots = new Robot[2];
        robots[0] = new Robot(new Point(18, 4), new Point(58, 74),
                new Point(1, 1), new Point(-1, -1), env);
        robots[1] = new Robot(new Point(89, 96), new Point(76, 38),
                new Point(-1, -1), new Point(-1, -1), env);
//        robots[0] = new Robot(new Point(54, 23), new Point(14, 74),
//                new Point(1, 1), new Point(1, -1), env);
//        robots[1] = new Robot(new Point(1, 63), new Point(73, 16),
//                new Point(1, -1), new Point(1, 0), env);
//        robots[0] = new Robot(new Point(24, 2), new Point(55, 80),
//                new Point(1, 0), new Point(1, 0), env);
//        robots[1] = new Robot(new Point(95, 83), new Point(10, 44),
//                new Point(-1, 0), new Point(-1, -1), env);
        PSO pso = new PSO(robots);
//        pso.printMsg();
        pso.runPSO();
        pso.printMsg();
    }

}