import java.io.*;
import java.util.List;
import java.util.Random;

public class SA {
    private String instance = "";
    Random rand = new Random();


    /**
     * Algorithm of Simulated Annealing
     * @param ffs: first feasible solution
     * @param startingTemperature: starting temperature of Simulated Annealing
     * @param numberOfIterations: max number of iterations
     * @param coolingRate: the cooling rate. The temperature goes down at every iteration
     * @param neighPop: number of components for every neighborhood
     * @param startTime: start time, i think that is useful in order to stop the program after a certain amount of time
     * @return the best solution found
     * @throws Exception
     */
    public Data startOptimization(Data ffs, double startingTemperature, int numberOfIterations, double coolingRate, int neighPop, long startTime) throws Exception {
        double t = startingTemperature;
        Data best = (Data) ObjectCloner.deepCopy(ffs);
        rand.setSeed(System.nanoTime());
        Data x;
        x = (Data) ObjectCloner.deepCopy(ffs);

        System.out.println("Initial solution: " + ffs.evaluateSolution());
        List<Data> neigh ;

        for (int i = 0; i < numberOfIterations; i++) {
            // If the current solution is the best, save it!
            if (x.evaluateSolution() < best.evaluateSolution()) {
                best = (Data) ObjectCloner.deepCopy(x);
                System.out.println("NEW BEST\t" + best.evaluateSolution() + "\t" + (System.currentTimeMillis()-startTime)/1000.00 + " s");
            }
            // Create neighborhood of the current solution
            neigh = x.createNeighborhood(neighPop);
            // Evaluate the solution of the current solution and of a random neighbor
            double f = x.evaluateSolution();
            Data mutatedX = neigh.get(rand.nextInt(neighPop));
            double newF = mutatedX.evaluateSolution();

            // If the solution found is bad, instead of discarting them, use it as current solution randomly (following the probability in the method PR)
            if (newF > f) {
                // Calculate probability p_ with method PR and probability randomly
                double p_ = PR(f, newF, t);
                double p = Math.random();
                // if p_ > p discart the solution, otherwise take it!
                if (p_ > p) {
//                    System.out.println("No");
                } else {
//                    System.out.println("Prob new!" + "\t"+ mutatedX.evaluateSolution());
                    x = (Data) ObjectCloner.deepCopy(mutatedX);
                }
            } // If the solution found is good take it!
            else {
//                System.out.println("Found new!" + "\t"+ mutatedX.evaluateSolution());
                x = (Data) ObjectCloner.deepCopy(mutatedX);

            }

            // Lower the temperature
            if (i%(5) == 0) {
                t = t * coolingRate;
            }
        }
        System.out.println("Initial solution: " + ffs.evaluateSolution());
        System.out.println("Final solution: " + best.evaluateSolution());
        return best;
    }


    /**
     * Calculate exponential probability starting from the evalutation of the two solution and the current temperature
     */
    double PR(double f1, double f2, double t) {
        return Math.exp(-(f2 - f1) / t);
    }
}
