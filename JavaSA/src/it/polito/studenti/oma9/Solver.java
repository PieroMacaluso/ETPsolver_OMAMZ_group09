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
		feasible.createSolution();

		// TODO: do we need a local search right at the beginning?
		Solution betterNeighbor = new Solution(feasible);
		LocalSearch.optimize(betterNeighbor, 0.01);

		System.out.println();
		System.out.println(Thread.currentThread().getName() + " Initial solution: " + feasible.solutionCost());
		System.out.println(Thread.currentThread().getName() + " LS solution: " + betterNeighbor.solutionCost());
		double delta = feasible.solutionCost() - betterNeighbor.solutionCost();

		// 0.69 obtained from logaritmo (TODO: spiegare 'sta cosa)
		SimulatedAnnealing.optimize(betterNeighbor, delta / 0.69, endTime);
	}
}
