H4P2О6
Во една фабрика потребно е производство на хипофосфорна киселина: H4P2О6

Во процесот на производство, се чека сите атоми да пристигнат во комората за поврзување, по што се дава сигнал за нивно поврзување. 
Имате бесконечна количина од кислород, водород и фосфор. Молекулите на H4P2О6 се формираат една по една.

Потребно е да го синхронизирате креирањето на хипофосфорната киселина (H4P2О6) со користење на следните функции:

state.bond() - Кажува дека може да се формира H4P2О6 молекулата
Треба да се повика од сите атоми кои учествуваат во процесот на креирање на молекулата.
Доколку не се присутни сите потребни атоми, ќе добиете порака за грешка.
Доколку методот е повикан од повеќе од 6 кислородни, 4 водородни и 2 фосфорни атоми, ќе добиете порака за грешка.

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

public class HypophosphoricAcidSolution {
    //H4P2O6

    static Semaphore h = new Semaphore(4);
    static Semaphore p = new Semaphore(2);
    static Semaphore o = new Semaphore(6);

    static Semaphore hHere = new Semaphore(0);
    static Semaphore pHere = new Semaphore(0);

    static Semaphore ready = new Semaphore(0);
    static Semaphore done = new Semaphore(0);

    static Semaphore lock = new Semaphore(1);

    static int oCount = 0;


    public static void init() {


    }


    public static class Phosphorus extends TemplateThread {

        public Phosphorus(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //H4P2O6
            p.acquire();
            pHere.release();
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
            //H4P2O6
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
            //H4P2O6
            o.acquire();
            synchronized (Oxygen.class){
                oCount++;
                if (oCount == 6){
                    hHere.acquire(4);
                    pHere.acquire(2);
                    oCount = 0;
                    ready.release(12);
                }
            }
            ready.acquire();
            state.bond();
            done.release();
            synchronized (Oxygen.class){
                oCount++;
                if (oCount == 6){
                    done.acquire(12);
                    state.validate();
                    h.release(4);
                    p.release(2);
                    o.release(6);
                    oCount = 0;
                }
            }
        }

    }


    static HypophosphoricAcidState state = new HypophosphoricAcidState();

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
                for (int j = 0; j < state.O_ATOMS; j++) {
                    Oxygen o = new Oxygen(numRuns);
                    threads.add(o);
                }
                for (int j = 0; j < state.H_ATOMS; j++) {
                    Hydrogen h = new Hydrogen(numRuns);
                    threads.add(h);
                }

                for (int j = 0; j < state.P_ATOMS; j++) {
                    Phosphorus p = new Phosphorus(numRuns);
                    threads.add(p);
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
