package it.polito.studenti.oma9;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

class Solution {
	private Map<Exam, Integer> allocations = new TreeMap<>();
	private Random rand = new Random();

	Solution(Solution sol) {
		this.allocations.putAll(sol.allocations);
	}

	Solution() {
		createSolution();
	}

	/**
	 *  Create the solution
	 */
	void createSolution() {

//		createFFS();
		List<Exam> order;

		order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());

//        for (Exam e:order) {
//            System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " "+ e.nConflict());
//        }
		// TODO: use do-while?
		while(!order.isEmpty()) {
//            System.out.println(" ");
//            for (Exam e : order) {
//                System.out.println(e.getExmID() + " " + e.nTimeslotNoWay() + " " + e.nConflict());
//            }
//            System.out.println(" ");

			Exam e = order.get(0);
			Set<Integer> slo = this.timeslotAvailable(e);
			if(slo.isEmpty()) {
//                System.out.println("No good slot available");
				for(Exam conflicting : e.exmConflict) {
					if(this.isScheduled(conflicting)) {
						this.unschedule(conflicting);
					}
				}
			} else {
				this.scheduleRand(e, slo);
			}
			order = Data.getInstance().getExams().values().stream().filter((Exam ex) -> !this.isScheduled(ex)).sorted(Comparator.comparing(this::nTimeslotNoWay).thenComparing(Exam::nConflict).reversed()).collect(Collectors.toList());
		}

//        printSolution();
	}

	/**
	 * Schedule exam in timeslot t
	 *
	 * @param exam exam
	 * @param ts   timeslot
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

	/**
	 * Find the number of slots where this exam cannot be placed
	 *
	 * @return number of slots
	 */
	int nTimeslotNoWay(Exam exam) {
		Set<Integer> timeslots = new HashSet<>();

		// For each conflicting exam
		for(Exam conflicting : exam.exmConflict) {
			// If it has been scheduled
			if(this.isScheduled(conflicting)) {
				// Add that time slot to the list of conflicting ones
				// (Set compares Integer value, not that it is a pointer to same memory location, so everything works fine)
				timeslots.add(this.allocations.get(conflicting));
			}
		}

		return timeslots.size();
	}

	/**
	 * Return a set of all the available time slots (no conflict) in the current context
	 *
	 * @param exam exam
	 * @return set of available time slots
	 */
	Set<Integer> timeslotAvailable(Exam exam) {
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
	 * Insert the exam in a timeslot available where there are no conflicts
	 * Does nothing if there are no available time slots.
	 *
	 * @param e: exam to schedule
	 */
	void scheduleRand(Exam e, Set<Integer> availableTimeslots) {
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
	 * Evaluate the cost of the current solution
	 *
	 * @return cost
	 */
	double evalutate() {
		double sum = 0;
		for(Exam e1 : allocations.keySet()) {
			for(Exam e2 : e1.exmConflict) {
				if(Math.abs(allocations.get(e2) - allocations.get(e1)) == 0) {
					System.out.println("Unfesible solution!! BAAAAAD");
					return Double.MAX_VALUE;

				}
				if(e2.getExmID() > e1.getExmID() && Math.abs((allocations.get(e2) - allocations.get(e1))) < 6) {
					int d = Math.abs((allocations.get(e2) - allocations.get(e1)));
					Set<Student> s = new TreeSet<>();
					s.addAll(e1.students.values());
					s.retainAll(e2.students.values());
					long nee = s.size();
					sum += Math.pow(2, 5 - d) * nee;
				}
			}

		}
		sum = sum / Data.getInstance().nStu;
		return sum;
	}


	/**
	 * @return exam cost
	 * @deprecated is this an unused duplicate?
	 */
	double costExam(Exam e1) {
		double sum = 0;
		for(Exam e2 : e1.exmConflict) {
			int d = Math.abs(this.getTimeslot(e2) - this.getTimeslot(e1));
			if(d == 0) {
				System.out.println("Unfesible solution!! BAAAAAD");
				return Double.MAX_VALUE;

			}
			if(e2.getExmID() > e1.getExmID() && d < 6) {
				Set<Student> s = new TreeSet<>();
				s.addAll(e1.students.values());
				s.retainAll(e2.students.values());
				long nee = s.size();
				sum += Math.pow(2, 5 - d) * nee;
			}
		}
		return sum / Data.getInstance().nStu;
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
				//System.out.println("OMG confligge con " + exam.conflictingStudentsCounter.getOrDefault(conflicting, 0) + " studenti (" + exam.getExmID() + " con " + conflicting.getExmID() + ")");
			}
		}
		return sum / Data.getInstance().nStu;

	}

	/**
	 * @return nstuconflict
	 */
	int getNStuConflict(Exam e1) {
		int stu = 0;
		for(Exam e2 : e1.exmConflict) {
			int d = Math.abs(this.getTimeslot(e2) - this.getTimeslot(e1));
			if(d < 6) {
				Set<Student> s = new TreeSet<>();
				s.addAll(e1.students.values());
				s.retainAll(e2.students.values());
				long nee = s.size();
				stu += nee;
			}
		}
		return stu;
	}

	/**
	 * Print the solution
	 */
	void printSolution() {
		try {
			PrintWriter writer = new PrintWriter(Data.getInstance().getFilename() + ".sol", "UTF-8");
			for(Map.Entry<Exam, Integer> e : allocations.entrySet()) {
				writer.println(e.getKey().getExmID() + " " + e.getValue());

				//System.out.println(e.getKey() + " " + e.getValue().getTimeslot());

			}
			writer.close();
		} catch(IOException e) {
			System.out.println("Non riesco a scrivere la soluzione su " + Data.getInstance().getFilename() + ".sol");
			throw new RuntimeException();
		}

	}

	/**
	 * Create a neighborhood starting from the main solution unscheduling 1/3 of the exams and randomly rescheduling them using the FFS method
	 *
	 * @return List of neighbor
	 * <p>
	 */
	@SuppressWarnings("SameParameterValue")
	Solution createNeighbor(double percentage) throws Exception {
		Exam u;
		Solution s = new Solution(this);
		int j = 0;
		while(j < (int) (Data.getInstance().nExm * percentage)) {
			u = Data.getInstance().getExams().get(rand.nextInt(Data.getInstance().nExm));
			if(u != null && this.isScheduled(u)) {
				s.unschedule(u);
				j++;
			}
		}

		s.createSolution();
		return s;
	}


}
