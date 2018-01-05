package it.polito.studenti.oma9;

import java.util.HashSet;
import java.util.Set;

class Student implements Comparable<Student> {
	private final int id;
	private Set<Exam> exams = new HashSet<>();

	Set<Exam> getExams() {
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
		exams.add(e);
	}

	@Override
	public int compareTo(Student student) {
		return student.id - this.id;
	}
}
