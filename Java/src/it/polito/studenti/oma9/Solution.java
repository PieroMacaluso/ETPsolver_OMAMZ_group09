package it.polito.studenti.oma9;

public class Solution implements Comparable<Solution> {

    /* TODO:
    - f che calcola la penalty
    - f che controlla feasibility
    - f di swap
    - f di rollback (che ripristina anche penalty e cose varie che vanno memorizzate da qualche parte qui dentro)
    - f di "swap" del GA (e relativa rollback)?
    */

    @Override
    public String toString() {
        // TODO: spara fuori il formato giusto da mettere nel file
        return super.toString();
    }

    @Override
    protected Solution clone() throws CloneNotSupportedException {
        super.clone();
        // TODO: implement
        return null;
    }

    /**
     * Qual e' migliore?
     *
     * @param solution l'altra
     * @return numeri.
     */
    @Override
    public int compareTo(Solution solution) {
        return 0;
    }
}
