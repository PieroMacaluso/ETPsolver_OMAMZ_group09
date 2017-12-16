package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

class Timeslot implements Serializable {
	private int sloID;
	private Map<Integer, Exam> exams = new TreeMap<>();

	/**
	 * Default constructor
	 *
	 * @param sloID slot ID
	 */
	Timeslot(int sloID) {
		this.sloID = sloID;
	}

	/**
	 * Get exam in the timeslot
	 *
	 * TODO: why is this unused!?
	 *
	 * @return exams in each time slot
	 */
	public Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Add exam e to the timeslot
	 *
	 * @param e exam
	 */
	void addExam(Exam e) {
		exams.put(e.getExmID(), e);
	}

	/**
	 * Get timeslot identifier
	 *
	 * @return timeslot ID
	 */
	int getSloID() {
		return sloID;
	}

	/**
	 * Reset exam in the timeslot
	 */
	void resetExam() {
		exams.clear();
	}

	/**
	 * Remove exam from timeslot
	 *
	 * @param exam exam
	 */
	void removeExam(Exam exam) {
		exams.remove(exam.getExmID());
	}


}
