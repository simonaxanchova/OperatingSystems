C2H2O4
Во една фабрика потребно е производство на оксална киселина: C2H2О4

Во процесот на производство, се чека сите атоми да пристигнат во комората за поврзување, по што се дава сигнал за нивно поврзување. 
Имате бесконечна количина од кислород, водород и јаглерод. Молекулите на C2H2О4 се формираат една по една.

Потребно е да го синхронизирате креирањето на оксалната киселина (C2H2О4) со користење на следните функции:

state.bond() - Кажува дека може да се формира C2H2О4 молекулата
Треба да се повика од сите атоми кои учествуваат во процесот на креирање на молекулата.
Доколку не се присутни сите потребни атоми, ќе добиете порака за грешка.
Доколку методот е повикан од повеќе од 4 кислородни, 2 водородни и 2 јаглеродни атоми, ќе добиете порака за грешка.
state.validate() - Проверува дали молекулата е формирана успешно
Се повикува само од еден атом по креирањето на молекулата
вие одлучете од кој атом (отстранете го овој повик од execute() методот на другите две класи).
Доколку не е формирана молекулата (не е завршен state.bond() методот кај сите атоми), ќе добиете порака за грешка.

__________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class OxalicAcidSolution {

    //za aktivacija na atomite
    private static Semaphore c = new Semaphore(2);
    private static Semaphore h = new Semaphore(2);
    private static Semaphore o = new Semaphore(4);

    //notifikacija za prisustvo
    private static Semaphore cHere = new Semaphore(0);
    private static Semaphore hHere = new Semaphore(0);

    private static Semaphore ready = new Semaphore(0);
    private static Semaphore done = new Semaphore(0);

    public static int oNum = 0;


    public static void init() {


    }


    public static class Carbon extends TemplateThread {

        public Carbon(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: Synchronize Carbon
            c.acquire();
            cHere.release();
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
            //TODO: Synchronize Hydrogen
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
            //TODO: Synchronize Oxygen
            o.acquire();
            synchronized (Oxygen.class){
                oNum++;
                if (oNum == 4){
                    hHere.acquire(2);
                    cHere.acquire(2);
                    oNum = 0;
                    ready.release(8);
                }
            }
            ready.acquire();
            state.bond();
            done.release();
            synchronized (Oxygen.class){
                oNum++;
                if (oNum == 4){
                    done.acquire(8);
                    state.validate();
                    o.release(4);
                    c.release(2);
                    h.release(2);
                    oNum = 0;
                }
            }
        }
    }


    static OxalicAcidState state = new OxalicAcidState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 300;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                Oxygen o = new Oxygen(numRuns);

                threads.add(o);
                if (i % 2 == 0) {
                    Hydrogen h = new Hydrogen(numRuns);
                    Carbon c = new Carbon(numRuns);
                    threads.add(c);
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