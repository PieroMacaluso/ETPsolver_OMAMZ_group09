package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
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

	String getFilename() {
		return filename;
	}

	Map<Integer, Exam> getExams() {
		return exams;
	}

	private Data() {
	}

	/**
	 * Create object representing problem data.
	 *
	 * @throws FileNotFoundException If any file for the instance doesn't exist
	 */
	void initialize(String filename) throws FileNotFoundException {
		this.filename = filename;
		startRead();
	}


	// Metodo della classe impiegato per accedere al singleton
	static synchronized Data getInstance() {
		if(instance == null) {
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
		readStudents(sStu);

		buildConflicts();
	}

	private void buildConflicts() {
		// TODO: questa cosa è inefficiente
		for(int i = 1; i < nExm; i++) {
			for(int j = i + 1; j <= nExm; j++) {
				for(Integer student1 : exams.get(i).students.keySet()) {
					for(Integer student2 : exams.get(j).students.keySet()) {
						if(student1.equals(student2)) {
							exams.get(i).addConflict(exams.get(j));
							exams.get(j).addConflict(exams.get(i));
						}
					}
				}
			}
		}
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

