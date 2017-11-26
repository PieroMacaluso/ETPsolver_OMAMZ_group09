package it.polito.studenti.oma9;

import java.util.Map;
import java.util.TreeMap;

public class Exam {
    private int exmID;
    Map<Integer, Student> students = new TreeMap<>();


    public Exam(int exmID) {
        this.exmID = exmID;
    }

    public int getExmID() {
        return exmID;
    }

    public void addStudent(Student s) {
        students.put(s.getStuID(), s);
    }
}
