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
			LocalSearch.optimize(current, 0.05);
			Data.getInstance().compareAndUpdateBest(current);
			//boolean better = Data.getInstance().compareAndUpdateBest(current);
//						if(better) {
//				System.out.println("NEW BEST\t" + cost + "\t remaining: " + Duration.between(LocalTime.now(), endTime) + " s");
//			}

			Solution neighbor = current.createNeighbor(0.3); // TODO: explain 0.3
			LocalSearch.optimize(neighbor, 0.05);
			Data.getInstance().compareAndUpdateBest(neighbor);

			if(neighbor.solutionCost() < current.solutionCost()) {
				// It's better
				current = new Solution(neighbor); // TODO: why cloning instead of assigning it directly?
			} else {
				// It's worse, but don't discard it yet: use it as current solution randomly
				double probability = probability(current.solutionCost(), neighbor.solutionCost(), temperature);
				double random = rng.nextDouble();
				// if probability > p discard the solution, otherwise take it!
				//noinspection StatementWithEmptyBody
				if(random < probability) {
					//System.out.println("Discarded" + "\t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f < %4.2f)", random, probability));
				} else {
					//System.out.println("Prob new!" + "\t" + neighbor.solutionCost());
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
	 * Calculate exponential probability starting from the evaluation of the two solution and the current temperature
	 */
	private static double probability(double currentCost, double neighborCost, double temperature) {
		//System.out.println("This: " + currentCost + " other: " + neighborCost);
		if(Math.exp(-(neighborCost - currentCost) / temperature) > 1.0) {
			System.out.println("Sta esplodendo tutto!");
			System.out.println("Best: " + currentCost + " other: " + neighborCost);
		}
		return Math.exp(-(neighborCost - currentCost) / temperature);
	}
}
