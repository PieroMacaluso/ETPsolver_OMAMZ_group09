package it.polito.studenti.oma9;

class Data {
    // TODO:
    // - leggi file
    // - crea array e matrici e roba varia

    private void creaMatrice() {
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
