package it.polito.studenti.oma9;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;

import io.jenetics.*;
import io.jenetics.engine.*;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class Main {

	public static void main(String[] args) {
		Temporal start = LocalTime.now();
		Temporal end;
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

		end = LocalTime.now().plus(seconds, ChronoUnit.SECONDS);

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
				//.survivorsSelector(new StochasticUniversalSelector<>())
				//.offspringSelector(new TournamentSelector<>(5))
				.alterers(
						//new Mutator<>(0.1), // se volete mutazioni...
						new SinglePointCrossover<>(0.5))
				.build();

		final Phenotype<IntegerGene, Double> result = engine.stream()
				.limit(Limits.byExecutionTime(Duration.between(start, end))) // numero di iterazioni
				.collect(toBestPhenotype());

		System.out.println(result);
	}
}
