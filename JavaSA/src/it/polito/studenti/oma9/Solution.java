package it.polito.studenti.oma9;

import java.util.*;
import java.util.stream.Collectors;

class Solution {
	// TODO: capire se hashmap era più veloce (O(1) vs O(logn), in teoria...)
	//private Map<Exam, Integer> timetable = new HashMap<>(Data.getInstance().nExm + 2, (float) 1.0);
	private Map<Exam, Integer> timetable = new TreeMap<>();
	private Random rand = new Random();
	private double cost;


	/**
	 * Clone another solution, basically.
	 *
	 * @param other solution
	 */
	Solution(Solution other) {
		timetable.putAll(other.timetable);
		cost = other.cost;
	}

	/**
	 * Create a new feasible solution.
	 */
	Solution() {
		while(!createSolution()) ;

	}

	/**
	 * Create the solution
	 */
	boolean createSolution() {
		List<Exam> order;
		Map<Exam, Integer> backup = new TreeMap<>(timetable);
		int count = 0;

		order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::countUnavailableTimeslots).thenComparing(Exam::nConflictingExams).reversed()).collect(Collectors.toList());
		// TODO: use do-while?
		while(!order.isEmpty()) {
			if(count > Data.getInstance().nExm / 2) {
				timetable = new TreeMap<>(backup);
				return false;
			}

			Exam e = order.get(0);
			Set<Integer> slo = this.getAvailableTimeslots(e);
			if(slo.isEmpty()) {
				count++;
				order.add(e);
				// System.out.println("No good slot available");
				order.addAll(e.conflicts);
				for(Exam conflicting : e.conflicts) {
					if(this.isScheduled(conflicting)) {
						this.unschedule(conflicting);
					}
				}
			} else {
				this.scheduleRand(e, slo);
			}
			order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::countUnavailableTimeslots).thenComparing(Exam::nConflictingExams).reversed()).collect(Collectors.toList());
		}
		return true;
	}

	/**
	 * Schedule exam in timeslot t
	 *
	 * @param exam exam
	 * @param ts   timeslot
	 */
	void schedule(Exam exam, int ts) {
		timetable.put(exam, ts);
		cost += examCost(exam);
	}

	/**
	 * Unschedule the exam.
	 * Does nothing if it wasn't scheduled.
	 *
	 * @param exam exam
	 */
	void unschedule(Exam exam) {
		cost -= examCost(exam);
		timetable.remove(exam);
	}

	/**
	 * Is it scheduled yet?
	 *
	 * @param exam exam
	 * @return True if the exam is scheduled, False otherwise
	 */
	private boolean isScheduled(Exam exam) {
		return timetable.containsKey(exam);
	}

	/**
	 * Get the timeslot where the exam is scheduled
	 *
	 * @param exam exam
	 * @return timeslot, null if not scheduled
	 */
	Integer getTimeslot(Exam exam) {
		return timetable.get(exam);
	}

	/**
	 * Find the number of slots where the specified exam cannot be placed
	 *
	 * @return number of slots
	 */
	private int countUnavailableTimeslots(Exam exam) {
		Set<Integer> timeslots = new TreeSet<>();

		// For each conflicting exam
		for(Exam conflicting : exam.conflicts) {
			// Get its scheduled position
			Integer timeslot = this.timetable.get(conflicting);
			// If it has actually been scheduled
			if(timeslot != null) {
				// Add that time slot to the list of conflicting ones
				// (Set compares Integer value, not that it is a pointer to same memory location, so everything works fine)
				timeslots.add(timeslot);
			}
		}

		return timeslots.size();
	}

	/**
	 * Return a set of all the available time slots (no conflict) for the specified exam
	 *
	 * @param exam exam
	 * @return set of available time slots
	 */
	Set<Integer> getAvailableTimeslots(Exam exam) {
		Set<Integer> all = new HashSet<>();

		// Start from all timeslots
		for(int i = 1; i <= Data.getInstance().nSlo; i++) {
			all.add(i);
		}

		// Get every conflicting exam
		for(Exam e : exam.conflicts) {
			// Is it scheduled somewhere?
			if(this.isScheduled(e)) {
				// If it is, remove that time slot
				all.remove(this.getTimeslot(e));
			}
		}

		// Return remaining set
		return all;
	}

	/**
	 * Insert the exam in an available time slot (= where there are no conflicts)
	 * Does nothing if there are no available time slots.
	 *
	 * @param e exam to schedule
	 */
	private void scheduleRand(Exam e, Set<Integer> availableTimeslots) {
		if(availableTimeslots.size() == 0) {
			return;
		}

		int n = rand.nextInt(availableTimeslots.size());
		int i = 0;
		for(Integer timeslot : availableTimeslots) {
			if(i == n) {
				this.schedule(e, timeslot);
				// System.out.println(e.getExmID() + " randomly scheduled in " + timeslot + " (position " + i + "/" + availableTimeslots.size() + ")");
				return;
			}
			i++;
		}

		throw new RuntimeException("Bad things are happening");
	}

	/**
	 * Find distance between two allocated exams
	 *
	 * @param e1 Exam
	 * @param e2 Other exam
	 * @return distance
	 */
	private int getDistance(Exam e1, Exam e2) {
		return Math.abs(timetable.get(e2) - timetable.get(e1));
	}

	/**
	 * Cost of a single exam, calculated according to objective function.
	 * Conflicting exams that aren't yet scheduled don't increase penalty.
	 *
	 * @return exam cost
	 */
	double examCost(Exam exam) {
		double sum = 0;
		// Take every conflicting exam
		for(Exam conflicting : exam.conflicts) {
			// If it has been scheduled
			if(this.isScheduled(conflicting)) {
				// Measure distance
				int d = getDistance(conflicting, exam);
				if(d == 0) {
					// This shouldn't happen, hopefully
					System.out.println("Infeasible solution!");
					return Double.MAX_VALUE;
				}
				// If they're close enough to trigger a penalty
				if(d <= 5) {
					// Calculate penalty
					// TODO: precompute pow for ULTIMATE optimization?
					sum += Math.pow(2, 5 - d) * Data.getInstance().conflictsBetween(exam, conflicting);
					//System.out.println("Conflict between " + exam.conflictingStudentsCounter.getOrDefault(conflicting, 0) + " students (exam " + exam.getExmID() + " with " + conflicting.getExmID() + ")");
				}
			}
		}
		return sum;
	}

	/**
	 * Return cost of current solution according to objective function
	 *
	 * @return cost
	 */
	double solutionCost() {
		return cost / Data.getInstance().nStu;
	}


	/**
	 * Create a neighbor solution starting from current solution, "unscheduling" a percentage of the exams
	 * and randomly rescheduling them using the FFS method
	 *
	 * @return New solution (leaves old solution unchanged)
	 */
	@SuppressWarnings("SameParameterValue")
	Solution createNeighbor(double percentage) {
		Solution s = new Solution(this);

		int j = 0;
		while(j < (int) (Data.getInstance().nExm * percentage)) {
			Exam chosen = Data.getInstance().getExams().get(rand.nextInt(Data.getInstance().nExm));
			// TODO: lots of accesses to isScheduled, which is slow... shuffle exams, put into list and unschedule first part of the list?
			if(chosen != null && s.isScheduled(chosen)) {
				s.unschedule(chosen);
				j++;
			}
		}

		while(!s.createSolution()) ;
		return s;
	}

	Iterable<? extends Map.Entry<Exam, Integer>> export() {
		return timetable.entrySet();
	}
}
