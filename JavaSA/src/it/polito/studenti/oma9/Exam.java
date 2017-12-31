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

	// Used by Set, Map key, etc... Exams are the same based on ID only.
	@Override
	public int compareTo(Exam exam) {
		return this.id - exam.id;
	}
}
