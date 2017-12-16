package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

class Exam implements Serializable {
	Map<Integer, Student> students = new HashMap<>();
	Map<Integer, Exam> exmConflict = new HashMap<>();
	private int exmID;
	private boolean scheduled = false;
	private Timeslot timeslot = null;

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
	 * Is it scheduled yet?
	 *
	 * @return True if the exam is scheduled, False otherwise
	 */
	boolean isScheduled() {
		return scheduled;
	}

	/**
	 * Unschedule the exam
	 */
	void unschedule() {
		timeslot.removeExam(this);
		timeslot = null;
		this.scheduled = false;
	}

	/**
	 * Get the timeslot where the exam is scheduled
	 *
	 * @return timeslot
	 */
	Timeslot getTimeslot() {
		return timeslot;
	}

	/**
	 * Reset the timeslot
	 */
	void resetTimeslot() {
		this.timeslot = null;
		this.scheduled = false;
	}

	/**
	 * Add a conflicting exam
	 *
	 * @param e exam in conflict
	 */
	void addConflict(Exam e) {
		this.exmConflict.put(e.getExmID(), e);
	}

	/**
	 * Return a map of all the timeslot available (no conflict) in the current context
	 *
	 * @param t: set of timeslot
	 * @return set of timeslot available
	 */
	Map<Integer, Timeslot> timeslotAvailable(Map<Integer, Timeslot> t) {
		Map<Integer, Timeslot> all = new HashMap<>();

		all.putAll(t);
		for(Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if(entry.getValue().getTimeslot() != null)
				all.remove(entry.getValue().getTimeslot().getSloID());
		}
		return all;
	}

	/**
	 * Find the number of slots where the exam e cannot be placed
	 *
	 * @return number of slots
	 */
	int nTimeslotNoWay() {
		Map<Integer, Timeslot> all = new HashMap<>();
		for(Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if(entry.getValue().getTimeslot() != null)
				all.put(entry.getValue().getTimeslot().getSloID(), entry.getValue().getTimeslot());
		}
		return all.size();
	}

	/**
	 * Number of conflict of the exam
	 *
	 * @return number of conflicts
	 */
	int nConflict() {
		return exmConflict.size();
	}

	/**
	 * Schedule exam in timeslot t
	 *
	 * @param t timeslot
	 */
	void schedule(Timeslot t) {
		this.scheduled = true;
		this.timeslot = t;
		t.addExam(this);
	}
}
