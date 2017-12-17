package it.polito.studenti.oma9;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Main {

	public static void main(String[] args) {
		Temporal start = LocalTime.now();
		Temporal endTime;
		LS ls = new LS();
		SA optimization = new SA();
		Data data;
		boolean secondsIsNext = false;
		int seconds = 0;
		String instance = "";

		// Initialization of the arguments
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

			// Initialization of a new object Data and creation of the FFS
			data = new Data(instance);
		} catch(FileNotFoundException e) {
			System.out.println("Missing files for instance " + instance);
			return;
			//e.printStackTrace();
		}

		data.createSolution();
		System.out.println("Initial solution: " + data.evaluateSolution());
		Double delta = data.evaluateSolution();
		ls.deepOptimization(data, 0.01);
		System.out.println("LS solution: " + data.evaluateSolution());
		delta -= data.evaluateSolution();


		endTime = start.plus(seconds, ChronoUnit.SECONDS);

		// Data x is an object that the program uses to write the solution of the Simulated Annealing
		Data x;
		try {
			// To see information of this method go to the implementation
			// 0.69 obtained from logaritmo (TODO: spiegare 'sta cosa)
			x = optimization.startOptimization(data, delta / 0.69, start, endTime);
			// Print of the solution
			x.printSolution();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
