import java.io.Serializable;
import java.util.*;

class LS implements Serializable{
    void startOptimization(Data ffs) {
        Timeslot bestSlo;
        for (Map.Entry en : entriesSortedByValues(ffs.getExams())) {
            Exam e = (Exam) en.getValue();
            bestSlo = e.getTimeslot();
            Map<Integer, Timeslot> sloA = e.timeslotAvailable(ffs.getTimeslots());
            for (Timeslot s:sloA.values()) {
                double oldC = e.costExamRemoving(ffs.nStu);
                e.unschedule();
                e.schedule(s);
                double newC = e.costExamRemoving(ffs.nStu);
                if (newC < oldC) bestSlo = s;

            }
            e.unschedule();
            e.schedule(bestSlo);
        }
        ffs.evaluateSolution();
    }
    static <K,V extends Comparable<? super V>>

    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());

                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
