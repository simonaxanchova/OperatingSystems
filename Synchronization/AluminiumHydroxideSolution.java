Al(OH)3
Во една фабрика потребно е производство на алуминиум хидроксид - Al(OH)3

Во процесот на производство, прво паралелно се формираат трите OH групи (секоја составена од по еден атом на кислород (O) и водород (H), 
за потоа да се поврзат со атомот на алуминиум (Al). Имате бесконечна количина од кислород, водород и алуминиум. Молекулите на Al(OH)3 се формираат една по една.

Потребно е да го синхронизирате креирањето на алуминиум хидроксид (Al(OH)3) со користење на следните функции:

state.bondOH() - Кажува дека може да се формира OH група
Се повиква само кај кислородните и водородните атоми.
Треба да се повика истовремено од сите кислородни и водородни атоми кои учествуваат во процесот на формирање на една молекула Al(OH)3, за да се формираат нејзините OH групи.
Треба трите OH групи од молекулата да се креираат паралелно (во спротивен случај ќе добиете порака за грешка).
При повикот, водородните атоми треба да се сигурни дека има присутен кислороден атом и обратно.
Доколку методот истовремено го повикаат повеќе од три пара кислородни и водородни атоми, ќе добиете порака за грешка.

state.bondAlOH() - Кажува дека може да се формира Al(OH)3 молекулата
Треба да се повика од сите атоми кои учествуваат во процесот на креирање на молекулата.
Доколку претходно не се формирани трите OH групи, ќе добиете порака за грешка.
Доколку методот е повикан од повеќе од 3 кислородни, 3 водородни и 1 алуминиумов атом, ќе добиете порака за грешка.

state.validate() - Проверува дали молекулата е формирана успешно
Се повикува само од еден атом по креирањето на молекулата
вие одлучете од кој атом (отстранете го овој повик од execute() методот на другите две класи).
Доколку не се присутни три OH групи и атом на алуминиум во процесот на спојување на молекулата (state.bondAlOH()), ќе добиете порака за грешка.

____________________________________________________________________________________________________________________________________________________________________________________

package zadachi;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class AluminiumHydroxideSolution {
    //TODO: AL(OH)3

    static Semaphore al;
    static Semaphore o;
    static Semaphore h;

    static Semaphore ohHere;
    static Semaphore oHere;

    static Semaphore ohReady;
    static Semaphore ready;
    static Semaphore done;
    static Semaphore next;

    public static void init() {
        al = new Semaphore(1);
        o = new Semaphore(3);
        h = new Semaphore(3);

        oHere = new Semaphore(0);
        ohHere = new Semaphore(0);

        ohReady = new Semaphore(0);
        ready = new Semaphore(0);
        done = new Semaphore(0);
        next = new Semaphore(0);
    }

    public static class Hydrogen extends TemplateThread {

        public Hydrogen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: AL(OH)3
            h.acquire();
            oHere.acquire();
            ohReady.release();
            state.bondOH();
            ohHere.release();
            ready.acquire();
            state.bondAlOH3();
            done.release();
            next.acquire();
            h.release();
        }

    }

    public static class Oxygen extends TemplateThread {

        public Oxygen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: AL(OH)3
            o.acquire();
            oHere.release();
            ohReady.acquire();
            state.bondOH();
            ohHere.release();
            ready.acquire();
            state.bondAlOH3();
            done.release();
            next.acquire();
            o.release();
        }
    }

    public static class Aluminium extends TemplateThread {

        public Aluminium(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //TODO: AL(OH)3
            al.acquire();
            ohHere.acquire(6);
            ready.release(6);
            state.bondAlOH3();
            done.acquire(6);
            next.release(6);
            state.validate();
            al.release();
        }

    }

    static AluminiumHydroxideState state = new AluminiumHydroxideState();

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
                Hydrogen h = new Hydrogen(numRuns);
                threads.add(o);
                if (i % 3 == 0) {
                    Aluminium al = new Aluminium(numRuns);
                    threads.add(al);
                }
                threads.add(h);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
