package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

public class Timeslot {
    private int sloID;
    private Map<Integer, Exam> exams = new TreeMap<>();

    public Timeslot(int sloID) {
        this.sloID = sloID;
    }

    public Map<Integer, Exam> getExams() {
        return exams;
    }
    void addExam(Exam e) {
        exams.put(e.getExmID(), e);
    }

    public int getSloID() {
        return sloID;
    }

    public void resetExam() {
        exams.clear();
    }

    public void removeExam(Exam exam) {
        exams.remove(exam.getExmID());
    }
}
