package it.polito.studenti.oma9;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;

class SA {
	/**
	 * Algorithm of Simulated Annealing
	 *
	 * @param startSol :                 first feasible solution
	 * @param initialTemperature : starting temperature of Simulated Annealing
	 * @param endTime :           start time, i think that is useful in order to stop the program after a certain amount of time
	 * @return the best solution found
	 * @throws Exception when stuff that shouldn't happen happens
	 */
	@SuppressWarnings("SameParameterValue")
	Solution startOptimization(Solution startSol, double initialTemperature, Temporal startTime, Temporal endTime) throws Exception {
		Double temperature = initialTemperature;
		LS ls = new LS();
		Solution best = new Solution(startSol);
		Duration toEnd;
		Duration total = Duration.between(startTime, endTime);
		Solution x = new Solution(startSol);

		System.out.println("Initial solution: " + startSol.evaluateCost());

//		for(int i = 0; i < numberOfIterations; i++) {
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			double f = x.evaluateCost();
			// If the current solution is the best, save it!
			if(x.evaluateCost() < best.evaluateCost()) {
				best = new Solution(x);
				System.out.println("NEW BEST\t" + best.evaluateCost() + "\t remaining: " + Duration.between(LocalTime.now(), endTime) + " s");
				Data.getInstance().saveSolution(best);
			}
			ls.deepOptimization(x, 0.1);

			//Data neigh = neigh.get(rand.nextInt(neighPop));
			Solution neigh = x.createNeighbor(0.321);
			ls.deepOptimization(neigh, 0.1);
			double newF = neigh.evaluateCost();

			// If the solution found is bad, instead of discarding them, use it as current solution randomly (following the probability in the method PR)
			if(newF > f) {
				// Calculate probability p_ with method PR and probability randomly
				double p_ = PR(f, newF, temperature);
				double p = Math.random();
				// if p_ > p discard the solution, otherwise take it!
				//noinspection StatementWithEmptyBody
				if(p_ > p) {
					System.out.println("Discarded" + "\t" + newF + " (probability " + String.format("%4.2f > %4.2f)", p_, p));
//                    System.out.println("No");
				} else {
					System.out.println("Prob new!" + "\t" + newF);
					x = new Solution(neigh);
				}
			} // If the solution found is good take it!
			else {
				System.out.println("Found new!" + "\t" + newF);
				x = new Solution(neigh);
			}

			// Lower the temperature
			Double coolingRate = (double) toEnd.toMillis() / (double) total.toMillis();
			temperature = initialTemperature * coolingRate;
			//System.out.println("Temperature: " + temperature);
		}
		System.out.println("Initial solution: " + startSol.evaluateCost());
		System.out.println("Final solution: " + best.evaluateCost());
		return best;
	}


	/**
	 * Calculate exponential probability starting from the evalutation of the two solution and the current temperature
	 */
	private double PR(double f1, double f2, double t) {
		return Math.exp(-(f2 - f1) / t);
	}
}
