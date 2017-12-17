package it.polito.studenti.oma9;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Data implements Serializable {
	private static Data instance = null;
	int nExm = 0;
	int nStu = 0;
	int nSlo = 0;
	private Map<Integer, Student> students = new HashMap<>();
	private Map<Integer, Exam> exams = new HashMap<>();
	private String filename;

	public String getFilename() {
		return filename;
	}

	Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Create object representing problem data.
	 *
	 * @throws FileNotFoundException If any file for the instance doesn't exist
	 */
	private Data()  {
	}
	void initialize(String filename) throws FileNotFoundException {
		this.filename = filename;
		startRead();
	}


	// Metodo della classe impiegato per accedere al singleton
	public static synchronized Data getInstance() {
		if (instance == null) {
			instance = new Data();
		}
		return instance;
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
	 *
	 * @deprecated
	 */
	Solution createSolution() {
		Solution sol = new Solution();
//		createFFS();
		List<Exam> order;

		order = exams.values().stream().filter(ex -> !sol.isScheduled(ex)).sorted(Comparator.comparing(sol::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());

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
			Set<Integer> slo = sol.timeslotAvailable(e);
			if(slo.isEmpty()) {
//                System.out.println("No good slot available");
				for(Exam conflicting : e.exmConflict) {
					if(sol.isScheduled(conflicting)) {
						sol.unschedule(conflicting);
					}
				}
			} else {
				sol.scheduleRand(e, slo);
			}
			order = exams.values().stream().filter(ex -> !sol.isScheduled(ex)).sorted(Comparator.comparing(sol::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}
		return sol;

//        printSolution();
//        System.out.println(evaluateSolution());
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
			exams.put(exmID, new Exam(exmID));
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

}

