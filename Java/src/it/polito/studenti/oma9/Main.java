package it.polito.studenti.oma9;

import java.io.*;
//import java.io.BufferedReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Hello wooooorld");
        System.out.println(new File(".").getAbsoluteFile());
	    Data d = new Data();
        try {
            d.startRead();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // TODO: fare 1 iterazione di genetic, tot di swap o altre ottimizzazioni, piu' si va avanti piu' farne di swap (bisogna misurare il teeeeempo)
    }

    /**
     * Crea una soluzione iniziale fattibile, in qualche modo greedy.
     *
     * @param data Dati di partenza
     * @return soluzione feasible ma che fa schifo
     */
    public static Solution greedyStart(Data data) {
        return null;
    }

    /**
     * Applica l'algoritmo genetico
     *
     * @param p1 parent 1
     * @param p2 parent 2
     */
    public void genetic(Solution p1, Solution p2) {
    }

    /**
     * Effettua swap "a caso" per migliorare la soluzione, restando nel neighborhood
     *
     * @param sol una soluzione
     */
    public void optimize(Solution sol) {
    }
}
