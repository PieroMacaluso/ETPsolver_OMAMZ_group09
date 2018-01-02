package it.polito.studenti.oma9;

import java.util.*;

class LocalSearch {

	private LocalSearch() {
	}

	/**
	 * Deep optimization.
	 *
	 * @param sol start from there
	 * @param delta keep improving only if improvement is greater than this
	 */
	@SuppressWarnings("SameParameterValue")
	static void optimize(Solution sol, double delta) {
		double next = sol.solutionCost();
		double prev = Double.MAX_VALUE;
		//int i = 1;
		while((prev - next) / prev > delta) {
			prev = next;
			next = optimizeOnce(sol);
			//if(i > 1) System.out.printf(Thread.currentThread().getName() + " LS step %-2d improvement:\t%4.2f%%,\trequired:\t%4.2f%%\n", i, 100 * (prev - next) / prev, delta * 100);
			//i++;
		}
	}

	private static double optimizeOnce(Solution sol) {
		Integer bestSlo;
		for(Map.Entry en : entriesSortedByValues(Data.getInstance().getExams())) {
			Exam e = (Exam) en.getValue();
			bestSlo = sol.getTimeslot(e);
			Set<Integer> sloA = sol.getAvailableTimeslots(e);
//			System.out.println("Ho " + sloA.size() + " buchi a disposizione");
			double bestC = sol.examCost(e);
			for(Integer s : sloA) {
				double newC = sol.examCostPrevision(e, s);
				if(newC < bestC) {
					bestSlo = s;
					bestC = newC;
				}
			}
			// TODO: check that it was scheduled
			if(bestSlo != bestC) {
				sol.unschedule(e);
				sol.schedule(e, bestSlo);
			}
		}
		return sol.solutionCost();
	}

	private static <K, V extends Comparable<? super V>>
	SortedSet<Map.Entry<K, V>> entriesSortedByValues(Map<K, V> map) {
		SortedSet<Map.Entry<K, V>> sortedEntries = new TreeSet<Map.Entry<K, V>>(
				new Comparator<Map.Entry<K, V>>() {
					@Override
					public int compare(Map.Entry<K, V> e1, Map.Entry<K, V> e2) {
						int res = e1.getValue().compareTo(e2.getValue());

						return res == 0 ? 1 : res;
					}
				}
		);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
