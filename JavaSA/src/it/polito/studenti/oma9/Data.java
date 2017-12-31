package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//import java.time.Duration;
//import java.time.LocalTime;

class Data {
	private static Data instance;
	private double best = Double.MAX_VALUE;
	final int nExm;
	final int nStu;
	final int nSlo;
	private final Map<Integer, Student> students = new HashMap<>();
	private final Map<Integer, Exam> exams = new HashMap<>();
	private int[][] conflicts;
	private final File solutionFile;

	/**
	 * Create object representing problem data.
	 *
	 * @throws FileNotFoundException If any file for the instance doesn't exist
	 */
	Data(String filename) throws FileNotFoundException {
		Data.instance = this;

		File stuFile = new File(filename + ".stu");
		File exmFile = new File(filename + ".exm");
		File sloFile = new File(filename + ".slo");
		solutionFile = new File(filename + ".sol");
		Scanner sStu = new Scanner(stuFile);
		Scanner sExm = new Scanner(exmFile);
		Scanner sSlo = new Scanner(sloFile);

		nSlo = readSlots(sSlo);
		nExm = readExams(sExm);
		nStu = readStudents(sStu);

		buildConflicts();
	}

	Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Access the singleton.
	 *
	 * @return instance
	 */
	static Data getInstance() {
		return instance;
	}

	/**
	 * Read .exm file to find number of exams and build data structures
	 *
	 * @param sExm Scanner
	 */
	private int readExams(Scanner sExm) {
		int exmID;
		int maxID = 0;
		while(sExm.hasNextLine()) {
			String line = sExm.nextLine();
			if(line.isEmpty()) {
				continue;
			}
			String part[] = line.split(" ");
			exmID = Integer.parseInt(part[0]);
			exams.put(exmID, new Exam(exmID));
			if(exmID > maxID) {
				maxID = Integer.parseInt(part[0]);
			}
		}
		return maxID;
	}

	/**
	 * Read .stu file
	 *
	 * @param sStu Scanner
	 * @return number of students
	 */
	private int readStudents(Scanner sStu) {
		Integer studentID;
		Integer examID;

		while(sStu.hasNextLine()) {
			String line = sStu.nextLine();
			if(line.isEmpty()) {
				continue;
			}
			if(line.startsWith("s")) {
				String part[] = line.substring(1).split(" ");
				studentID = Integer.parseInt(part[0]);
				examID = Integer.parseInt(part[1]);
				Student student;
				if(!students.containsKey(studentID)) {
					student = new Student(studentID);
					students.put(studentID, student);
				} else {
					student = students.get(studentID);
				}
				Exam exam = exams.get(examID);
				student.addExam(exam);
			}
		}
		return students.size();
	}

	/**
	 * Read .slo file
	 *
	 * @param sSlo Scanner
	 * @return number of slots
	 */
	private int readSlots(Scanner sSlo) {
		String line = sSlo.nextLine();
		return Integer.parseInt(line);
	}


	/**
	 * Build conflicts map.
	 */
	private void buildConflicts() {
		//LocalTime start = LocalTime.now();
		//System.out.println("Begin building conflict map (new method)");
		conflicts = new int[nExm + 1][nExm + 1];

		for(Student student : students.values()) {
			for(Exam exam : student.getExams().values()) {
				for(Exam other : student.getExams().values()) {
					if(exam.compareTo(other) < 0) {
						exam.addConflict(other);
						other.addConflict(exam);
						conflicts[exam.id][other.id]++;
						conflicts[other.id][exam.id]++;
						//System.out.println(exam.id + " and " + other.id + " conflict by " + conflicts[exam.id][other.id]);
					}
				}
			}
		}

		//int total = 0;
//		for(int i = 1; i <= nExm; i++) {
//			for(int j = i + 1; j <= nExm; j++) {
//				if(conflicts[i][j] != 0) {
//					Exam one = this.exams.get(i);
//					Exam two = this.exams.get(j);
//					Integer numberOfConflicts = conflicts[i][j];
//					one.setConflictCounter(two, numberOfConflicts);
//					two.setConflictCounter(one, numberOfConflicts);
//					//total += 2;
//				}
//			}
//		}

		//System.out.println("Finished building conflict map, " + total + " conflicting exam couples, took: " + Duration.between(start, LocalTime.now()));
	}

	int conflictsBetween(Exam one, Exam two) {
		return conflicts[one.id][two.id];
	}

	int conflictsBetween(Integer one, Integer two) {
		return conflicts[one][two];
	}

	int conflictsBetween(int one, int two) {
		return conflicts[one][two];
	}

	/**
	 * Is this solution better than the best?
	 * If yes, save it to file.
	 *
	 * This calls a synchronized function internally, so for all intents and purposes it's synchronized.
	 *
	 * @return true if new solution is better
	 */
	boolean compareAndUpdateBest(Solution candidate) {
		double cost = candidate.solutionCost();
		return compareAndUpdateBestSynchronized(cost, candidate);
	}

	/**
	 * Use for debugging and printing messages ONLY
	 */
	double getBest() {
		return this.best;
	}

	/**
	 * @see Data#compareAndUpdateBest(Solution)
	 */
	private synchronized boolean compareAndUpdateBestSynchronized(double cost, Solution candidate) {
		if(cost >= this.best) {
			return false;
		} else {
			this.best = cost;
			saveSolution(candidate);
			return true;
		}
	}

	/**
	 * Print solution to file
	 */
	private synchronized void saveSolution(Solution solution) {
		try {
			PrintWriter writer = new PrintWriter(solutionFile, "UTF-8");
			for(Map.Entry<Exam, Integer> e : solution.export()) {
				writer.println(e.getKey().id + " " + e.getValue());

				//System.out.println(e.getKey() + " " + e.getValue().getTimeslot());

			}
			writer.close();
		} catch(IOException e) {
			System.err.println("Cannot write solution on " + solutionFile.getName() + ".sol");
			throw new RuntimeException();
		}

	}
}

