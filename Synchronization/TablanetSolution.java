На еден турнир во табланет на кој има 20 учесници има само една маса на која има место за 4 играчи. 
Турнирот се одвива во повеќе циклуси, каде во секој циклус играат 5 групи од играчи, една по една. 
Групите се составуваат произволно, според тоа дали има место на масата и како пристигнале играчите. Турнирот се одвива според следните правила:

Играчите може да седнат на масата само доколку има слободни места
кога ќе се соберат 4 играчи, го повикуваат дилерот да ги подели картите
кога дилерот ќе ги подели картите, играчите започнуваат да играат табланет
откако ќе завршат со играта, секој од играчите чека да завршат останатите групи, пред да се обиде повторно да игра
нови играчи не смеат да седнат на масата додека не излезат оние што претходно играле
потоа сценариото може да започне од почеток
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирани се класите Player и Dealer, кои го симболизираат однесувањето на играчите и дилерот на турнирот. 
Има 20 инстанци од класата Player кај кои методот execute() се повикува онолку пати колку што има циклуси на турнирот и 
една инстанца од класата Dealer чии што execute() метод се повикува повеќе пати (еднаш за секоја група која ќе игра табланет).

Во имплементацијата, треба да ги користите следните методи од веќе дефинираната променлива state:

state.playerSeat()
Го симболизира седнувањето на играчот на масата.
Се повикува од сите играчи.
Доколку ист играч го повика методот во рамките на ист циклус, ќе повика исклучок.
Доколку се повика кога масата е полна, ќе фрли исклучок.
Доколку се повика пред претходната група да заврши со играта (state.endRound()), ќе фрли исклучок.

state.dealCards()
Го симболизира делењето на картите од страна на дилерот
Се повикува само од дилерот
Доколку масата не е пополнета (нема точно 4 присутни играчи), ќе фрли исклучок.

state.play()
Го симболизира процесот на играње табланет.
Се повикува од сите играчи.
Доколку процесот на играње не се извршува паралелно, ќе фрли исклучок.
Доколку картите не се поделени (не е повикан state.dealCards()), ќе фрли исклучок.

state.nextGroup()
Означува дека сите играчи од групата завршиле со играњето и може да станат од масата.
Се повикува само од дилерот.
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

public class TablanetSolution {

    private static Semaphore seats;
    private static Semaphore playerHere;
    private static Semaphore canPlay;
    private static Semaphore playerFinished;
    private static Semaphore newCycle;
    private static Semaphore lock;
    private static int totalNo;


    public static void init() {
        seats = new Semaphore(4);
        playerHere = new Semaphore(0);
        canPlay = new Semaphore(0);
        playerFinished = new Semaphore(0);
        newCycle = new Semaphore(0);
        lock = new Semaphore(1);
        totalNo = 0;

    }

    public static class Dealer extends TemplateThread {

        public Dealer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            playerHere.acquire(4);
            state.dealCards();
            canPlay.release(4);
            playerFinished.acquire(4);
            state.nextGroup();
            seats.release(4);
        }
    }

    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }


        @Override
        public void execute() throws InterruptedException {
            seats.acquire();
            state.playerSeat();
            playerHere.release();
            canPlay.acquire();
            state.play();
            playerFinished.release();
            lock.acquire();
            totalNo++;
            if (totalNo == 20){
                state.endCycle();
                newCycle.release(20);
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

    static TablanetState state = new TablanetState();

    public static void run() {
        try {
            int numCycles = 10;
            int numIterations = 20;

            HashSet<Thread> threads = new HashSet<Thread>();

            Dealer d = new Dealer(50);
            threads.add(d);
            for (int i = 0; i < numIterations; i++) {
                Player c = new Player(numCycles);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
