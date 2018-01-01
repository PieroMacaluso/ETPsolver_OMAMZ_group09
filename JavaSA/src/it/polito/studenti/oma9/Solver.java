package it.polito.studenti.oma9;

import java.time.temporal.Temporal;

public class Solver implements Runnable {
	// This is needed because run() cannot accept parameters
	private final Temporal endTime;

	/**
	 * Create an initial solution and optimize until the end of time
	 */
	Solver(Temporal endTime) {
		this.endTime = endTime;
	}

	@Override
	public void run() {
		Solution feasible = new Solution();

		// TODO: do we need a local search right at the beginning?
		Solution optimized = new Solution(feasible);
		LocalSearch.optimize(optimized, 0.05);

		System.out.println(Thread.currentThread().getName() + " Initial solution:\t" + feasible.solutionCost() + "\n" +
				"         LS solution:\t" + optimized.solutionCost());
		double delta = feasible.solutionCost() - optimized.solutionCost();

		// 0.69 obtained from logaritmo (TODO: spiegare 'sta cosa)
		SimulatedAnnealing.optimize(optimized, delta / 0.69, endTime);
	}
}
