package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.*;

class LS implements Serializable {

	void deepOptimization(Data data, double delta) {
		double next = data.evaluateSolution();
		double prev = Double.MAX_VALUE;
		int i = 0;
		while((prev - next) / prev > delta) {
			prev = next;
			next = startOptimization(data);
//			System.out.println("Ottimizzazione pari a: " + 100 * (prev - next) / prev + "%");
			i++;
		}
//		System.out.println("" + i + " giri di LS compiuti con gaudio");
	}

	private double startOptimization(Data ffs) {
		Integer bestSlo;
		for(Map.Entry en : entriesSortedByValues(ffs.getExams())) {
			Exam e = (Exam) en.getValue();
			bestSlo = e.getTimeslot();
			Set<Integer> sloA = e.timeslotAvailable();
//			System.out.println("Ho " + sloA.size() + " buchi a disposizione");
			double bestC = e.examCost(ffs.nStu);
			for(Integer s : sloA) {
				e.unschedule();
				e.schedule(s);
				double newC = e.examCost(ffs.nStu);
				if(newC < bestC) {
					bestSlo = s;
					bestC = newC;
				}

			}
			e.unschedule();
			e.schedule(bestSlo);
		}
		return ffs.evaluateSolution();
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
