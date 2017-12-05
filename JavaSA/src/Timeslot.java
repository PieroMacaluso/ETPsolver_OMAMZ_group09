import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

public class Timeslot implements Serializable {
	private int sloID;
	private Map<Integer, Exam> exams = new TreeMap<>();

	Timeslot(int sloID) {
		this.sloID = sloID;
	}

	public Map<Integer, Exam> getExams() {
		return exams;
	}

	void addExam(Exam e) {
		exams.put(e.getExmID(), e);
	}

	int getSloID() {
		return sloID;
	}

	void resetExam() {
		exams.clear();
	}

	void removeExam(Exam exam) {
		exams.remove(exam.getExmID());
	}



}
