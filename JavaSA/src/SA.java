import java.io.*;
import java.util.List;
import java.util.Random;

public class SA {
    private String instance = "";
    Random rand = new Random();

    public Data startOptimization(Data ffs, double startingTemperature, int numberOfIterations, double coolingRate, int neighPop, long startTime) throws Exception {
        double t = startingTemperature;
        Data best = (Data) ObjectCloner.deepCopy(ffs);
        rand.setSeed(System.nanoTime());
        Data x;
        x = (Data) ObjectCloner.deepCopy(ffs);

        System.out.println("Initial solution: " + ffs.evaluateSolution());
        List<Data> neigh ;

        for (int i = 0; i < numberOfIterations; i++) {
            if (x.evaluateSolution() < best.evaluateSolution()) {
                best = (Data) ObjectCloner.deepCopy(x);
                System.out.println("NEW BEST\t" + best.evaluateSolution() + "\t" + (System.currentTimeMillis()-startTime)/1000.00 + " s");
            }
            neigh = x.createNeighborhood(neighPop);
            double f = x.evaluateSolution();
            Data mutatedX = neigh.get(rand.nextInt(neighPop));
            double newF = mutatedX.evaluateSolution();
            if (newF > f) {
                double p_ = PR(f, newF, t);
                double p = Math.random();
                if (p_ > p) {
//                    System.out.println("No");
                } else {
//                    System.out.println("Prob new!" + "\t"+ mutatedX.evaluateSolution());
                    x = (Data) ObjectCloner.deepCopy(mutatedX);
                }
            } else {
//                System.out.println("Found new!" + "\t"+ mutatedX.evaluateSolution());
                x = (Data) ObjectCloner.deepCopy(mutatedX);

            }
            if (i%(20) == 0) {
                t = t * coolingRate;
            }
        }
        System.out.println("Initial solution: " + ffs.evaluateSolution());
        System.out.println("Final solution: " + best.evaluateSolution());
        return best;
    }

    double PR(double f1, double f2, double t) {
        return Math.exp(-(f2 - f1) / t);
    }
}
