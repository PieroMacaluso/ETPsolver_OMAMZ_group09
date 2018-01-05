package it.polito.studenti.oma9;

import java.util.*;

class LocalSearch {

	private LocalSearch() {
	}

	/**
	 * Optimize solution while delta between previous and obtained solution is above supplied minimum delta
	 *
	 * @param sol   start from there
	 * @param delta minimum delta, keep improving only if improvement is greater than this
	 */
	@SuppressWarnings("SameParameterValue")
	static void optimize(Solution sol, double delta) {
		double next = sol.solutionCost();
		double prev;
		//int i = 1;
		do {
			prev = next;
			next = optimizeOnce(sol);
			//if(i > 1) System.out.printf(Thread.currentThread().getName() + " LS step %-2d improvement:\t%4.2f%%,\trequired:\t%4.2f%%\n", i, 100 * (prev - next) / prev, delta * 100);
			//i++;
		} while((prev - next) / prev > delta);
	}

	/**
	 * Try to place each exam in a better timeslot, if it exists
	 *
	 * @param sol current solution
	 * @return cost of optimized solution
	 */
	private static double optimizeOnce(Solution sol) {
		// For each exam
		for(Exam exam : Data.getInstance().getExamsByConflicts()) {
			// Get its timeslot
			Integer initialSlot = sol.getTimeslot(exam);
			// Assume its current slot is the best one, until proven wrong
			Integer bestSlot = initialSlot;
			double bestCost = sol.examCost(exam);

			// Where else could it be placed?
			Set<Integer> available = sol.getAvailableTimeslots(exam);
			for(Integer newSlot : available) {
				// How much would that exam cost, if placed there?
				double newCost = sol.examCostSlot(exam, newSlot);
				// Is that an improvement?
				if(newCost < bestCost) {
					// Mark it as best
					bestSlot = newSlot;
					bestCost = newCost;
				}
			}
			// If a better slot was found, move it there
			if(!initialSlot.equals(bestSlot)) {
				sol.unschedule(exam);
				sol.schedule(exam, bestSlot);
			}
		}

		return sol.solutionCost();
	}
}
