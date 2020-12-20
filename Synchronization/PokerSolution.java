Poker Game Problem
На еден покер турнир на кој има 15 учесници има само една маса на која има место за 5 играчи. 
Турнирот се одвива во повеќе циклуси, каде во секој циклус играат 3 групи од играчи, една по една. 
Групите се составуваат произволно, според тоа дали има место на масата и како пристигнале играчите. Турнирот се одвива според следните правила:

Играчите може да седнат на масата само доколку има слободни места
кога ќе се соберат 5 играчи, започнуваат да играат покер
откако ќе завршат со играта, секој од играчите чека да завршат останатите групи, пред да се обиде повторно да игра
нови играчи не смеат да влезат додека не излезат оние што претходно играле
потоа сценариото може да започне од почеток
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирана е класа Player, која го симболизира однесувањето на играчите на турнирот. 
Има 15 инстанци од класата Player кај кои методот execute() се повикува онолку пати колку што има циклуси на турнирот.

Во имплементацијата, треба да ги користите следните методи од веќе дефинираната променлива state:

state.playerSeat()
Го симболизира седнувањето на играчот на масата.
Се повикува од сите играчи.
Доколку ист играч го повика методот во рамките на ист циклус, ќе повика исклучок.
Доколку се повика кога масата е полна, ќе фрли исклучок.
Доколку се повика пред претходната група да заврши со играта (state.endRound()), ќе фрли исклучок.

state.play()
Го симболизира процесот на играње покер.
Се повикува од сите играчи.
Доколку процесот на играње не се извршува паралелно, ќе добиете порака за грешка.
Доколку масата не е пополнета (нема точно 5 присутни играчи), ќе фрли исклучок.

state.endRound()
Означува дека сите играчи од групата завршиле со играњето и може да станат од масата.
Се повикува само од еден играч.
Доколку во моменот на повикување има играчи кои се' уште се присутни на масата, ќе фрли исклучок.

state.endCycle()
Означува дека сите играчи завршиле со играњето и може да започне следниот циклус.
Се повикува само од еден играч.
Доколку во моменот на повикување не завршиле со игра сите играчи, ќе фрли исклучок.

__________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

public class PokerSolution {

    private static Semaphore seats = new Semaphore(5);
    private static Semaphore canPlay = new Semaphore(0);
    private static Semaphore newCycle = new Semaphore(0);
    private static Semaphore lock = new Semaphore(1);
    private static int groupNo = 0;
    private static int totalNo = 0;

    public static void init() {
    }

    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            seats.acquire();
            state.playerSeat();
            lock.acquire();
            groupNo++;
            if (groupNo == 5){
                canPlay.release(5);
            }
            lock.release();

            canPlay.acquire();
            state.play();
            lock.acquire();
            groupNo--;
            totalNo++;
            if (groupNo == 0){
                state.endRound();
                seats.release(5);
            }
            if (totalNo == 15){
                state.endCycle();
                newCycle.release(15);
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

    static PokerState state = new PokerState();

    public static void run() {
        try {
            int numRuns = 20;
            int numIterations = 15;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Player c = new Player(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}