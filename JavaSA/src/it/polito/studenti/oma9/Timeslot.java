package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Timeslot implements Serializable {
	private int sloID;
	private Map<Integer, Exam> exams = new TreeMap<>();

	/**
	 * Default constructor
	 * @param sloID
	 */
	Timeslot(int sloID) {
		this.sloID = sloID;
	}

	/**
	 * Get exam in the timeslot
	 * @return
	 */
	public Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Add exam e to the timeslot
	 * @param e
	 */
	void addExam(Exam e) {
		exams.put(e.getExmID(), e);
	}

	/**
	 * Get timeslot identifier
	 * @return
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
	 * @param exam
	 */
	void removeExam(Exam exam) {
		exams.remove(exam.getExmID());
	}



}
