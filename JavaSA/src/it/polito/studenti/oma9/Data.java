package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

class Data implements Serializable {

	private int nExm = 0;
	int nStu = 0;
	int nSlo = 0;
	private boolean hasFFS = false;
	private Map<Integer, Student> students = new TreeMap<>();
	private Map<Integer, Exam> exams = new TreeMap<>();
	private Random rand = new Random();
	private String filename;

	Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Create object representing problem data.
	 *
	 * @param filename Instance name, actually. Without extension. Could even be a path.
	 * @throws FileNotFoundException If any file for the instance doesn't exist
	 */
	Data(String filename) throws FileNotFoundException {
		this.filename = filename;
		//rand.setSeed(System.nanoTime()); // TODO: this is a bad seed™, something even MORE random should be used (e.g. /dev/random, which is not available on Windows)
		startRead();
	}

	/**
	 * Read .stu and .exm files and fill the appropriate matrices
	 */
	private void startRead() throws FileNotFoundException {
		File stuFile = new File(filename + ".stu");
		File exmFile = new File(filename + ".exm");
		File sloFile = new File(filename + ".slo");
		Scanner sStu = new Scanner(stuFile);
		Scanner sExm = new Scanner(exmFile);
		Scanner sSlo = new Scanner(sloFile);

		readSlots(sSlo);
		readExams(sExm);
//        conflictTable = new int[nExm][nExm];
		readStudents(sStu);

		// Creazione della tabella di conflitti con l'utilizzo delle Key delle mappe
		// Gli ID degli esami devono partire da 0 se no Jenetics esplode.
		// Si può modificare la funzione di validazione per farlo funzionare anche in quel modo, forse...
		// In ogni caso, se ci sono buchi esplode tutto malamente a causa dei .get()
		for(int i = 0; i < nExm; i++) {
			for(int j = i + 1; j < nExm; j++) {
				// è già inizializzata a 0
				// Se si vuole: assert(i < j); (magari non con assert visto che non si può usare...)
				for(Integer student1 : exams.get(i + 1).students.keySet()) {
					for(Integer student2 : exams.get(j + 1).students.keySet()) {
						if(student1.equals(student2)) {
//                            conflictTable[i][j]++;
//                            conflictTable[j][i]++;
							exams.get(i + 1).addConflict(exams.get(j + 1));
							exams.get(j + 1).addConflict(exams.get(i + 1));
						}
					}
				}
			}
		}


//		System.out.println("Exams: " + nExm + ", slots: " + nSlo + ", students: " + nStu + "\n\n");
//		// Stampa a video della tabella dei conflitti per verifica
//		for(int i = 0; i < nExm; i++) {
//			for(int j = 0; j < nExm; j++) {
//				System.out.print(conflictTable[i][j] + "\t");
//			}
//			System.out.print("\n");
//		}

//		 Stampa a video della tabella dei booleani per verifica
//		for(int i = 1; i <= nStu; i++) {
//			for(int j = 1; j <= nExm; j++) {
//				System.out.print(stuExm[i][j] + "\t");
//			}
//			System.out.print("\n");
//		}
	}

	/**
	 * Set the flag and create the solution
	 */
	void createSolution() {
		createFFS();
		hasFFS = true;
//        printSolution();
//        System.out.println(evaluateSolution());
	}

	/**
	 * Print the solution
	 */
	void printSolution() {
		for(Map.Entry<Integer, Exam> e : exams.entrySet()) {
			System.out.println(e.getKey() + " " + e.getValue().getTimeslot());

		}
	}

	/**
	 * Evaluate the cost of the current solution
	 *
	 * @return cost
	 */
	double evaluateSolution() {
		double sum = 0;
		if(hasFFS) {
			for(Map.Entry<Integer, Exam> e1 : exams.entrySet()) {
				for(Exam e2 : e1.getValue().exmConflict) {
					if(Math.abs(e2.getTimeslot() - e1.getValue().getTimeslot()) == 0) {
						System.out.println("Unfesible solution!! BAAAAAD");
						return Double.MAX_VALUE;

					}
					if(e2.getExmID() > e1.getKey() && Math.abs(e2.getTimeslot() - e1.getValue().getTimeslot()) < 6) {
						int d = Math.abs(e2.getTimeslot() - e1.getValue().getTimeslot());
						long nee = students.entrySet().stream().filter(s -> s.getValue().hasExam(e1.getKey())).filter(s -> s.getValue().hasExam(e2.getExmID())).collect(Collectors.toList()).size();
						sum += Math.pow(2, 5 - d) * nee;
					}
				}

			}
		}
		sum = sum / nStu;
		return sum;
	}


