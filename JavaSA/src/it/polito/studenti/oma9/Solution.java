package it.polito.studenti.oma9;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

class Solution {
	private Map<Exam, Integer> timetable = new HashMap<>(Data.nExm * 2, (float) 1.0);
	private Map<Integer, Set<Exam>> time = new HashMap<>(Data.nSlo * 2, (float) 1.0);
	private ThreadLocalRandom rand = ThreadLocalRandom.current();
	private double cost;
	private static final double[] precomputedPowers = {32, 16, 8, 4, 2, 1};
	private final Map<Exam, Integer> unavailableTimeslots = new HashMap<>(500);

	/**
	 * Clone another solution, basically.
	 *
	 * @param other solution
	 */
	Solution(Solution other) {
		createTime();
		for(Map.Entry<Integer, Set<Exam>> entry : other.time.entrySet()) {
			time.get(entry.getKey()).addAll(entry.getValue());
		}
		timetable.putAll(other.timetable);
		cost = other.cost;

	}

	/**
	 * Create a new, random, feasible solution.
	 */
	Solution() {
		createTime();
		createSolution();
	}

	private void createTime() {
		for(int i = 1; i <= Data.nSlo; i++) {
			time.put(i, new HashSet<>());
		}
	}

	/**
	 * Schedule all remaining exams and make sure the solution will be feasible.
	 */
	private void createSolution() {
		boolean valid = false;
		while(!valid) {
			valid = tryScheduleRemaining();
		}
	}

