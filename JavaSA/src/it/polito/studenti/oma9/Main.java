package it.polito.studenti.oma9;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Main {

	public static void main(String[] args) {
		Temporal start = LocalTime.now();
		Temporal endTime;
		int seconds = 0;
		String instance = "";

		// Read command line arguments
		boolean secondsIsNext = false;
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
			System.exit(1);
		}

		if(instance.length() == 0) {
			System.out.println("No instance name provided");
			System.exit(1);
		}

		try {
			// Load supplied data files. Data class is a singleton, accessible via a static method.
			new Data(instance);
		} catch(FileNotFoundException e) {
			System.out.println("Missing files for instance " + instance);
			System.exit(1);
		}

		// TODO: do we need a local search right at the beginning?
		Solution ffs = new Solution();
		ffs.createSolution();
		Solution fls = new Solution(ffs);
		LocalSearch.optimize(fls, 0.01);
		System.out.println("Initial solution: " + ffs.solutionCost());
		System.out.println("LS solution: " + fls.solutionCost());
		double delta = ffs.solutionCost() - fls.solutionCost();


		endTime = start.plus(seconds, ChronoUnit.SECONDS);

		try {
			// To see information of this method go to the implementation
			// 0.69 obtained from logaritmo (TODO: spiegare 'sta cosa)
			// TODO: actually run in a thread
			new SimulatedAnnealing(fls, delta / 0.69, endTime).run();
			// Print of the solution
			//x.printSolution(); // TODO: serviva?
			System.out.println("Final solution: " + Data.getInstance().getBest());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
