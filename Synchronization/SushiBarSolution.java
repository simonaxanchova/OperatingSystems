Sushi Bar Problem
Еден суши бар има место за 5 посетители. Доколку некој поестител дојде во барот кога има слободно место, тој може веднаш да седне, 
но доколку дојде во момент кога сите 5 места се зафатени, тоа значи дека групата e заедно дојдена и ќе мора да почека сите да станат за да може да седне 
(Внимание: Доколку еден посетител ја напушти групата не значи дека групата е готова т.е. доколку има посетители кои чекаат ќе мора да почекаат и другите членови да ја напуптат групата).

Во почетниот код кој е даден, дефинирана е класата Customer, која го симболизира однесувањето на посетителите на суши барот. 
Има повеќе инстанци од класата Customer кај кои методот execute() се повикува повеќе пати.

Во имплементацијата, можете да ги користите следните методи од веќе дефинираната променлива state:

- state.seat()
•Служи за проверка дали има место во барот и доколку има место седнува на масата. Доколку се повика додека барот е полн или пак додека групата сеуште е присутна ќе фрли исклучок.

- state.groupGathered()
•Го симболизира собирањето на групата. Се повикува од 5тиот член кога ќе седне на масата. Доколку се повика кога на масата нема 5 посетители ќе фрли исклучок.

- state.eat()
•Го симболизира времето додека посетителот седи во барот.

- state.groupDone()
•Го симболизира излагањето на групата од барот. Се повикува од последниот член на групата кога го напушта барот. Доколку се повика кога на масата сеуште има посетители ќе фрли исклучок.

- state.done()
•Го симболизира напуштањето на посетител од барот. Се повикува од секој посетител кој го напушта барот освен од последниот член на групата кој оваа функција ја извршува со методот state.groupDone(). 
Доколку се повика додека нема никој присутен во барот ќе фрли исклучок.

_____________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class SushiBarSolution {

    static SushiBarState state = new SushiBarState();

    static Semaphore customers;
    static int numOfPeopleOnTable;
    static Object lock;
    static boolean fullTable;


    public static void init() {
        customers = new Semaphore(5);
        numOfPeopleOnTable = 0;
        lock = new Object();
        fullTable = false;
    }

    public static class Customer extends TemplateThread {

        public Customer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //semafor koj oznacuva kolku mesta ima slobodni vo barot
            customers.acquire();

            state.seat();

            //zgolemuvanje na brojot na lugje vo barot i proverka dali ima moznost za sozdavanje grupa
            synchronized (lock){
                numOfPeopleOnTable++;
                if(numOfPeopleOnTable == 5){
                    fullTable = true;
                    state.groupGathered();
                }
            }

            //posetitelot jade
            state.eat();

            //zaminuvanje na posetitelot od barot i proverka dali e posleden clen na grupata
            //dokolku e, go izvestuva barot deka barot ne e zafaten od grupata
            synchronized (lock){
                numOfPeopleOnTable--;

                if(numOfPeopleOnTable == 0 && fullTable){
                    fullTable = false;
                    state.groupDone();
                    customers.release(5);
                }else if(numOfPeopleOnTable > 0 && fullTable){
                    state.done();
                }else{
                    state.done();
                    customers.release();
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 1200;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Customer c = new Customer(numRuns);
                threads.add(c);
            }

            init();

            ProblemExecution.start(threads, state);
            // System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


