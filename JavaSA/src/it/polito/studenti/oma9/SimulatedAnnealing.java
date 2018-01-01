package it.polito.studenti.oma9;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;
import java.util.concurrent.ThreadLocalRandom;

class SimulatedAnnealing {
	/**
	 * An implementation of Simulated Annealing
	 *
	 * @param initial            a feasible solution
	 * @param initialTemperature starting temperature of Simulated Annealing
	 */
	@SuppressWarnings("SameParameterValue")
	static void optimize(Solution initial, double initialTemperature, Temporal endTime) {
		double temperature = initialTemperature;
		Temporal startTime = LocalTime.now();
		Duration toEnd;
		Duration total = Duration.between(startTime, endTime);
		ThreadLocalRandom rng = ThreadLocalRandom.current();

		System.out.println(Thread.currentThread().getName() + " started SA with: " + initial.solutionCost());
		Solution current = new Solution(initial);

//		for(int i = 0; i < numberOfIterations; i++) {
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			// Check if current solution is better (and save it)
			//boolean better = Data.getInstance().compareAndUpdateBest(current);
			Data.getInstance().compareAndUpdateBest(current);
//			if(better) {
//				System.out.println("NEW BEST\t" + cost + "\t remaining: " + Duration.between(LocalTime.now(), endTime) + " s");
//			}
			LocalSearch.optimize(current, 0.1); // TODO: BUT WHY? Compare to best again, maybe?

			Solution neighbor = current.createNeighbor(0.3); // TODO: explain 0.3
			LocalSearch.optimize(neighbor, 0.1);

			if(Data.getInstance().compareAndUpdateBest(current)) {
				// It's better
				current = new Solution(neighbor);
			} else {
				// It's worse, but don't discard it yet: use it as current solution randomly (following the probability in the method PR)
				// Calculate probability p_ with method PR and probability randomly
				double p_ = PR(current.solutionCost(), neighbor.solutionCost(), temperature);
				double p = rng.nextDouble();
				// if p_ > p discard the solution, otherwise take it!
				//noinspection StatementWithEmptyBody
				if(p_ > p) {
					System.out.println("Discarded" + "\t" + neighbor.solutionCost() + " (probability " + String.format("%4.2f > %4.2f)", p_, p));
//                    System.out.println("No");
				} else {
					System.out.println("Prob new!" + "\t" + neighbor.solutionCost());
					current = new Solution(neighbor);
				}
			}

			// Lower the temperature
			Double coolingRate = (double) toEnd.toMillis() / (double) total.toMillis();
			temperature = initialTemperature * coolingRate;
			//System.out.println("Temperature: " + temperature);
		}
		// To see where we started when at the end
		//System.out.println(Thread.currentThread().getName() + " Initial solution: " + initial.solutionCost());
	}


	/**
	 * Calculate exponential probability starting from the evalutation of the two solution and the current temperature
	 */
	private static double PR(double f1, double f2, double t) {
		return Math.exp(-(f2 - f1) / t);
	}
}
