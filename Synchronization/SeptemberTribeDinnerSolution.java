Едно племе јаде заеднична вечера од голем казан во кој има ограничен број порции. Доколку има храна во казанот, секој член од племето сам се послужува и 
седнува на трпезата да јаде. Доколку казанот е празен, се повикува готвачот за да зготви нов казан со храна и сите кои не започнале да јадат чекаат додека 
готвачот не ги извести дека вечерата е готова. Притоа, трпезата има место за максимум четворица, што значи дека максимум четворица истовремено може да јадат. 
Доколку нема место на трпезата, членовите чекаат додека не се ослободи.

Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирани се класите TribeMember и Chef, кои го симболизираат однесувањето на членовите на племето и готвачот, соодветно. 
Во имплементацијата, треба да ги користите следните методи од веќе дефинираната променлива state:

state.isPotEmpty()
Враќа boolean кој означува дали казанот е празен.
Се повикува од сите членови на племето.
Доколку повеќе членови паралелно проверуваат, ќе добиете порака за грешка.

state.fillPlate()
Го симболизира земањето храна од казанот.
Се повикува од сите членови на племето.
Доколку казанот е празен, ќе добиете порака за грешка.
Доколку повеќе членови паралелно земаат храна, ќе добиете порака за грешка.

state.eat()
Го симболизира јадењето на трпезата на членовите на племето.
Се повикува од сите членови на племето.
Доколку повеќе од четворица паралелно јадат, ќе добиете порака за грешка.
Доколку методот се извршува секвенцијално (само од еден член во еден момент), ќе добиете порака за грешка.

state.cook()
Го симболизира готвењето на храна во казанот од страна на готвачот.
Се повикува само од готвачот.
Доколку методот се повика, а казанот не е празен, ќе добиете порака за грешка.

____________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class SeptemberTribeDinnerSolution {

    static Semaphore table = new Semaphore(4);
    static Semaphore lock = new Semaphore(1);
    static Semaphore empty = new Semaphore(0);
    static Semaphore filled = new Semaphore(0);

    public static void init() {

    }

    public static class TribeMember extends TemplateThread {

        public TribeMember(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            lock.acquire();
            //vrakja dali e prazen kazanot
            if (state.isPotEmpty()){
                empty.release();
                filled.acquire();
            }
            //metod za polnenje na cinijata
            state.fillPlate();
            lock.release();

            table.acquire();
            //dokolku ima mesto na trpezata, treba da se povika
            state.eat();
            table.release();
        }

    }

    public static class Chef extends TemplateThread {

        public Chef(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            empty.acquire();
            state.cook();
            filled.release();
        }

    }

    static SeptemberTribeDinnerState state = new SeptemberTribeDinnerState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 150;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                TribeMember h = new TribeMember(numRuns);
                threads.add(h);
            }

            Chef chef = new Chef(10);
            threads.add(chef);

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

