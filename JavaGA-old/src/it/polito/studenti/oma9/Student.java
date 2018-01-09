package it.polito.studenti.oma9;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

class Student {
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
}
