package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

class Data implements Serializable {
	private static Data instance = null;
	int nExm = 0;
	int nStu = 0;
	int nSlo = 0;
	private Map<Integer, Student> students = new HashMap<>();
	private Map<Integer, Exam> exams = new HashMap<>();
	private int[][] conflicts;
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

	/**
	 * Build conflicts map. Ci mette 27 s ogni volta.
	 *
	 * @deprecated dire che è inefficiente è un understatement
	 */
	private void buildConflictsOld() {
		// TODO: questa cosa è inefficiente
		LocalTime start = LocalTime.now();
		System.out.println("Begin building conflict map");
		for(int i = 1; i <= nExm; i++) {
			for(int j = i + 1; j <= nExm; j++) {
				for(Integer student1 : exams.get(i).students.keySet()) {
					for(Integer student2 : exams.get(j).students.keySet()) {
						if(student1.equals(student2)) {
							exams.get(i).addConflict(exams.get(j));
							exams.get(j).addConflict(exams.get(i));

							// Questo prima lo faceva in addConflict (col codice ora commentato via), è un numero a caso tanto per metterci qualcosa, in realtà dovrebbe fare ++...
							exams.get(i).setConflictCounter(exams.get(j), 1337);
							exams.get(j).setConflictCounter(exams.get(i), 1337);
						}
					}
				}
			}
		}

		int total = 0;
		for(Exam e : exams.values()) {
			total += e.exmConflict.size();
		}
		System.out.println("Finished building conflict map, " + total + " conflicting exam couples, took: " + Duration.between(start, LocalTime.now()));
	}

	/**
	 * Build conflicts map.
	 * Runs in 0.05 s, usually.
	 */
	private void buildConflicts() {
		LocalTime start = LocalTime.now();
		System.out.println("Begin building conflict map (new)");
		conflicts = new int[nExm+1][nExm+1];

		for(Student student : students.values()) {
			for(Exam exam : student.getExams().values()) {
				for(Exam other : student.getExams().values()) {
					if(exam.compareTo(other) < 0) {
						exam.addConflict(other);
						other.addConflict(exam);
						conflicts[exam.exmID][other.exmID]++;
						conflicts[other.exmID][exam.exmID]++;
						//System.out.println(exam.exmID + " and " + other.exmID + " conflict by " + conflicts[exam.exmID][other.exmID]);
					}
				}
			}
		}

		int total = 0;
		for(int i = 1; i <= nExm; i++) {
			for(int j = i + 1; j <= nExm; j++) {
				if(conflicts[i][j] != 0) {
					Exam one = this.exams.get(i);
					Exam two = this.exams.get(j);
					Integer numberOfConflicts = conflicts[i][j];
					one.setConflictCounter(two, numberOfConflicts);
					two.setConflictCounter(one, numberOfConflicts);
					total += 2;
				}
			}
		}

		System.out.println("Finished building conflict map, " + total + " conflicting exam couples, took: " + Duration.between(start, LocalTime.now()));
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
				Exam exam = exams.get(examID);
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

