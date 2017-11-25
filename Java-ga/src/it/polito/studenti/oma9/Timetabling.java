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
	private static int[][] conflicts;

	Timetabling(int S, int e, int ts, int[][] conflicts) {
		this.S = S;
		this.e = e;
		this.ts = ts;
		Timetabling.conflicts = conflicts; // questo è LO SCHIFO, la classe è un singleton forzosamente ma non so che farci
	}

	public Function<ISeq<IntegerGene>, Double> fitness() {
		return schedule -> {
			int distance;

			// calcola la penalità
			double penalty = 0;
			for(int i = 0; i < this.e; i++) {
				for(int j = 0; j < i; j++) {
					// se 2 esami sono in conflitto
					if(Timetabling.conflicts[i][j] > 0) {
						// calcola la distanza...
						distance = Math.abs(schedule.get(i).intValue() - schedule.get(j).intValue());
						if(distance <= 5) {
							// ...che non può essere 0 altrimenti il valdiatore sta sbagliando tutto
							assert (distance > 0);
							// calcola la penalità
							penalty += Math.pow(2.0, 5.0 - (double) distance) * (double) conflicts[i][j];
						}
					}
				}
			}

			//System.out.println("Penalty: " + (penalty / (double) this.S));
			return penalty / (double) this.S;
		};
	}

	// la documentazione di questa parte FA SCHIFO, è contraddittoria e caotica e sull'orlo dell'inesistenza,
	// ci sono voluti 20 tentativi prima di trovare qualcosa di funzionante
	public Codec<ISeq<IntegerGene>, IntegerGene> codec() {
		return Codec.of(
				Genotype.of(IntegerChromosome.of(1, this.ts, this.e)),
				gt -> gt.getChromosome().toSeq()
		);
	}

	static boolean validator(Genotype<IntegerGene> gt) {
		Map<Integer, Set<Integer>> TimeslotConflicts = new TreeMap<>();
		ISeq<IntegerGene> genes = gt.getChromosome().toSeq();

		int len = genes.length();

		for(int exam = 0; exam < len; exam++) {
			Integer timeslot = genes.get(exam).intValue();

			if(!TimeslotConflicts.containsKey(timeslot)) {
				TimeslotConflicts.put(timeslot, new TreeSet<>());
			}

			Set<Integer> sovrapposti = TimeslotConflicts.get(timeslot);

			for(int eprime : sovrapposti) {
				assert(eprime != exam);
				if(conflicts[exam][eprime] > 0) {
					//System.out.println("Invalid: " + gt);
					return false;
				}
			}

			sovrapposti.add(exam);
		}
		//System.out.println("OK: " + gt);
		return true;
	}
}
