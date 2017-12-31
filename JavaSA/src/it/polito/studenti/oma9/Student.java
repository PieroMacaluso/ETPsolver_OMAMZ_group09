package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

class Student implements Comparable<Student> {
	private final int id;
	private Map<Integer, Exam> exams = new TreeMap<>();

	Map<Integer, Exam> getExams() {
		return exams;
	}

	/**
	 * Default constructor
	 *
	 * @param id student ID
	 */
	Student(int id) {
		this.id = id;
	}

	/**
	 * Add exam to the student
	 *
	 * @param e Exam
	 */
	void addExam(Exam e) {
		exams.put(e.id, e);
	}

	@Override
	public int compareTo(Student student) {
		return student.id - this.id;

	}
}
