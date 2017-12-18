package it.polito.studenti.oma9;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

class Solution {
	//private Map<Exam, Integer> timetable = new HashMap<>();
	private Map<Exam, Integer> timetable = new HashMap<>(Data.getInstance().nExm + 2, (float) 1.0);
	private Random rand = new Random();

	/**
	 * Clone another solution, basically.
	 *
	 * @param sol other solution
	 */
	Solution(Solution sol) {
		timetable.putAll(sol.timetable);
	}

	/**
	 * Create a new feasible solution.
	 */
	Solution() {
		createSolution();
	}

	/**
	 *  Create the solution
	 */
	void createSolution() {
		List<Exam> order;

		order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::countUnavailableTimeslots).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		// TODO: use do-while?
		while(!order.isEmpty()) {
			Exam e = order.get(0);
			Set<Integer> slo = this.getAvailableTimeslots(e);
			if(slo.isEmpty()) {
				// System.out.println("No good slot available");
				for(Exam conflicting : e.exmConflict) {
					this.unschedule(conflicting);
				}
			} else {
				this.scheduleRand(e, slo);
			}
			order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::countUnavailableTimeslots).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}
	}

	/**
	 * Schedule exam in timeslot t
	 *
	 * @param exam exam
	 * @param ts   timeslot
	 */
	void schedule(Exam exam, int ts) {
		timetable.put(exam, ts);
	}

	/**
	 * Unschedule the exam.
	 * Does nothing if it wasn't scheduled.
	 *
	 * @param exam exam
	 */
	void unschedule(Exam exam) {
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
		Set<Integer> timeslots = new HashSet<>();

		// For each conflicting exam
		for(Exam conflicting : exam.exmConflict) {
			// If it has been scheduled
			// TODO: optimize by calling .get directly?
			if(this.isScheduled(conflicting)) {
				// Add that time slot to the list of conflicting ones
				// (Set compares Integer value, not that it is a pointer to same memory location, so everything works fine)
				timeslots.add(this.timetable.get(conflicting));
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
		for(Exam e : exam.exmConflict) {
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
	 * Evaluate the cost of the current solution according to the objective function
	 *
	 * @return cost
	 */
	double evaluateCost() {
		double sum = 0;
		for(Exam e1 : timetable.keySet()) {
			for(Exam e2 : e1.exmConflict) {
				int distance = getDistance(e1, e2);
				if(distance == 0) {
					throw new RuntimeException("Infeasible solution!");
					//return Double.MAX_VALUE;
				}
				if(e2.getExmID() > e1.getExmID() && distance <= 5) {
					sum += Math.pow(2, 5 - distance) * e1.conflictingStudentsCounter.getOrDefault(e2, 0);
				}
			}

		}
		sum = sum / Data.getInstance().nStu;
		return sum;
	}

	/**
	 * @return exam cost
	 */
	double examCost(Exam exam) {
		double sum = 0;
		for(Exam conflicting : exam.exmConflict) {
			// Take every conflicting exam and measure distance
			int d = Math.abs(this.getTimeslot(conflicting) - this.getTimeslot(exam));
			// This shouldn't happen, hopefully
			if(d == 0) {
				System.out.println("Infeasible solution!! BAAAAAD");
				return Double.MAX_VALUE;
			}
			// If they're close enough to trigger a penalty
			if(d <= 5) {
				// Calculate penalty
				sum += Math.pow(2, 5 - d) * exam.conflictingStudentsCounter.getOrDefault(conflicting, 0);
				//System.out.println("Conflict between " + exam.conflictingStudentsCounter.getOrDefault(conflicting, 0) + " students (exam " + exam.getExmID() + " with " + conflicting.getExmID() + ")");
			}
		}
		return sum / Data.getInstance().nStu;

	}

	/**
	 * Print the solution
	 */
	void printSolution() {
		try {
			PrintWriter writer = new PrintWriter(Data.getInstance().getFilename() + ".sol", "UTF-8");
			for(Map.Entry<Exam, Integer> e : timetable.entrySet()) {
				writer.println(e.getKey().getExmID() + " " + e.getValue());

				//System.out.println(e.getKey() + " " + e.getValue().getTimeslot());

			}
			writer.close();
		} catch(IOException e) {
			System.err.println("Cannot write solution on " + Data.getInstance().getFilename() + ".sol");
			throw new RuntimeException();
		}

	}

	/**
	 * Create a neighbor solution starting from current solution, "unscheduling" a percentage of the exams
	 * and randomly rescheduling them using the FFS method
	 *
	 * @return New solution (leaves old solution unchanged)
	 */
	@SuppressWarnings("SameParameterValue")
	Solution createNeighbor(double percentage) throws Exception {
		Solution s = new Solution(this);

		int j = 0;
		while(j < (int) (Data.getInstance().nExm * percentage)) {
			Exam chosen = Data.getInstance().getExams().get(rand.nextInt(Data.getInstance().nExm));
			// TODO: lots of accesses to isScheduled, which is slow... shuffle exams, put into list and unschedule first part of the list?
			if(chosen != null && this.isScheduled(chosen)) {
				s.unschedule(chosen);
				j++;
			}
		}

		s.createSolution();
		return s;
	}


}
