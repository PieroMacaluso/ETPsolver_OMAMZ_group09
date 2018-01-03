package it.polito.studenti.oma9;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		Temporal start = LocalTime.now();
		Temporal endTime;
		List<Thread> workers;
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

		endTime = start.plus(seconds, ChronoUnit.SECONDS);
		int cores = Runtime.getRuntime().availableProcessors();
		//int cores = 1;
		System.out.println("Running " + cores + " threads...");
		workers = new ArrayList<>(cores);
		for(int i = 0; i < cores; i++) {
			Thread worker = new Thread(new Solver(endTime));
			workers.add(i, worker);
			worker.start();
		}

		try {
			for(int i = 0; i < cores; i++) {
				// Workers of the world, unite!
				workers.get(i).join();
			}
		} catch(InterruptedException e) {
			System.out.println("Main thread interrupted, terminating...");
		}
		// Print final solution
		System.out.println("Final solution: " + Data.getInstance().getBest());
	}
}
