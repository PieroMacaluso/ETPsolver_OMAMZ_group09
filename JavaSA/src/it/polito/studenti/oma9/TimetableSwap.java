package it.polito.studenti.oma9;

import java.util.HashSet;
import java.util.Set;

class TimetableSwap {
	private TimetableSwap() {
	}

	/**
	 * Try to swap every couple of slots, do it if it improves the cost.
	 *
	 * @param sol current solution
	 * @return best solution
	 */
	static Solution optimize(Solution sol) {
		for(int i = 1; i <= Data.nSlo; i++) {
			for(int j = i + 1; j <= Data.nSlo; j++) {
				Solution newSol = new Solution(sol);
				swapTimeslots(newSol, i, j);
				if(newSol.solutionCost() < sol.solutionCost()) {
					sol = newSol;
				}
			}
		}
		return sol;
	}

	/**
	 * Swap two timeslots
	 */
	private static void swapTimeslots(Solution solution, Integer ts1, Integer ts2) {
		Set<Exam> examsTs1 = new HashSet<>(solution.getExamsInSlot(ts1));
		Set<Exam> examsTs2 = new HashSet<>(solution.getExamsInSlot(ts2));
		for(Exam exam : examsTs1)
			solution.unschedule(exam);
		for(Exam exam : examsTs2)
			solution.unschedule(exam);
		for(Exam exam : examsTs1)
			solution.schedule(exam, ts2);
		for(Exam exam : examsTs2)
			solution.schedule(exam, ts1);
	}
}
