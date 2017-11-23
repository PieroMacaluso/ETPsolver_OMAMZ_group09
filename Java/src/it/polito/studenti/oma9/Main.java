package it.polito.studenti.oma9;

import java.io.*;
//import java.io.BufferedReader;
import java.util.*;

public class Main {

    public static void main(String[] args) {
	    System.out.println("Hello wooooorld");

        int n = 5;
        int m = 7;
        int i, j;

        int[][] cose = new int[n][m];

        for(i = 0; i < n; i++) {
            for(j = 0; j < m; j++) {
                cose[i][j] = i*j;
            }
        }


        for(i = 0; i < n; i++) {
            for(j = 0; j < m; j++) {
                System.out.print(cose[i][j]);
                System.out.print(", ");
            }
            System.out.println("");
        }
    }
}
