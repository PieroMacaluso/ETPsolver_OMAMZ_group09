import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

class Student implements Serializable {
	private int stuID;
	private Map<Integer, Exam> exams = new TreeMap<>();

	Student(int stuID) {
		this.stuID = stuID;
	}

	int getStuID() {
		return stuID;
	}

	void addExam(Exam e) {
		exams.put(e.getExmID(), e);
	}
	boolean hasExam (int id) {
		return exams.containsKey(id);
	}

}
