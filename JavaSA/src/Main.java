import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public class Main {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Temporal start = LocalTime.now();
        Duration duration;
        SA optimization = new SA();
        Data data;
        boolean secondsIsNext = false;
        int seconds = 0;
        String instance = "";

        for (String arg : args) {
            if (secondsIsNext) {
                seconds = Integer.parseInt(arg);
                secondsIsNext = false;
            } else if (arg.equals("-t")) {
                secondsIsNext = true;
            } else {
                instance = arg;
            }
        }

        if (seconds <= 0) {
            System.out.println("Invalid number of seconds to run: " + seconds);
            return;
        }

        if (instance.length() == 0) {
            System.out.println("No instance name provided");
            return;
        }

        try {
            // dalla cartella Java-ga, si può invocare "questo-file.jar ../instances/instance01 -t 30"
            // (o mettere "../instances/instance01 -t 30" nei parametri della run configuration di Intellij...)
            // e va a prendere l'istanza nella cartella giusta
            data = new Data(instance);
            data.createSolution();

        } catch (FileNotFoundException e) {
            System.out.println("Missing files for instance " + instance);
            return;
            //e.printStackTrace();
        }

        Data x = null;
        try {
            x = optimization.startOptimization(data, data.evaluateSolution(), 200, 0.9 , 10, startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        x.printSolution();
    }
}