	/**
	 * Read .exm file to find number of exams and build data structures
	 *
	 * @param sExm Scanner
	 */
	private void readExams(Scanner sExm) {
		int exmID;
		int maxID = 0;
		while(sExm.hasNextLine()) {
			String line = sExm.nextLine();
			if(line.isEmpty()) {
				continue;
			}
			String part[] = line.split(" ");
			exmID = Integer.parseInt(part[0]);
			exams.put(exmID, new Exam(exmID, this));
			if(exmID > maxID) {
				maxID = Integer.parseInt(part[0]);
			}
		}
		nExm = maxID;
	}

	/**
	 * Read .stu file
	 *
	 * @param sStu Scanner
	 */
	private void readStudents(Scanner sStu) {
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
				Exam exam = this.exams.get(examID);
				student.addExam(exam);
				exam.addStudent(student);
			}
		}
		// È necessario il numero esatto, anche se ci sono buchi tra un ID e l'altro!
		nStu = students.size();
	}

	/**
	 * Read .slo file
	 *
	 * @param sSlo Scanner
	 */
	private void readSlots(Scanner sSlo) {
		String line = sSlo.nextLine();
		nSlo = Integer.parseInt(line);
	}

	/**
	 * FFS stands for "First Feasible Solution"
	 * C1 - timeslot available: priority to exam that has less timeslot available
	 * C2 - conflict with others: priority to exams with more conflict
	 * <p>
	 * If the code reaches a stuck point with one exam, unschedule all conflicting exams and continue the procedure.
	 */
	private void createFFS() {
		List<Exam> order;

		order = exams.values().stream().filter(ex -> !ex.isScheduled()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());

//        for (Exam e:order) {
//            System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " "+ e.nConflict());
//        }
		// TODO: use do-while?
		while(!order.isEmpty()) {
//            System.out.println(" ");
//            for (Exam e : order) {
//                System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " " + e.nConflict());
//            }
//            System.out.println(" ");

			Exam e = order.get(0);
			Set<Integer> slo = e.timeslotAvailable();
			if(slo.isEmpty()) {
//                System.out.println("No good slot available");
				for(Exam conflicting : e.exmConflict) {
					if(conflicting.isScheduled()) {
						conflicting.unschedule();
					}
				}
			} else {
				scheduleRand(e, slo);
			}
			order = exams.values().stream().filter(ex -> !ex.isScheduled()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}

	}

	/**
	 * Clears data structures from previously generated FFS
	 * <p>
	 * TODO: is this needed?
	 */
	private void resetFFS() {
		exams.forEach((i, e) -> e.unschedule());
	}

	/**
	 * Insert the exam in a timeslot available where there are no conflicts
	 * Does nothing if there are no available time slots.
	 *
	 * @param e: exam to schedule
	 */
	private void scheduleRand(Exam e, Set<Integer> availableTimeslots) {
		if(availableTimeslots.size() == 0) {
			return;
		}

		int n = rand.nextInt(availableTimeslots.size());
		int i = 0;
		for(Integer timeslot : availableTimeslots) {
			if(i == n) {
				e.schedule(timeslot);
				// System.out.println(e.getExmID() + " randomly scheduled in " + timeslot + " (position " + i + "/" + availableTimeslots.size() + ")");
				return;
			}
			i++;
		}

		throw new RuntimeException("Bad things are happening");
	}

	/**
	 * Create a neighborhood starting from the main solution unscheduling 1/3 of the exams and randomly rescheduling them using the FFS method
	 *
	 * @param n size of neighborhood
	 * @return List of neighbor
	 * <p>
	 * TODO: accept the "1/3" thing as a parameter?
	 */
	List<Data> createNeighborhood(int n) throws Exception {
		List<Data> neigh = new ArrayList<>();
		Exam u;
		Data d = (Data) ObjectCloner.deepCopy(this);

		int j = 0;
		while(j < nExm / 3) {
			u = d.exams.get(rand.nextInt(nExm));
			if(u != null && u.isScheduled()) {
				u.unschedule();
				j++;
			}
		}

		int i;
		for(i = 0; i < n; i++) {
			neigh.add(d);
		}

		for(Data da : neigh) {
			da.createSolution();
		}
		return neigh;
	}
}

