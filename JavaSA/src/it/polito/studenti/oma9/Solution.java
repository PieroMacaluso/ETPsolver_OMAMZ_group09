package it.polito.studenti.oma9;

import java.util.Map;

public class Solution {
	private Map<Exam, Integer> allocations;
	/**
	 * Schedule exam in timeslot t
	 *
	 * @param exam exam
	 * @param ts timeslot
	 */
	void schedule(Exam exam, int ts) {
		allocations.put(exam, ts);
	}

	/**
	 * Unschedule the exam
	 *
	 * @param exam exam
	 */
	void unschedule(Exam exam) {
		allocations.remove(exam);
	}

	/**
	 * Is it scheduled yet?
	 *
	 * @param exam exam
	 * @return True if the exam is scheduled, False otherwise
	 */
	boolean isScheduled(Exam exam) {
		return allocations.containsKey(exam);
	}

	/**
	 * Get the timeslot where the exam is scheduled
	 *
	 * @param exam exam
	 * @return timeslot, null if not scheduled
	 */
	Integer getTimeslot(Exam exam) {
		return allocations.get(exam);
	}
}
