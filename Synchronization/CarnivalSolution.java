Carnival Problem
На еден карневал на кој има 30 учесници има само една бина на која има место за 10 учесници. 
Презентацијата на учесниците на бината се одвива во повеќе циклуси, каде во секој циклус на бината својата точка ја изведуваат 3 групи на учесници, една по една. 
Групите се составуваат произволно,според тоа дали има место на бината и како пристигнале учесниците. Карневалот се одвива според следните правила:

Учесниците може да излезат на бината само доколку има слободни места
кога ќе се соберат 10 учесници на бината, започнуваат со својата точка
откако ќе завршат со точката, секој од учесниците чека да завршат останатите групи, пред да се обиде повторно да се качи на бината
нови играчи не смеат да влезат додека не излезат оние што претходно играле
потоа сценариото може да започне од почеток
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирана е класа Participant, која го симболизира однесувањето на учесниците на карневалот. 
Има 30 инстанци од класата Participant кај кои методот execute() се повикува онолку пати колку што има циклуси на карневалот.

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
Доколку бината не е пополнета (нема точно 10 присутни учесници), ќе фрли исклучок.

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

import zadachi.PointsException;
import zadachi.TemplateThread;


import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class CarnivalSolution {

    public static final int GROUP_SIZE = 10;
    public static final int TOTAL = 30;

    private static Semaphore seats;
    private static Semaphore canPlay;
    private static Semaphore newCycle;
    private static Semaphore lock;

    private static int groupNo;
    private static int totalNo;


    public static void init() {
        seats = new Semaphore(10);
        canPlay = new Semaphore(0);
        newCycle = new Semaphore(0);
        lock = new Semaphore(1);
        groupNo = 0;
        totalNo = 0;


    }

    public static class Participant extends TemplateThread {

        public Participant(int numRuns) {
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

    static CarnivalState state = new CarnivalState();

    public static void run() {
        try {
            int numRuns = 15;
            int numThreads = 30;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numThreads; i++) {
                Participant c = new Participant(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