	/**
	 * Attempt to schedule all remaining exams, to create a feasible solution
	 *
	 * @see Solution#createSolution()
	 * @return true if valid, false if creation failed
	 */
	private boolean tryScheduleRemaining() {
		int failures = 0;
		final int nExm = Data.nExm;
		final int limit = nExm / 3; // TODO: explain limit
		boolean allScheduled = false;
		// Put all not-yet-scheduled exams into a list, we'll need to sort them...
		List<Exam> unscheduledExams = new ArrayList<>(Data.nExm + 1);
		Data.getInstance().getExams().values().stream().filter((Exam exam) -> !this.isScheduled(exam)).forEachOrdered(unscheduledExams::add);
		// ...with this comparator: by number of unavailable timeslots (ascending) and by number of conflicting exams (ascending)
		// This should put exams that are more difficult to schedule at the end of the list
		Comparator<Exam> comparator = Comparator.comparing(this::countUnavailableTimeslotsCached).thenComparing(Exam::nConflictingExams);

		// Nothing to schedule, nothing to do
		if(unscheduledExams.size() <= 0) {
			System.out.println(Thread.currentThread().getName() + " pointless rescheduling");
			return true;
		}

		// Until all exams have been scheduled
		while(!allScheduled) {
			// If too many exams were unschedulable, return
			if(failures > limit) {
				return false;
			}

			// Reset cache, since it depends on solution not changing and it probably has changed from last run
			resetUnavailableTimeslotCache();

			// Sort all not-yet-scheduled exams. This should do about n·log(n) comparisons, so 2·n·log(n) calls to this::countUnavailableTimeslots).
			// That method is relatively slow and there are just n exams, so by using a caching layer (this::countUnavailableTimeslotsCached)
			// we can call countUnavailableTimeslots just n times and get a (much faster) cache hit the remaining times.
			unscheduledExams.sort(comparator);

			// Reset cache again, to avoid using it accidentally and to save some memory
			resetUnavailableTimeslotCache();

			// Pick last exam from list and see where it could be placed
			int last = unscheduledExams.size() - 1;
			Exam candidate = unscheduledExams.get(last);
			Set<Integer> availableTimeslots = this.getAvailableTimeslots(candidate);

			if(availableTimeslots.isEmpty()) {
				// If answer is "nowhere", unschedule every conflicting exam and retry
				for(Exam conflicting : candidate.conflicts) {
					if(this.isScheduled(conflicting)) {
						this.unschedule(conflicting);
						unscheduledExams.add(conflicting);
					}
				}
				failures++;
			} else {
				// If there are available slots, choose one at random
				this.scheduleRand(candidate, availableTimeslots);
				// Was that the last exam to schedule?
				if(last <= 0) {
					// Done!
					allScheduled = true;
				} else {
					// Remove it.
					unscheduledExams.remove(last);
				}
			}

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
		time.get(ts).add(exam);
		cost += examCost(exam);
	}

	/**
	 * Unschedule the exam.
	 *
	 * @param exam exam
	 */
	void unschedule(Exam exam) {
		cost -= examCost(exam);
		time.get(this.getTimeslot(exam)).remove(exam);
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

	private void resetUnavailableTimeslotCache() {
		unavailableTimeslots.clear();
	}

	/**
	 * @see Solution#countUnavailableTimeslots(Exam)
	 *
	 * @return number of slots (from cache)
	 */
	private Integer countUnavailableTimeslotsCached(Exam exam) {
		Integer count = unavailableTimeslots.get(exam);
		if(count == null) {
			count = countUnavailableTimeslots(exam);
			unavailableTimeslots.put(exam, count);
		}
		return count;
	}

	/**
	 * Find the number of slots where the specified exam cannot be placed
	 *
	 * @return number of slots
	 */
	private int countUnavailableTimeslots(Exam exam) {
		Set<Integer> unavailable = new HashSet<>(Data.nExm * 2, (float) 1.0);

		// For each conflicting exam
		for(Exam conflicting : exam.conflicts) {
			// Get its scheduled position
			Integer timeslot = this.timetable.get(conflicting);
			// If it has actually been scheduled
			if(timeslot != null) {
				// Add that time slot to the list of conflicting ones
				// (Set compares Integer value, not that it is a pointer to same memory location, so everything works fine)
				unavailable.add(timeslot);
			}
		}

		return unavailable.size();
	}

	/**
	 * Return a set of all the available time slots (no conflict) for the specified exam
	 *
	 * @param exam exam
	 * @return set of available time slots
	 */
	Set<Integer> getAvailableTimeslots(Exam exam) {
		Set<Integer> all = new HashSet<>(Data.nSlo * 2, (float) 1.0);

		// Start from all timeslots
		for(int i = 1; i <= Data.nSlo; i++) {
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
			System.out.println("WARNING: scheduleRand called with no available timeslot");
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
	}

	/**
	 * Find distance between an exam and a time slot.
	 * Returns -1 if exam hasn't been scheduled.
	 *
	 * @param exam  Exam
	 * @param timeslot Timeslot
	 * @return distance
	 */
	private int getDistance(Exam exam, Integer timeslot) {
		Integer examTimeslot = timetable.get(exam);
		if(examTimeslot == null) {
			return -1;
		}
		return Math.abs(timeslot - examTimeslot);
	}

	/**
	 * Cost of a single exam, calculated according to objective function.
	 * Conflicting exams that aren't yet scheduled don't increase penalty.
	 *
	 * @return exam cost
	 */
	double examCost(Exam exam) {
		return examCostSlot(exam, timetable.get(exam));
	}

	/**
	 * Cost of a single exam if it were in specified timeslot, calculated according to objective function.
	 * Conflicting exams that aren't yet scheduled don't increase penalty.
	 *
	 * @return exam cost
	 */
	double examCostSlot(Exam exam, Integer slot) {
		double sum = 0;
		Data data = Data.getInstance();

		// Take every conflicting exam
		for(Exam conflicting : exam.conflicts) {
			// Measure distance
			int d = getDistance(conflicting, slot);
			// If conflicting exam hasn't been scheduled
			if(d < 0) {
				// keep looping
				continue;
			}
			// If they're scheduled to the same time slot
			if(d == 0) {
				// This shouldn't happen, hopefully
				System.out.println("Infeasible solution!");
				return Double.MAX_VALUE;
			}
			// If they're close enough to trigger a penalty
			if(d <= 5) {
				// Calculate penalty
				sum += precomputedPowers[d] * data.conflictsBetween(exam, conflicting);
			}
		}
		return sum;
	}

	/**
	 * Return cost of current solution according to objective function
	 * Avoiding the division by nStu (since it's just a scaling factor) didn't provide any significant performance enhancement.
	 *
	 * @return cost
	 */
	double solutionCost() {
		return cost / Data.nStu;
	}

	/**
	 * Create a neighbor solution starting from current solution, "unscheduling" a percentage of the exams
	 * and randomly rescheduling them.
	 *
	 * @return New solution (leaves old solution unchanged)
	 */
	@SuppressWarnings("SameParameterValue")
	Solution createNeighbor(double percentage) {
		Solution neighbor = null; // This just prevents the compiler from complaining, but it's guaranteed to be set before returning...
		boolean done = false;

		// TODO: se la % è bassa c'è il rischio che unscheduli 2 esami incastratissimi che ci stanno solo in quel buco e subito ricrea la stessa soluzione? (sì)
		// l'alternativa facile è NON far dipendere la % dalla temperatura, com'era prima...
		if(percentage < 0.1) {
			//System.out.printf(Thread.currentThread().getName() + " changing percentage to 5%% (from %4.2f)...\n", percentage*100);
			percentage = 0.1;
		}
		while(!done) {
			neighbor = new Solution(this);
			done = neighbor.unschedulePercentage(percentage) || neighbor.tryScheduleRemaining();
			//if(!done) System.out.println(Thread.currentThread().getName() + " retrying neighbor generation...");
		}

		return neighbor;
	}

	/**
	 * "Unschedule" a percentage of exams.
	 * Make sure that all exams have been scheduled before calling!
	 *
	 * @return true if nothing has been unscheduled (percentage too low) so solution is still valid, false otherwise
	 */
	private boolean unschedulePercentage(double percentage) {
		int j = 0;
		final int nExm = Data.nExm;
		final int limit = (int) (nExm * percentage);
		ArrayList<Exam> shuffled;

		if(limit <= 0) {
			return true;
		}

		shuffled = new ArrayList<>(Data.getInstance().getExams().values());
		Collections.shuffle(shuffled);

		while(j < limit) {
			Exam chosen = shuffled.get(j);
			//if(chosen != null && isScheduled(chosen)) {
				unschedule(chosen);
				j++;
			//}
		}
		return false;
	}

	Iterable<? extends Map.Entry<Exam, Integer>> export() {
		return timetable.entrySet();
	}

	public Set<Exam> getExamsInSlot(Integer slo) {
		return time.get(slo);
	}
}
