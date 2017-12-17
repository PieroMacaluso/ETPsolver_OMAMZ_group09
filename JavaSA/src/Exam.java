import java.io.Serializable;
import java.sql.Time;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Exam implements Serializable, Comparable<Exam> {
    private int exmID;
    private boolean scheluded = false;
    Map<Integer, Student> students = new HashMap<>();
    Map<Integer, Exam> exmConflict = new HashMap<>();
    private Timeslot timeslot = null;

    /**
     * Default constructor
     *
     * @param exmID
     */
    Exam(int exmID) {
        this.exmID = exmID;
    }

    /**
     * Getter ExmID
     *
     * @return
     */
    int getExmID() {
        return exmID;
    }

    /**
     * Add a student s in the exam
     *
     * @param s Student
     */
    void addStudent(Student s) {
        students.put(s.getStuID(), s);
    }

    /**
     * True if the exam is scheduled, False otherwise
     *
     * @return
     */
    boolean isScheduled() {
        return scheluded;
    }

    /**
     * Unschedule the exam
     */
    void unschedule() {
        timeslot.removeExam(this);
        timeslot = null;
        this.scheluded = false;
    }

    /**
     * Set the current exam as scheduled
     *
     * @param scheluded
     */
    void setScheduled(boolean scheluded) {
        this.scheluded = scheluded;
    }

    /**
     * Get the timeslot where the exam is scheduled
     *
     * @return
     */
    Timeslot getTimeslot() {
        return timeslot;
    }

    /**
     * Set the timeslot to the exam
     *
     * @param timeslot
     */
    void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    /**
     * Reset the timeslot
     */
    void resetTimeslot() {
        this.timeslot = null;
        this.scheluded = false;
    }

    /**
     * Add a conflicting exam
     *
     * @param e exam in conflict
     */
    void addConflict(Exam e) {
        this.exmConflict.put(e.getExmID(), e);
    }

    /**
     * Return a map of all the timeslot available (no conflict) in the current context
     *
     * @param t: set of timeslot
     * @return set of timeslot available
     */
    Map<Integer, Timeslot> timeslotAvailable(Map<Integer, Timeslot> t) {
        Map<Integer, Timeslot> all = new HashMap<>();

        all.putAll(t);
        for (Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
            if (entry.getValue().getTimeslot() != null)
                all.remove(entry.getValue().getTimeslot().getSloID());
        }
        return all;
    }

    /**
     * Find the number of slot where the exam e cannot be placed
     *
     * @return number of slot
     */
    int nTimeslotNoWay() {
        Map<Integer, Timeslot> all = new HashMap<>();
        for (Map.Entry<Integer, Exam> entry : exmConflict.entrySet()) {
            if (entry.getValue().getTimeslot() != null)
                all.put(entry.getValue().getTimeslot().getSloID(), entry.getValue().getTimeslot());
        }
        return all.size();
    }

    /**
     * Number of conflict of the exam
     *
     * @return
     */
    int nConflict() {
        return exmConflict.size();
    }

    /**
     * Schedule exam in timeslot t
     *
     * @param t timeslot
     */
    public void schedule(Timeslot t) {
        this.setScheduled(true);
        this.setTimeslot(t);
        t.addExam(this);
    }
    double costExam (int nStu) {
        double sum = 0;
        for (Map.Entry<Integer, Exam> e2 : this.exmConflict.entrySet()) {
            int d = Math.abs(e2.getValue().getTimeslot().getSloID() - this.getTimeslot().getSloID());
            if (d == 0) {
                System.out.println("Unfesible solution!! BAAAAAD");
                return Double.MAX_VALUE;

            }
            if (e2.getKey() > this.getExmID() && d < 6) {
                long nee = students.entrySet().stream().filter(s -> s.getValue().hasExam(this.getExmID())).filter(s -> s.getValue().hasExam(e2.getKey())).collect(Collectors.toList()).size();
                sum += Math.pow(2, 5 - d) * nee;
            }
        }
        return sum /nStu;
    }
    double costExamRemoving (int nStu) {
        double sum = 0;
        for (Map.Entry<Integer, Exam> e2 : this.exmConflict.entrySet()) {
            int d = Math.abs(e2.getValue().getTimeslot().getSloID() - this.getTimeslot().getSloID());
            if (d == 0) {
                System.out.println("Unfesible solution!! BAAAAAD");
                return Double.MAX_VALUE;

            }
            if (d < 6) {
                long nee = students.entrySet().stream().filter(s -> s.getValue().hasExam(this.getExmID())).filter(s -> s.getValue().hasExam(e2.getKey())).collect(Collectors.toList()).size();
                sum += Math.pow(2, 5 - d) * nee;
            }
        }
        return sum /nStu;

    }
    int getNStuConflict() {
        int stu = 0;
        for (Map.Entry<Integer, Exam> e2 : exmConflict.entrySet()) {
            int d = Math.abs(e2.getValue().getTimeslot().getSloID() - this.getTimeslot().getSloID());
            if (d < 6) {
                int nee = students.entrySet().stream().filter(s -> s.getValue().hasExam(this.getExmID())).filter(s -> s.getValue().hasExam(e2.getKey())).collect(Collectors.toList()).size();
                stu += nee;
            }
        }
        return stu;
    }


    @Override
    public int compareTo(Exam o) {
        if(this.nConflict()>o.nConflict()) return -1;
        if(this.nConflict()<o.nConflict()) return 1;
        return 0;
    }
}
