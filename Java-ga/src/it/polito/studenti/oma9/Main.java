package it.polito.studenti.oma9;

import io.jenetics.*;
import io.jenetics.engine.*;

public class Main {

    public static void main(String[] args) {
        int S = 8;
        int e = 4;
        int ts = 6;
        int[][] conflicts = new int[e][e];

        conflicts[1][2] = 2;
        conflicts[2][1] = 2;
        conflicts[1][3] = 2;
        conflicts[3][1] = 2;
        conflicts[2][3] = 2;
        conflicts[3][2] = 2;

        Timetabling problem = new Timetabling(S, e, ts, conflicts);

        Engine<IntegerGene, Double> engine = Engine.builder(problem)
            .minimizing()
            .maximalPhenotypeAge(5)
            .alterers(new MeanAlterer<>(0.4), new Mutator<>(0.3))
            .build();

        Phenotype<IntegerGene, Double> result = engine.stream()
            .limit(100)
            .collect(EvolutionResult.toBestPhenotype());

        System.out.println(result);
    }
}
