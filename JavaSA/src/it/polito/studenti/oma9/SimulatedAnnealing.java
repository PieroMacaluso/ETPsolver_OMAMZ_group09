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
		double relativeTemperature;
		double currentCost, neighborCost;
		Data data = Data.getInstance();

		//System.out.println(Thread.currentThread().getName() + " started SA with: " + initial.solutionCost());
		Solution current = initial;

		// Until the end of time
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			// To optimize more as time goes on
			relativeTemperature = temperature / initialTemperature;
			//System.out.printf(Thread.currentThread().getName() + " relative temperature: %4.2f\n", relativeTemperature);

			// Update current cost
			currentCost = current.solutionCost();

			// Then create a neighbor and optimize it
			// Using relativeTemperature gives consistently better solutions in instance 4 by around 1%, from limited testing
			Solution neighbor = current.createNeighbor(0.3 * relativeTemperature);
			neighbor = TimetableSwap.optimize(neighbor);
			LocalSearch.optimize(neighbor, 0.1 * relativeTemperature);
			neighborCost = neighbor.solutionCost();

			// Is it an improvement over current (thread-local) solution?
			if(neighborCost < currentCost) {
				// It's better, take it
				current = neighbor;
				data.compareAndUpdateBest(current);
			} else {
				// It's worse, but don't discard it yet, calculate probability and a random number instead
				double probability = probability(currentCost, neighborCost, temperature);
				double random = rng.nextDouble();
				// Probability decreases from 1 to 0 as time goes on, so if random number is less than probability take it!
				//noinspection StatementWithEmptyBody
				if(random < probability) {
					//System.out.println(Thread.currentThread().getName() + " accepted worse solution\t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f < %4.2f)", random, probability));
					current = neighbor;
				} else {
					//System.out.println(Thread.currentThread().getName() + " discarded              \t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f > %4.2f)", random, probability));
				}
			}

			// Lower the temperature
			double coolingRate = (double) toEnd.toMillis() / totalDuration;
			temperature = initialTemperature * coolingRate;
			//System.out.println(Thread.currentThread().getName() + " temperature: " + temperature);
		}
	}


	/**
	 * Calculate exponential probability from cost of current solutions, and current temperature
	 */
	private static double probability(double currentCost, double neighborCost, double temperature) {
		return Math.exp((currentCost - neighborCost) / temperature);
	}
}
