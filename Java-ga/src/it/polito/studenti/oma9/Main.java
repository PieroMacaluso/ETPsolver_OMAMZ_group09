package it.polito.studenti.oma9;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import io.jenetics.*;
import io.jenetics.engine.*;

public class Main {

	public static void main(String[] args) {
		int S = 8;
		int e = 4;
		int ts = 6;
		int[][] conflicts = new int[e][e];

		// si fa -1 di tutti gli indici, gli esami devono partire da 0
		conflicts[0][1] = 2;
		conflicts[1][0] = 2;
		conflicts[0][2] = 2;
		conflicts[2][0] = 2;
		conflicts[1][2] = 3;
		conflicts[2][1] = 3;

		Timetabling problem = new Timetabling(S, e, ts, conflicts);

//	public static void main(final String[] args) {
		final Engine<IntegerGene, Double> engine = Engine.builder(problem)
				.optimize(Optimize.MINIMUM) // minimizza la fitness function (funzione obiettivo)
				.genotypeValidator(Timetabling::validator) // valuta feasibility delle soluzioni
				//.populationSize(500)
				//.survivorsSelector(new StochasticUniversalSelector<>())
				//.offspringSelector(new TournamentSelector<>(5))
				.alterers(
						//new Mutator<>(0.1), // se volete mutazioni...
						new SinglePointCrossover<>(0.5))
				.build();

		final Phenotype<IntegerGene, Double> result = engine.stream()
				.limit(10) // numero di iterazioni
				.collect(toBestPhenotype());

		System.out.println(result);
	}
}