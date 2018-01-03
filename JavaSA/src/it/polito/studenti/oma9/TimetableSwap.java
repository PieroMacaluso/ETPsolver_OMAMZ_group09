package it.polito.studenti.oma9;

import java.util.HashSet;
import java.util.Set;

class TimetableSwap {
	private TimetableSwap() {
	}

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

	private static void swapTimeslots(Solution sol, Integer i, Integer j) {
		Set<Exam> exi = new HashSet<>(sol.getExamsInSlot(i));
		Set<Exam> exj = new HashSet<>(sol.getExamsInSlot(j));
		for(Exam e : exi)
			sol.unschedule(e);
		for(Exam e : exj)
			sol.unschedule(e);
		for(Exam e : exi)
			sol.schedule(e, j);
		for(Exam e : exj)
			sol.schedule(e, i);
	}
}
