H2S2О5
Во една фабрика потребно е производство на дисулфурна киселина: H2S2О5

Во процесот на производство, се чека сите атоми да пристигнат во комората за поврзување, по што се дава сигнал за нивно поврзување. 
Имате бесконечна количина од кислород, водород и сулфур. Молекулите на H2S2О5 се формираат една по една.

Потребно е да го синхронизирате креирањето на дисулфурната киселина (H2S2О5) со користење на следните функции:

state.bond() - Кажува дека може да се формира H2S2О5 молекулата
Треба да се повика од сите атоми кои учествуваат во процесот на креирање на молекулата.
Доколку не се присутни сите потребни атоми, ќе добиете порака за грешка.
Доколку методот е повикан од повеќе од 5 кислородни, 2 водородни и 2 сулфурни атоми, ќе добиете порака за грешка.

state.validate() - Проверува дали молекулата е формирана успешно
Се повикува само од еден атом по креирањето на молекулата
вие одлучете од кој атом (отстранете го овој повик од execute() методот на другите две класи).
Доколку не е формирана молекулата (не е завршен state.bond() методот кај сите атоми), ќе добиете порака за грешка.


____________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class DisulfurousAcidSolution {

    //H2S2O5
    static Semaphore h = new Semaphore(2);
    static Semaphore s = new Semaphore(2);
    static Semaphore o = new Semaphore(5);

    static Semaphore ready = new Semaphore(0);
    static Semaphore done = new Semaphore(0);

    static Semaphore hHere = new Semaphore(0);
    static Semaphore sHere = new Semaphore(0);

    static int oCount = 0;


    public static void init() {


    }


    public static class Sulfur extends TemplateThread {
        //H2S2O5
        public Sulfur(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: Synchronize Sulfur
            s.acquire();
            sHere.release();
            ready.acquire();
            state.bond();
            done.release();
        }
    }

    public static class Hydrogen extends TemplateThread {

        public Hydrogen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            h.acquire();
            hHere.release();
            ready.acquire();
            state.bond();
            done.release();
        }

    }

    public static class Oxygen extends TemplateThread {

        public Oxygen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //H2S2O5
            o.acquire();
            synchronized (Oxygen.class){
                oCount++;
                if (oCount == 5){
                    hHere.acquire(2);
                    sHere.acquire(2);
                    oCount = 0;
                    ready.release(9);
                }
            }
            ready.acquire();
            state.bond();
            done.release();
            synchronized (Oxygen.class){
                oCount++;
                if (oCount == 5){
                    done.acquire(9);
                    state.validate();
                    h.release(2);
                    s.release(2);
                    o.release(5);
                    oCount = 0;
                }
            }
        }

    }


    static DisulfurousAcidState state = new DisulfurousAcidState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 100;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                for (int j = 0; j < 5; j++) {
                    Oxygen o = new Oxygen(numRuns);
                    threads.add(o);
                }
                for (int j = 0; j < 2; j++) {
                    Hydrogen h = new Hydrogen(numRuns);
                    Sulfur s = new Sulfur(numRuns);
                    threads.add(s);
                    threads.add(h);

                }

            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}