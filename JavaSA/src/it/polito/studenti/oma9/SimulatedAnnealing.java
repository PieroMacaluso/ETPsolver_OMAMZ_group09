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
	 * @param initialTemperature starting temperature
	 */
	@SuppressWarnings("SameParameterValue")
	static void optimize(Solution initial, double initialTemperature, Temporal endTime) {
		Temporal startTime = LocalTime.now();
		Duration toEnd;
		ThreadLocalRandom rng = ThreadLocalRandom.current();
		double totalDuration = Duration.between(startTime, endTime).toMillis();
		double temperature = initialTemperature;

		System.out.println(Thread.currentThread().getName() + " started SA with: " + initial.solutionCost());
		Solution current = new Solution(initial);

		// Until the end of time
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			// Optimize current solution using local search
			LocalSearch.optimize(current, 0.05);
			Data.getInstance().compareAndUpdateBest(current);

			// Then create a neighbor and optimize it
			Solution neighbor = current.createNeighbor(0.3); // TODO: explain 0.3
			LocalSearch.optimize(neighbor, 0.05);
			Data.getInstance().compareAndUpdateBest(neighbor);

			// Is it an improvement over current (thread-local) solution?
			if(neighbor.solutionCost() < current.solutionCost()) {
				// It's better, take it
				current = neighbor;
			} else {
				// It's worse, but don't discard it yet, calculate probability and a random number instead
				double probability = probability(current.solutionCost(), neighbor.solutionCost(), temperature);
				double random = rng.nextDouble();
				// Probability decreases as time goes on, so if random number is less than probability take it!
				//noinspection StatementWithEmptyBody
				if(random < probability) {
					//System.out.println(Thread.currentThread().getName() + " discarded              \t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f < %4.2f)", random, probability));
				} else {
					//System.out.println(Thread.currentThread().getName() + " accepted worse solution\t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f > %4.2f)", random, probability));
					current = neighbor;
				}
			}

			// Lower the temperature
			double coolingRate = (double) toEnd.toMillis() / totalDuration;
			temperature = initialTemperature * coolingRate;
			//System.out.println(Thread.currentThread().getName() + " temperature: " + temperature);
		}
	}


	/**
	 * Calculate exponential probability starting from the evaluation of the two solution and the current temperature
	 */
	private static double probability(double currentCost, double neighborCost, double temperature) {
		return Math.exp(-(neighborCost - currentCost) / temperature);
	}
}
