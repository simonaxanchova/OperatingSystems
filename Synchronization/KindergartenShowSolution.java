На претстава во градинка на која учествуваат 24 дечиња има само една бина на која има место за 6 учесници. 
Презентацијата на учесниците на бината се одвива во повеќе циклуси, каде во секој циклус на бината својата точка ја изведуваат 4 групи на учесници, една по една. 
Групите се составуваат произволно,според тоа дали има место на бината и како пристигнале учесниците. Претставата се одвива според следните правила:

Учесниците може да излезат на бината само доколку има слободни места
кога ќе се соберат 6 учесници на бината, започнуваат со својата точка
откако ќе завршат со точката, секој од учесниците чека да завршат останатите групи, пред да се обиде повторно да се качи на бината
нови играчи не смеат да влезат додека не излезат оние што претходно играле
потоа сценариото може да започне од почеток.

Во имплементацијата, треба да ги користите следните методи од веќе дефинираната променлива state:

state.participantEnter()
Го симболизира влегувањето на учесникот на бината.
Се повикува од сите учесници.
Доколку ист учесник го повика методот во рамките на ист циклус, ќе повика исклучок.
Доколку се повика кога бината е полна, ќе фрли исклучок.
Доколку се повика пред претходната група да заврши со презентирањето(state.endGroup()), ќе фрли исклучок.

state.present()
Го симболизира процесот на презентирање на учесниците.
Се повикува од сите учесници.
Доколку процесот на презентирање не се извршува паралелно, ќе добиете порака за грешка.
Доколку бината не е пополнета (нема точно 6 присутни учесници), ќе фрли исклучок.

state.endGroup()
Означува дека сите учесници од групата завршиле со презентирањето и може да излезат од бината.
Се повикува само од еден учесник.
Доколку во моменот на повикување има учесници кои се' уште се присутни на бината, ќе фрли исклучок.

state.endCycle()
Означува дека сите групи и учесници завршиле со презентирањето и може да започне следниот циклус.
Се повикува само од еден учесник.
Доколку во моменот на повикување има учесници кои се' уште не завршиле, ќе фрли исклучок.

__________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class KindergartenShowSolution {

    public static final int GROUP_SIZE = 6;
    public static final int TOTAL = 24;

    private static Semaphore seats = new Semaphore(6, false);
    private static Semaphore canPlay = new Semaphore(0, false);
    private static Semaphore newCycle = new Semaphore(0, false);
    private static Semaphore lock = new Semaphore(1, false);
    private static int groupNo = 0;
    private static int totalNo = 0;
    private static int sumPermits = 0;
    private static int numExecutions = 0;
    private static int sumQueue = 0;



    public static void init() {
    }

    public static class Child extends TemplateThread {

        public Child(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            seats.acquire();
            state.participantEnter();
            lock.acquire();
            groupNo++;
            if (groupNo == GROUP_SIZE){
                canPlay.release(GROUP_SIZE);
            }
            lock.release();

            canPlay.acquire();
            state.present();
            lock.acquire();
            groupNo--;
            totalNo++;
            if (groupNo == 0){
                state.endGroup();
                seats.release(GROUP_SIZE);
            }
            if (totalNo == TOTAL){
                state.endCycle();
                newCycle.release(TOTAL);
                totalNo = 0;
            }
            lock.release();

            newCycle.acquire();

        }

    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    static KindergartenShowState state = new KindergartenShowState();

    public static void run() {
        try {
            int numRuns = 24;
            int numIterations = 24;
            numExecutions = 0;
            sumPermits = 0;
            sumQueue = 0;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Child c = new Child(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(((double) sumPermits) / numExecutions);
            System.out.println(((double) sumQueue) / numExecutions);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}