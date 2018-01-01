package it.polito.studenti.oma9;

import java.util.Set;
import java.util.TreeSet;

class Exam implements Comparable<Exam> {
	Set<Exam> conflicts = new TreeSet<>();
	final int id;

	/**
	 * Create an empty exam
	 *
	 * @param id exam ID
	 */
	Exam(int id) {
		this.id = id;
	}

	/**
	 * Add a conflicting exam
	 *
	 * @param e exam in conflict
	 */
	void addConflict(Exam e) {
		conflicts.add(e);
	}

	/**
	 * Number of conflicting exams
	 *
	 * @return number of conflicting exams
	 */
	int nConflictingExams() {
		return conflicts.size();
	}

	/**
	 * Number of conflicting students
	 *
	 * Didn't work very well (slow and solutions are basically the same)
	 *
	 * @return number of conflicting students
	 */
	int nConflictingStudents() {
		Data data = Data.getInstance();
		int sum = 0;

		for(Exam other : conflicts) {
			sum += data.conflictsBetween(this, other);
		}

		return sum;
	}

	// Used by Set, Map key, etc... Exams are the same based on ID only.
	@Override
	public int compareTo(Exam exam) {
		return this.id - exam.id;
	}
}
