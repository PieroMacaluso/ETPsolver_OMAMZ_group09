package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Array;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

class Data {
	private Map<Integer, Student> students = new TreeMap<>();
	private Map<Integer, Exam> exams = new TreeMap<>();
	private Map<Integer, Timeslot> timeslots = new TreeMap<>();
	private Random rand = new Random();

	int nExm = 0;
	int nStu = 0;
	int nSlo = 0;
	private String filename;

	// Matrice di booleani con ordinate studenti e ascissa esami
	// private boolean stuExm[][];
	int conflictTable[][];

	/**
	 * Create object representing problem data.
	 *
	 * @param filename Instance name, actually. Without extension. Could even be a path.
	 * @throws FileNotFoundException If any file for the instance doesn't exist
	 */
	Data(String filename) throws FileNotFoundException {
		this.filename = filename;
		rand.setSeed(Timestamp.from(Instant.now()).getTime());
		startRead();
		createFFS();
		createFFS();
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
		conflictTable = new int[nExm][nExm];
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
							conflictTable[i][j]++;
							conflictTable[j][i]++;
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

	private void readSlots(Scanner sSlo) {
		String line = sSlo.nextLine();
		nSlo = Integer.parseInt(line);
		for(int i = 1; i <= nSlo; i++)
			timeslots.put(i, new Timeslot(i));
	}

	/**
	 * FFS stands for "First Feasible Solution"
	 * C1 - timeslot available: priority to exam that has less timeslot available
	 * C2 - conflict with others: priority to exams with more conflict
	 */
	public boolean createFFS2() {
		List<Exam> order;
		order = exams.values().stream().filter(e -> !e.isScheluded()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());

//        for (Exam e:order) {
//            System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " "+ e.nConflict());
//        }
		while(!order.isEmpty()) {
			Exam e = order.get(0);
			Map<Integer, Timeslot> slo = e.timeslotAvailable(timeslots);
			if(slo.isEmpty()) {
				System.out.println("NOOO");
				return false;
			}
			scheduleRand(e, slo);
			for(Map.Entry<Integer, Exam> entry : e.exmConflict.entrySet()) {
				slo = entry.getValue().timeslotAvailable(timeslots);
				if(slo.isEmpty()) {
					System.out.println("NOOO");
					return false;
				}
				scheduleRand(entry.getValue(), slo);
			}
			order = exams.values().stream().filter(ex -> !ex.isScheluded()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}

		System.out.println("FFS created");
		return true;
	}

	/**
	 * FFS stands for "First Feasible Solution"
	 * C1 - timeslot available: priority to exam that has less timeslot available
	 * C2 - conflict with others: priority to exams with more conflict
	 * <p>
	 * If the code reaches a stuck point with one exam, unschedule all conflicting exams and continue the procedure.
	 */
	public boolean createFFS() {
		List<Exam> order;
		order = exams.values().stream().filter(ex -> !ex.isScheluded()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());

//        for (Exam e:order) {
//            System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " "+ e.nConflict());
//        }
		while(!order.isEmpty()) {
//            System.out.println(" ");
//            for (Exam e : order) {
//                System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " " + e.nConflict());
//            }
//            System.out.println(" ");

			Exam e = order.get(0);
			Map<Integer, Timeslot> slo = e.timeslotAvailable(timeslots);
			if(slo.isEmpty()) {
//                System.out.println("No good slot available");
//                scheduleRand(e, timeslots);
				for(Map.Entry<Integer, Exam> entry : e.exmConflict.entrySet()) {
					if(entry.getValue().isScheluded())
						entry.getValue().unschedule();
				}
			} else {
				scheduleRand(e, slo);

			}
			order = exams.values().stream().filter(ex -> !ex.isScheluded()).sorted(Comparator.comparing(Exam::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}

		for(Map.Entry<Integer, Exam> entry : exams.entrySet()) {
			System.out.println(entry.getValue().getExmID() + " " + entry.getValue().getTimeslot().getSloID());
		}
		System.out.println("FFS created");


		return true;
	}

	/**
	 * Useful, but not yet used to reset FFS
	 */
	private void resetFFS() {
		System.out.println("FFS noway");
		exams.forEach((i, e) -> e.resetTimeslot());
		timeslots.forEach((i, t) -> t.resetExam());
	}

	/**
	 * Insert the exam in a timeslot available where there are no conflicts
	 *
	 * @param e: exam to schedule
	 */
	private void scheduleRand(Exam e, Map<Integer, Timeslot> slo) {
		if(slo.size() == 0) return;
		int n = rand.nextInt(nSlo) + 1;
		while(!slo.containsKey(n)) {
			n = rand.nextInt(nSlo) + 1;
		}
		Timeslot t = timeslots.get(n);
		e.setScheluded(true);
		e.setTimeslot(t);
		t.addExam(e);
//        System.out.println(e.getExmID() + " " + e.getTimeslot().getSloID());

	}

	/**
	 * Extract a random exam not already scheduled
	 *
	 * @return Exam selected
	 */
	private Exam randomExamNotScheduled() {
		Exam e;
		int n = rand.nextInt(nExm) + 1;
		e = exams.get(n);
		while(e.isScheluded()) {
			n = rand.nextInt(nExm) + 1;
			e = exams.get(n);
		}
		return e;

	}


}

