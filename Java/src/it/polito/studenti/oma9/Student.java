package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

public class Student {
    private int stuID;
    private Map<Integer, Exam> exams =  new TreeMap<>();

    public Student(int stuID) {
        this.stuID = stuID;
    }

    public int getStuID() {
        return stuID;
    }

    public void addExam(Exam e) {
        exams.put(e.getExmID(), e);
    }
}
