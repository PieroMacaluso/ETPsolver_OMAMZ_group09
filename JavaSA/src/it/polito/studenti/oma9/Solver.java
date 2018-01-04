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
		double unoptimizedCost;
		double delta;

		// Build a feasible solution and save its cost
		Solution initial = new Solution();
		unoptimizedCost = initial.solutionCost();
		// Optimize with local search
		LocalSearch.optimize(initial, 0.1);

		// Save it, as a starting point
		Data.getInstance().compareAndUpdateBest(initial);

//		System.out.printf(Thread.currentThread().getName() + " Initial solution: %.6f\n              LS solution: %.6f\n", unoptimizedCost, initial.solutionCost());

		// Calculate delta and start SA. 0.69 obtained from logaritmo (TODO: spiegare 'sta cosa)
		delta = unoptimizedCost - initial.solutionCost();
		SimulatedAnnealing.optimize(initial, delta / 0.69, endTime);
	}
}
