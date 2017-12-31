package it.polito.studenti.oma9;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.Temporal;

class SimulatedAnnealing implements Runnable {
	// This is needed because run() cannot accept parameters
	private final Solution initial;
	private final double initialTemperature;
	private final Temporal endTime;

	/**
	 * Prepare to run simulated annealing inside a thread
	 *
	 * @see SimulatedAnnealing#optimize optimize() for an explanation of parameters
	 */
	@SuppressWarnings("SameParameterValue")
	SimulatedAnnealing(Solution initial, double initialTemperature, Temporal endTime) {
		this.initial = initial;
		this.initialTemperature = initialTemperature;
		this.endTime = endTime;
	}

	/**
	 * An implementation of Simulated Annealing
	 *
	 * @param initial            a feasible solution
	 * @param initialTemperature starting temperature of Simulated Annealing
	 */
	private static void optimize(Solution initial, double initialTemperature, Temporal endTime) {
		double temperature = initialTemperature;
		Temporal startTime = LocalTime.now();
		Duration toEnd;
		Duration total = Duration.between(startTime, endTime);

		Solution x = new Solution(initial);
		System.out.println(Thread.currentThread().getName() + " Initial solution: " + initial.solutionCost());

//		for(int i = 0; i < numberOfIterations; i++) {
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			double cost = x.solutionCost();
			// Check if current solution is better (and save it)
			boolean better = Data.getInstance().compareAndUpdateBest(x);
			if(better) {
				System.out.println("NEW BEST\t" + cost + "\t remaining: " + Duration.between(LocalTime.now(), endTime) + " s");
			}
			LocalSearch.optimize(x, 0.1); // TODO: BUT WHY? Compare to best again, maybe?

			//Data neigh = neigh.get(rand.nextInt(neighPop));
			Solution neigh = x.createNeighbor(0.321);
			LocalSearch.optimize(neigh, 0.1);
			double newF = neigh.solutionCost();

			// If the solution found is bad, instead of discarding them, use it as current solution randomly (following the probability in the method PR)
			if(newF > cost) {
				// Calculate probability p_ with method PR and probability randomly
				double p_ = PR(cost, newF, temperature);
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
		System.out.println(Thread.currentThread().getName() + " Initial solution: " + initial.solutionCost());
	}


	/**
	 * Calculate exponential probability starting from the evalutation of the two solution and the current temperature
	 */
	private static double PR(double f1, double f2, double t) {
		return Math.exp(-(f2 - f1) / t);
	}

	@Override
	public void run() {
		SimulatedAnnealing.optimize(initial, initialTemperature, endTime);
	}
}
