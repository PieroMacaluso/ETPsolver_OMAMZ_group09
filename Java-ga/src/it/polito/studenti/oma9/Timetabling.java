package it.polito.studenti.oma9;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.*;

import java.util.*;
import java.util.function.Function;

public class Timetabling implements Problem<int[], IntegerGene, Double> {
    private int S;
    private int e;
    private int[][] conflicts;
    private IntRange domain;

    public Timetabling(int S, int e, int ts, int[][] conflicts) {
        this.S = S;
        this.e = e;
        this.conflicts = conflicts;
        this.domain = IntRange.of(ts);
    }

    public Function<int[], Double> fitness() {
        return schedule -> {
            int exam, len = schedule.length;
            int distance;
            boolean feasible = true;

            SortedMap<Integer, Set<Integer>> TimeslotConflicts = new TreeMap<>();

            for(exam = 0; feasible && exam < len; exam++) {
                if(!TimeslotConflicts.containsKey(exam)) {
                    TimeslotConflicts.put(exam, new TreeSet<>());
                }
                TimeslotConflicts.get(exam).add(schedule[exam]);
                for(int eprime : TimeslotConflicts.get(exam)) {
                    if(eprime >= exam) {
                        break;
                    }
                    if(this.conflicts[e][eprime] > 0) {
                        feasible = false;
                        break;
                    }
                }
            }
            if(!feasible) {
                return Double.POSITIVE_INFINITY;
            }

            double penalty = 0;
            for(int i = 0; i < this.e; i++) {
                for(int j = 0; j < i; j++) {
                    if(this.conflicts[i][j] > 0) {
                        distance = Math.abs(schedule[i] - schedule[j]);
                        if(distance < 5) {
                            assert (distance > 0);
                            penalty += Math.pow(2.0, (double) distance) * (double) this.conflicts[i][j];
                        }
                    }
                }
            }
            return penalty / (double) this.S;
        };
    }

    public Codec<int[], IntegerGene> codec() {
        return Codecs.ofVector(domain, e);
    }
}
