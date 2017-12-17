package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

class Exam implements Comparable<Exam>, Serializable {
	Map<Integer, Student> students = new HashMap<>();
	Set<Exam> exmConflict = new HashSet<>();
	private int exmID;

	/**
	 * Default constructor
	 *
	 * @param exmID exam ID
	 */
	Exam(int exmID) {
		this.exmID = exmID;
	}

	/**
	 * Getter ExmID
	 *
	 * @return exam ID
	 */
	int getExmID() {
		return exmID;
	}

	/**
	 * Add a student s in the exam
	 *
	 * @param s Student
	 */
	void addStudent(Student s) {
		students.put(s.getStuID(), s);
	}

	/**
	 * Add a conflicting exam
	 *
	 * @param e exam in conflict
	 */
	void addConflict(Exam e) {
		this.exmConflict.add(e);
	}



	/**
	 * Number of conflict of the exam
	 *
	 * @return number of conflicts
	 */
	int nConflict() {
		return exmConflict.size();
	}


	// Used by Set, Map key, etc... Exams are the same based on ID only.
	@Override
	public int compareTo(Exam exam) {
		return exam.getExmID() - this.getExmID();
	}
}
