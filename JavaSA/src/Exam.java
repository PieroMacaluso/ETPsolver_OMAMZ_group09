import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Exam implements Serializable{
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

	boolean isScheduled() {
		return scheluded;
	}

	void unschedule() {
		timeslot.removeExam(this);
		timeslot = null;
		this.scheluded = false;
	}

	void setScheduled(boolean scheluded) {
		this.scheluded = scheluded;
	}

	Timeslot getTimeslot() {
		return timeslot;
	}

	void setTimeslot(Timeslot timeslot) {
		this.timeslot = timeslot;
	}

	void resetTimeslot() {
		this.timeslot = null;
		this.scheluded = false;
	}

	void addConflict(Exam e) {
		this.exmConflict.put(e.getExmID(), e);
	}

	Map<Integer, Timeslot> timeslotAvailable(Map<Integer, Timeslot> t) {
		Map<Integer, Timeslot> all = new TreeMap<>();
		all.putAll(t);
		for(Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if(entry.getValue().getTimeslot() != null)
				all.remove(entry.getValue().getTimeslot().getSloID());
		}
		return all;
	}

	public int nTimeslotAvailable(Map<Integer, Timeslot> t) {
		Map<Integer, Timeslot> all = new TreeMap<>();
		all.putAll(t);
		for(Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if(entry.getValue().getTimeslot() != null)

				all.remove(entry.getValue().getTimeslot().getSloID());
		}
		return all.size();
	}

	int nTimeslotNoWay() {
		Map<Integer, Timeslot> all = new TreeMap<>();
		for(Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
			if(entry.getValue().getTimeslot() != null)
				all.put(entry.getValue().getTimeslot().getSloID(), entry.getValue().getTimeslot());
		}
		return all.size();
	}

	int nConflict() {
		return exmConflict.size();
	}

}
