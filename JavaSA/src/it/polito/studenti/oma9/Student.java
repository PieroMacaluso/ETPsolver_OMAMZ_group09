package it.polito.studenti.oma9;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

class Student implements Serializable, Comparable<Student> {
	private int stuID;

	public Map<Integer, Exam> getExams() {
		return exams;
	}

	private Map<Integer, Exam> exams = new TreeMap<>();

	/**
	 * Default constructor
	 *
	 * @param stuID student ID
	 */
	Student(int stuID) {
		this.stuID = stuID;
	}

	/**
	 * Get Student ID
	 *
	 * @return student ID
	 */
	int getStuID() {
		return stuID;
	}

	/**
	 * Add exam to the student
	 *
	 * @param e Exam
	 */
	void addExam(Exam e) {
		exams.put(e.getExmID(), e);
	}

	/**
	 * Check if the student is signed up to exam with id
	 *
	 * @param id exam ID
	 * @return is it signed up or not?
	 */
	boolean hasExam(int id) {
		return exams.containsKey(id);
	}

	@Override
	public int compareTo(Student student) {
		return student.getStuID() - this.getStuID();

	}
}
