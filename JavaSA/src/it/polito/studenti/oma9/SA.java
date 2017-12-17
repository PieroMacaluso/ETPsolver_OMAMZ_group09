package it.polito.studenti.oma9;

import java.util.Random;

class SA {
	private Random rand = new Random();

	/**
	 * Algorithm of Simulated Annealing
	 *
	 * @param ffs:                 first feasible solution
	 * @param startingTemperature: starting temperature of Simulated Annealing
	 * @param numberOfIterations:  max number of iterations
	 * @param coolingRate:         the cooling rate. The temperature goes down at every iteration
	 * @param neighPop:            number of components for every neighborhood
	 * @param startTime:           start time, i think that is useful in order to stop the program after a certain amount of time
	 * @return the best solution found
	 * @throws Exception when stuff that shouldn't happen happens
	 */
	@SuppressWarnings("SameParameterValue")
	Data startOptimization(Data ffs, double startingTemperature, int numberOfIterations, double coolingRate, int neighPop, long startTime) throws Exception {
		double t = startingTemperature;
		LS ls = new LS();
		Data best = (Data) ObjectCloner.deepCopy(ffs);
		rand.setSeed(System.nanoTime());
		Data x;
		x = (Data) ObjectCloner.deepCopy(ffs);

		System.out.println("Initial solution: " + ffs.evaluateSolution());

		for(int i = 0; i < numberOfIterations; i++) {
			// If the current solution is the best, save it!
			if(x.evaluateSolution() < best.evaluateSolution()) {
				best = (Data) ObjectCloner.deepCopy(x);
				System.out.println("NEW BEST\t" + best.evaluateSolution() + "\t" + (System.currentTimeMillis() - startTime) / 1000.00 + " s");
				best.printSolution();
			}
			// Create neighborhood of the current solution

			// Evaluate the solution of the current solution and of a random neighbor
			double f = x.evaluateSolution();
			ls.deepOptimization(x, 0.1);

			//Data neigh = neigh.get(rand.nextInt(neighPop));
			Data neigh = x.createNeighbor();
			ls.deepOptimization(neigh, 0.1);
			double newF = neigh.evaluateSolution();

			// If the solution found is bad, instead of discarding them, use it as current solution randomly (following the probability in the method PR)
			if(newF > f) {
				// Calculate probability p_ with method PR and probability randomly
				double p_ = PR(f, newF, t);
				double p = Math.random();
				// if p_ > p discard the solution, otherwise take it!
				//noinspection StatementWithEmptyBody
				if(p_ > p) {
					System.out.println("Discarded" + "\t" + newF + " (probability " + String.format("%4.2f > %4.2f)", p_, p));
//                    System.out.println("No");
				} else {
					System.out.println("Prob new!" + "\t" + newF);
					x = (Data) ObjectCloner.deepCopy(neigh);
				}
			} // If the solution found is good take it!
			else {
				System.out.println("Found new!" + "\t" + newF);
				x = (Data) ObjectCloner.deepCopy(neigh);
			}

			// Lower the temperature
			if(i % (5) == 0) {
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
	private double PR(double f1, double f2, double t) {
		// TODO: this should be always decreasing, but values seem very random
		return Math.exp(-(f2 - f1) / t);
	}
}
