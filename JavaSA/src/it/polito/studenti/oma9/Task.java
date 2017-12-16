package it.polito.studenti.oma9;

import java.util.concurrent.Callable;

class Task implements Callable<String> {
    public Task() {
    }

    @Override
    public String call() throws Exception {
        Thread.sleep(4000); // Just to demo a long running task of 4 seconds.
        return "Ready!";
    }
}