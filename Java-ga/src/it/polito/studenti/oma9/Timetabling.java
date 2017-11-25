package it.polito.studenti.oma9;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

public class Timetabling implements Problem<ISeq<IntegerGene>, IntegerGene, Double> {
	private int S;
	private int e;
	private int ts;
	private int[][] conflicts;

	Timetabling(int S, int e, int ts, int[][] conflicts) {
		this.S = S;
		this.e = e;
		this.ts = ts;
		this.conflicts = conflicts;
	}

	public Function<ISeq<IntegerGene>, Double> fitness() {
		return schedule -> {
			int distance;

			// calcola la penalità
			double penalty = 0;
			for(int i = 0; i < this.e; i++) {
				for(int j = 0; j < i; j++) {
					// se 2 esami sono in conflitto
					if(this.conflicts[i][j] > 0) {
						// calcola la distanza...
						distance = Math.abs(schedule.get(i).intValue() - schedule.get(j).intValue());
						if(distance <= 5) {
							// ...che non può essere 0 altrimenti il valdiatore sta sbagliando tutto
							assert (distance > 0);
							// calcola la penalità
							penalty += Math.pow(2.0, 5.0 - (double) distance) * (double) this.conflicts[i][j];
						}
					}
				}
			}

			return penalty / (double) this.S;

			// Questo controllava la feasibility della soluzione, costruendo un elenco di esami per ogni timeslot e guardando se erano in conflitto
//            for(exam = 0; feasible && exam < len; exam++) {
//                if(!TimeslotConflicts.containsKey(exam)) {
//                    TimeslotConflicts.put(exam, new TreeSet<>());
//                }
//            }
//                TimeslotConflicts.get(exam).add(schedule[exam]);
//                for(int eprime : TimeslotConflicts.get(exam)) {
//                    if(eprime >= exam) {
//                        break;
//                    }
//                    if(this.conflicts[e][eprime] > 0) {
//                        feasible = false;
//                        break;
//                    }
//                }
//            }
//            if(!feasible) {
//                return Double.POSITIVE_INFINITY;
//            }
		};
	}

	// la documentazione di questa parte FA SCHIFO, è contraddittoria e caotica e sull'orlo dell'inesistenza, ci sono voluti 20 tentativi prima di trovare qualcosa di funzionante
	public Codec<ISeq<IntegerGene>, IntegerGene> codec() {
		// Genotype.of(new IntegerChromosome(0, this.ts, this.e);
		// return Codecs.ofVector(IntRange.of(0, this.ts), this.e);
		return Codec.of(
				Genotype.of(IntegerChromosome.of(1, this.ts, this.e)),
				gt -> gt.getChromosome().toSeq()
		);
	}

	static boolean validator(Genotype<IntegerGene> gt) {
		boolean feasible;
		Map<Integer, Set<Integer>> TimeslotConflicts = new TreeMap<>();
		ISeq<IntegerGene> genes = gt.getChromosome().toSeq();

		int len = genes.length();

		for(int exam = 0; exam < len; exam++) {
			if(!TimeslotConflicts.containsKey(exam)) {
				TimeslotConflicts.put(exam, new TreeSet<>());
			}
		}
		return true;
	}
}
