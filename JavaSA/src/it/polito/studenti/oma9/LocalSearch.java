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
	static void optimize(Solution sol, double delta) {
		double next = sol.solutionCost();
		double prev = Double.MAX_VALUE;
		//int i = 0;
		while((prev - next) / prev > delta) {
			prev = next;
			next = optimizeOnce(sol);
			//System.out.println("Ottimizzazione pari a: " + 100 * (prev - next) / prev + "%, richiesto " + delta * 100 + "%");
			//i++;
		}
		//System.out.println("" + i + " giri di LS compiuti con gaudio");
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
				sol.unschedule(e);
				sol.schedule(e, s);
				double newC = sol.examCost(e);
				if(newC < bestC) {
					bestSlo = s;
					bestC = newC;
				}

			}
			sol.unschedule(e);
			sol.schedule(e, bestSlo);
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

						return res != 0 ? res : 1;
					}
				}
		);
		sortedEntries.addAll(map.entrySet());
		return sortedEntries;
	}
}
