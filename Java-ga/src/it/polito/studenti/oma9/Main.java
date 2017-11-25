package it.polito.studenti.oma9;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import java.util.stream.IntStream;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.CharSeq;

public class Main {

	public static void main(String[] args) {
		int S = 8;
		int e = 4;
		int ts = 6;
		int[][] conflicts = new int[e][e];

		conflicts[1][2] = 2;
		conflicts[2][1] = 2;
		conflicts[1][3] = 2;
		conflicts[3][1] = 2;
		conflicts[2][3] = 2;
		conflicts[3][2] = 2;

		Timetabling problem = new Timetabling(S, e, ts, conflicts);

		// Roba presa dagli esempi per avere una vaga idea di come funziona l'intero trabiccolo:

//		final Problem<CharSequence, CharacterGene, Integer> PROBLEM =
//				Problem.of(
//						seq -> IntStream.range(0, TARGET_STRING.length())
//								.map(i -> seq.charAt(i) == TARGET_STRING.charAt(i) ? 1 : 0)
//								.sum(),
//						Codec.of(
//								Genotype.of(new CharacterChromosome(
//										CharSeq.of("a-z"), TARGET_STRING.length()
//								)),
//								gt -> (CharSequence) gt.getChromosome()
//						)
//				);
	}

//	public static void main(final String[] args) {
//		final Engine<CharacterGene, Integer> engine = Engine.builder(PROBLEM)
//				.populationSize(500)
//				.survivorsSelector(new StochasticUniversalSelector<>())
//				.offspringSelector(new TournamentSelector<>(5))
//				.alterers(
//						new Mutator<>(0.1),
//						new SinglePointCrossover<>(0.5))
//				.build();
//
//		final Phenotype<CharacterGene, Integer> result = engine.stream()
//				.limit(100)
//				.collect(toBestPhenotype());
//
//		System.out.println(result);
//	}

}