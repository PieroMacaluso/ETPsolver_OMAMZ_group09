package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Exam implements Serializable {
	Map<Integer, Student> students = new HashMap<>();
	Map<Integer, Exam> exmConflict = new HashMap<>(); // TODO: is Integer the other exam ID? Can we turn this into a Set?
	private int exmID;
	private boolean scheduled = false;
	private Integer timeslot = null;
	private Data data;

	/**
	 * Default constructor
	 *
	 * @param exmID exam ID
	 */
	Exam(int exmID, Data data) {
		this.exmID = exmID;
		this.data = data;
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
	 * Schedule exam in timeslot t
	 *
	 * @param t timeslot
	 */
	void schedule(int t) {
		this.timeslot = t;
		this.scheduled = true;
	}

	/**
	 * Unschedule the exam
	 */
	void unschedule() {
		timeslot = null;
		this.scheduled = false;
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
	 * Get the timeslot where the exam is scheduled
	 *
	 * @return timeslot
	 */
	Integer getTimeslot() {
		return timeslot;
	}

	/**
	 * Reset the timeslot
	 *
	 * @deprecated it's a duplicate of "unschedule"
	 */
	void resetTimeslot() {
		this.unschedule();
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
	 * Return a set of all the available time slots (no conflict) in the current context
	 *
	 * @return set of available time slots
	 */
	Set<Integer> timeslotAvailable() {
		Set<Integer> all = new HashSet<>();

		// Start from all timeslots
		for(int i = 1; i <= data.nSlo; i++) {
			all.add(i);
		}

		// Get every conflicting exam
		for(Exam entry : exmConflict.values()) {
			// Is it scheduled somewhere?
			Integer timeslot = entry.getTimeslot();
			if(timeslot != null) {
				// If it is, remove that time slot
				all.remove(timeslot);
			}
		}

		// Return remaining set
		return all;
	}

	/**
	 * Find the number of slots where this exam cannot be placed
	 *
	 * @return number of slots
	 */
	int nTimeslotNoWay() {
		Set<Integer> timeslots = new HashSet<>();

		// For each conflicting exam
		for(Exam other : exmConflict.values()) {
			// If it has been scheduled
			Integer timeslot = other.getTimeslot();
			if(timeslot != null) {
				// Add that time slot to the list of conflicting ones
				// (Set compares Integer value, not that it is a pointer to same memory location, so everything works fine)
				timeslots.add(timeslot);
			}
		}

		return timeslots.size();
	}

	/**
	 * Number of conflict of the exam
	 *
	 * @return number of conflicts
	 */
	int nConflict() {
		return exmConflict.size();
	}
}
