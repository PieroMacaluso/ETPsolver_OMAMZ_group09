package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

public class Exam {
	private int exmID;
	private boolean scheluded = false;
	Map<Integer, Student> students = new TreeMap<>();
	Map<Integer, Exam> exmConflict = new TreeMap<>();
	private Timeslot timeslot = null;

	Exam(int exmID) {
		this.exmID = exmID;
	}

	int getExmID() {
		return exmID;
	}

	void addStudent(Student s) {
		students.put(s.getStuID(), s);
	}

	public boolean isScheluded() {
		return scheluded;
	}

	public void setScheluded(boolean scheluded) {
		this.scheluded = scheluded;
	}

	public Timeslot getTimeslot() {
		return timeslot;
	}

	public void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}
	public void resetTimeslot() {
		this.timeslot = null;
		this.scheluded= false;
	}

	public void addConflict(Exam e) {
		this.exmConflict.put(e.getExmID(), e);
	}

	public Map<Integer, Timeslot> timeslotAvailable(Map<Integer, Timeslot> t) {
		Map <Integer, Timeslot> all = new TreeMap<>();
		all.putAll(t);
		for (Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if (entry.getValue().getTimeslot() != null)
				all.remove(entry.getValue().getTimeslot().getSloID());
		}
		return all;
	}
	public int nTimeslotAvailable(Map<Integer, Timeslot> t) {
		Map <Integer, Timeslot> all = new TreeMap<>();
		all.putAll(t);
		for (Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if (entry.getValue().getTimeslot() != null)

				all.remove(entry.getValue().getTimeslot().getSloID());
		}
		return all.size();
	}
	public int nTimeslotNoWay() {
		Map <Integer, Timeslot> all = new TreeMap<>();
		for (Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if (entry.getValue().getTimeslot() != null)
				all.put(entry.getKey(), entry.getValue().getTimeslot());
		}
		return all.size();
	}
	public int nConflict() {
		return exmConflict.size();
	}
}
