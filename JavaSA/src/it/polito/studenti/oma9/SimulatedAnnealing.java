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
		int noImprovement = 0;

		//System.out.println(Thread.currentThread().getName() + " started SA with: " + initial.solutionCost());
		Solution current = new Solution(initial);

		// Until the end of time
		while(!(toEnd = Duration.between(LocalTime.now(), endTime)).isNegative()) {
			// To optimize more as time goes on
			//relativeTemperature = Math.sqrt(temperature / initialTemperature); // TODO: explain the sqrt thing
			relativeTemperature = temperature / initialTemperature;
			//System.out.printf(Thread.currentThread().getName() + " relative temperature: %4.2f\n", relativeTemperature);

			// Optimize current solution using local search
			// TODO: se prende una soluzione peggiore, che è già ultra-ottimizzata, al giro successivo rifà almeno 1 passo di LS senza senso qui (senza senso perché il miglioramento richiesto è più alto)
			LocalSearch.optimize(current, 0.1 * relativeTemperature); // TODO: explain 0.1 (10%), even though it's random
			if(Data.getInstance().compareAndUpdateBest(current)) noImprovement = 0; else noImprovement++;

			// Then create a neighbor and optimize it
			Solution neighbor = current.createNeighbor(0.22); // TODO: explain 0.2 which was 0.3 (~1/3)
			LocalSearch.optimize(neighbor, 0.1 * relativeTemperature); // TODO: explain 0.03 (3%)
			if(!Data.getInstance().compareAndUpdateBest(neighbor)) noImprovement = 0; else noImprovement++;

			// Is it an improvement over current (thread-local) solution?
			if(neighbor.solutionCost() < current.solutionCost()) {
				// It's better, take it
				current = neighbor;
			} else {
				// It's worse, but don't discard it yet, calculate probability and a random number instead
				double probability = probability(current.solutionCost(), neighbor.solutionCost(), temperature);
				double random = rng.nextDouble();
				// Probability decreases from 1 to 0 as time goes on, so if random number is less than probability take it!
				//noinspection StatementWithEmptyBody
				if(random < probability) {
					//System.out.println(Thread.currentThread().getName() + " accepted worse solution\t" + neighbor.solutionCost() + "\t(got " + String.format("%4.2f < %4.2f)", random, probability));
					current = neighbor;
				} else {
					// TODO: rimuovere questa parte?
					double restartProbability = probability(Data.getInstance().getBest(), current.solutionCost(), temperature);
					random = rng.nextDouble();
					if(random * (10.0 / noImprovement) < restartProbability) {
						current = new Solution();
						LocalSearch.optimize(current, 0.05 * relativeTemperature);
						noImprovement = 0;
						System.out.printf(Thread.currentThread().getName() + " restarting with %.6f after %d non-improving solutions (got %4.2f < %4.2f)\n", current.solutionCost(), noImprovement, random, restartProbability);
					}
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
	 * Calculate exponential probability starting from the evaluation of the two solution and the current temperature
	 */
	private static double probability(double currentCost, double neighborCost, double temperature) {
		return Math.exp((currentCost - neighborCost) / temperature);
	}
}
