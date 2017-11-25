package it.polito.studenti.oma9;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import static it.polito.studenti.oma9.Config.*;

class Data {
    private String defaultFilePath = "src/it/polito/studenti/oma9/Files/";
    private Map<Integer, Student> students = new TreeMap<>();
    private Map<Integer, Exam> exams = new TreeMap<>();
    private int nExm;
    private int nStu;
    private int nSlo;

    // Matrice di booleani con ordinate studenti e ascissa esami
    private boolean stuExm[][];
    private int conflictTable[][];

    private void creaMatrice() {
        int n = 5;
        int m = 7;
        int i, j;

        int[][] cose = new int[n][m];

        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                cose[i][j] = i * j;
            }
        }

        for (i = 0; i < n; i++) {
            for (j = 0; j < m; j++) {
                System.out.print(cose[i][j]);
                System.out.print(", ");
            }
            System.out.println("");
        }

    }

    /**
     * Funzione per leggere e memorizzare i dati dei file .stu e .exm
     */
    void startRead() throws FileNotFoundException {
        Scanner sStu = new Scanner(new File(defaultFilePath + filename + stu));
        Scanner sExm = new Scanner(new File(defaultFilePath +  filename + exm));

        Student cStu;
        Exam cExm;
        int currentStudent;
        int exmID = -1;
        int stuID = -1;

        // Lettura file .exm per trovare il numero di esami (exmID massimo) e per memorizzarli
        while (sExm.hasNextLine() ) {
            String line = sExm.nextLine();
            if (line.isEmpty()) {
                break;
            }
            String part[] = line.split(" ");
            exams.put(Integer.parseInt(part[0]), new Exam(Integer.parseInt(part[0])));
            if (Integer.parseInt(part[0]) > exmID) {
                exmID = Integer.parseInt(part[0]);
            }
        }
        nExm = exmID;

        // Lettura file .stu per trovare il numero di studenti (stuID Massimo) e per memorizzarli
        while (sStu.hasNextLine()) {
            String line = sStu.nextLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("s")) {
                String part[] = line.substring(1).split(" ");
                currentStudent = Integer.parseInt(part[0]);
                if (!students.containsKey(currentStudent)) {
                    students.put(currentStudent, new Student(currentStudent));

                }
                if (currentStudent> stuID) {
                    stuID = currentStudent;
                }
            }
        }
        nStu = stuID;

        // Lettura file .stu fare il binding tra esami e studenti
        stuExm = new boolean[nStu+1][nExm+1];
        sStu = new Scanner(new File(defaultFilePath + filename + stu));
        while(sStu.hasNextLine()){
            String line = sStu.nextLine();
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("s")) {
                String part[] = line.substring(1).split(" ");
                cStu = students.get(Integer.parseInt(part[0]));
                cExm = exams.get(Integer.parseInt(part[1]));
                cExm.addStudent(cStu);
                cStu.addExam(cExm);

                stuExm[cStu.getStuID()][cExm.getExmID()] = true;
            }
        }

        // Creazione della tabella di conflitti con l'utilizzo delle Key delle mappe e il metodo retainAll()
        conflictTable = new int[nExm+1][nExm+1];
        for(int i=1; i<=nExm; i++) {
            for(int j=i;j<=nExm; j++) {
                if (i==j){
                    conflictTable[i][j] = 0;
                    continue;
                }
                Set<Integer> s;
                s = new HashSet<>(exams.get(i).students.keySet());
                s.retainAll(exams.get(j).students.keySet());
                conflictTable[i][j] = s.size();
                conflictTable[j][i] = s.size();
            }
        }

        // Stampa a video della tabella dei conflitti per verifica
        for(int i=1; i<=nExm; i++) {
            for(int j=1;j<=nExm; j++) {
                System.out.print(conflictTable[i][j]+ "\t");
            }
            System.out.print("\n");
        }
        // Stampa a video della tabella dei booleani per verifica
        for(int i=1; i<=nStu; i++) {
            for(int j=1;j<=nExm; j++) {
                System.out.print(stuExm[i][j]+ "\t");
            }
            System.out.print("\n");
        }
    }
}
