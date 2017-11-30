package it.polito.studenti.oma9;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Main {

	public static void main(String[] args) {
		Temporal start = LocalTime.now();
		Duration duration;
		Data data;
		boolean secondsIsNext = false;
		int seconds = 0;
		String instance = "";

		for(String arg : args) {
			if(secondsIsNext) {
				seconds = Integer.parseInt(arg);
				secondsIsNext = false;
			} else if(arg.equals("-t")) {
				secondsIsNext = true;
			} else {
				instance = arg;
			}
		}

		if(seconds <= 0) {
			System.out.println("Invalid number of seconds to run: " + seconds);
			return;
		}

		if(instance.length() == 0) {
			System.out.println("No instance name provided");
			return;
		}

		try {
			// dalla cartella Java-ga, si può invocare "questo-file.jar ../instances/instance01 -t 30"
			// (o mettere "../instances/instance01 -t 30" nei parametri della run configuration di Intellij...)
			// e va a prendere l'istanza nella cartella giusta
			data = new Data(instance);
		} catch(FileNotFoundException e) {
			System.out.println("Missing files for instance " + instance);
			return;
			//e.printStackTrace();
		}

		Timetabling problem = new Timetabling(data.nStu, data.nExm, data.nSlo, data.conflictTable);

		final Engine<IntegerGene, Double> engine = Engine.builder(problem)
				.optimize(Optimize.MINIMUM) // minimizza la fitness function (funzione obiettivo)
				.genotypeValidator(Timetabling::validator) // valuta feasibility delle soluzioni
				//.populationSize(500)
				.survivorsSelector(new StochasticUniversalSelector<>())
				.offspringSelector(new TournamentSelector<>(5))
				.alterers(
						new Mutator<>(0.1), // se volete mutazioni...
						new MultiPointCrossover<>(0.5, data.nExm/data.nSlo))
				.build();


		duration = Duration.between(LocalTime.now(), start.plus(seconds, ChronoUnit.SECONDS));
		System.out.println("Starting NOW, reminaing: " + duration);

		final EvolutionStatistics<Double, ?>
				statistics = EvolutionStatistics.ofNumber();

		final Phenotype<IntegerGene, Double> result = engine.stream()
				.limit(Limits.byExecutionTime(duration))
				//.limit(100) // numero di iterazioni
				.peek(statistics)
				.collect(toBestPhenotype());

		Timetabling.printResult(result.getGenotype().getChromosome().toSeq(), instance);
		System.out.println(result);
		System.out.println(statistics);
	}
}
