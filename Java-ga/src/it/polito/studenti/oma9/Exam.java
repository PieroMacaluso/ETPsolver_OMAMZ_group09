package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

class Exam {
	private int exmID;
	Map<Integer, Student> students = new TreeMap<>();

	Exam(int exmID) {
		this.exmID = exmID;
	}

	int getExmID() {
		return exmID;
	}

	void addStudent(Student s) {
		students.put(s.getStuID(), s);
	}
}
