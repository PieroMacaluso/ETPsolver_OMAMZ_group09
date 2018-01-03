package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

class Data {
	private static Data instance;
	private double best = Double.POSITIVE_INFINITY;
	static int nExm;
	static int nStu;
	static int nSlo;
	private final File solutionFile;
	private final Map<Integer, Student> students = new HashMap<>();
	private final Map<Integer, Exam> exams = new HashMap<>();
	private final List<Exam> examsByConflicts;
	private int[][] conflicts;

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

		examsByConflicts =  new ArrayList<>(nExm + 1);
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

		examsByConflicts.addAll(exams.values());
		examsByConflicts.sort(Comparator.comparing(Exam::nConflictingExams).reversed());
	}

	/**
	 * Number of conflicting students between exams
	 *
	 * @param one exam
	 * @param two exam
	 * @return number of conflicting students
	 */
	int conflictsBetween(Exam one, Exam two) {
		return conflicts[one.id][two.id];
	}

	/**
	 * Is this solution better than the best?
	 * If yes, save it to file.
	 * <p>
	 * This calls a synchronized function internally, so for all intents and purposes it's synchronized.
	 *
	 * @return true if new solution is better
	 */
	boolean compareAndUpdateBest(Solution candidate) {
		double cost = candidate.solutionCost();
		//noinspection SimplifiableIfStatement
		if(cost >= this.best) {
			return false;
		} else {
			return compareAndUpdateBestSynchronized(cost, candidate);
		}
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
		//System.out.println(Thread.currentThread().getName() + " entering critical section");
		if(cost >= this.best) {
			//System.out.println(Thread.currentThread().getName() + " leaving critical section (worse)");
			return false;
		} else {
			System.out.printf(Thread.currentThread().getName() + " found a new best: %.6f < %.6f\n", cost, this.best);
			this.best = cost;
			saveSolution(candidate);
			//System.out.println(Thread.currentThread().getName() + " leaving critical section (NEW BEST)");
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

	List<Exam> getExamsByConflicts() {
		return examsByConflicts;
	}
}

