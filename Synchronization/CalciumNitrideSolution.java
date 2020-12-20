Ca3N2
Во една фабрика потребно е производство на калциум нитрид - Ca3N2

Во процесот на производство треба да се присутни 3 калциумови (Ca) атоми и 2 азотни (N) атоми. Имате бесконечна количина од калциум и азот. 
Mолекулите на Ca3N2 се формираат една по една.

Потребно е да го синхронизирате креирањето на калциум нитрид со користење на следните функции:

state.bond() - Кажува дека може да се формира молекулата
Треба да се повика истовремено кај сите атоми.
Доколку не се повикува паралелно, ќе добиете порака за грешка.
Доколку методот истовремено го повикаат повеќе од три калциумови и два азотни атоми, ќе добиете порака за грешка.

state.validate() - Проверува дали молекулата е формирана успешно
Се повикува само од еден атом по креирањето на молекулата
вие одлучете од кој атом (отстранете го овој повик од execute() методот на другата класа).
Доколку не се присутни три калциумови и два азотни атоми во процесот на спојување на молекулата (state.bond()), ќе добиете порака за грешка.

___________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class CalciumNitrideSolution {
    //Ca3N2
    static Semaphore ca = new Semaphore(3);
    static Semaphore n = new Semaphore(2);

    static Semaphore nHere = new Semaphore(0);

    static Semaphore ready = new Semaphore(0);
    static Semaphore done = new Semaphore(0);

    static int caNum = 0;

    public static void init() {


    }

    public static class Calcium extends TemplateThread {

        public Calcium(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //Ca3N2
            ca.acquire();
            synchronized (Calcium.class){
                caNum++;
                if (caNum == 3){
                    nHere.acquire(2);
                    caNum = 0;
                    ready.release(5);
                }
            }
            ready.acquire();
            state.bond();
            done.release();
            synchronized (Calcium.class){
                caNum++;
                if (caNum == 3){
                    done.acquire(5);
                    state.validate();
                    ca.release(3);
                    n.release(2);
                    caNum = 0;
                }
            }
        }
    }

    public static class Nitrogen extends TemplateThread {

        public Nitrogen(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //Ca3N2
            n.acquire();
            nHere.release();
            ready.acquire();
            state.bond();
            done.release();
        }

    }

    static CalciumNitrideState state = new CalciumNitrideState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            Scanner s = new Scanner(System.in);
            int numRuns = 1;
            int numIterations = 100;
            s.close();

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Nitrogen n = new Nitrogen(numRuns);
                threads.add(n);
                Calcium ca = new Calcium(numRuns);
                threads.add(ca);
                ca = new Calcium(numRuns);
                threads.add(ca);
                n = new Nitrogen(numRuns);
                threads.add(n);
                ca = new Calcium(numRuns);
                threads.add(ca);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
