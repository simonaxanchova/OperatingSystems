The Senate Bus Problem

Овој проблем е оригинално базиран на проблемот со автобусите на колеџот Wellesley. Патниците пристигнуваат на автобуска станица и чекаат автобус. 
Кога даден автобус ќе пристигне на автобуската станица сите патници кои моментално чекаат на станицата пробуваат да се качат во автобусот, 
но секој патник кој ќе пристигне по пристигнувањето на автобусот ќе мора да го чека следниот автобус. Капацитетот на секој од автобусите е 50 лица. 
Ако на автобуската станица чекаат повеќе од 50 патници, некои од нив ќе мора да го чекаат наредниот автобус за да се качат. 
Кога сите предвидени патници за тековниот автобус ќе се качат во истиот, тој си заминува од автобуската станица. 
Ако даден автобус пристигне во момент кога на автобуската станица не чека ниту еден патник, во тој случај автобусот треба веднаш да си замине.

Ограничувањa
- Не е дозволено повеќе од 50 патници да се качат во еден автобус.
- Не е дозволено повеќе од еден автобус да стои на автобуската станица во даден момент.
- Даден автобус не смее да ја напушти автобуската станица доколку во него не се качени сите патници(не повеќе од 50) кои првично биле на станицата кога тој пристигнал.
- Во даден автобус не е дозволено да влегуваат патници кои пристигнале на автобуската станица во момент кога тој веќе бил пристигнат на неа.
- Даден автобус не смее да стои на автобуската станица доколку при неготово пристигнување на неа немало ниту еден патник. Во ваквиот случај автобусот треба веднаш да си замине.
- Автобусот треба да ги повукува методите busArrives() и busDeparts().
- Патниците е потребно да ги повикуваат методите riderArrives() и riderBoardsBus().
Методи
Автобусот ги повикува методите:
- state.busArrives() - метода со која даден автобус пристигнува на автобуската станица
- state.busDeparts() - метода со која даден автобус си заминува од автобуската станица
Oваа метода не може да биде повикана доколку претходно не е повикана методата state.busArrives()


Патникот ги повикува методите:
- state.riderArrives()
метода со која еден патник пристигнува на автобуската станица
- state.riderBoardsBus()
метода со која даден патник, кој тековно чека на автобуската станица, се качува во автобусот кој се наоѓа на истата
оваа метода не може да биде повикана доколку претходно не е повикана методата state.riderArrives() за дадениот патник и state.busArrives() за соодветниот автобус

____________________________________________________________________________________________________________________________________________________________________________________
package zadachi;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class SenateBusSolution {
    
    //brojac koj gi broi pristignatite patnicni koi cekaat da se kacat vo prviot avtobus
    //koj ke pristigne posle niv
    static int waiting;
    
    //mutex koj go regulira pristignuvanjeto na patnicnite na avtobuskata stanica i kacuvanjeto
    //na patnicnite koi cekaat vo momentalno pristignatiot avtobus
    static Semaphore mutex;
    
    //semafor koj signalizira koga daden avtobus pristignuva na avtobuskata stanica
    static Semaphore bus;
    
    //semafor koj go signalizira kacuvanjeto na daden patnik vo avtobusot
    static Semaphore boarded;
    
    //promenliva koja pretstavuva broj na preostanati patnici koi ne si zaminale so daden avtobus
    static int ridersLeft;
    
    

    public static void init() {
        waiting = 0;
        mutex = new Semaphore(1);
        bus = new Semaphore(0);
        boarded = new Semaphore(0);
    }

    public static class Bus extends TemplateThread {

        public Bus(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            while(true) {
                //vzaemno isklucuvanje so pomos na mutex pri pristignuvanje na tekovniot avtobus
                mutex.acquire();
                //pristignuvanje na avtobus na avtobuskata stanica
                state.busArrives();
                //presmetuvanje na brojot na patnici koi ke treba da se kacat vo avtobusot koj
                //pristigna, pritoa nedozvoluvajki istiot da bide pogolem od 50
                int n = Math.min(waiting, 50);

                //odzemanje na brojot na patnici koi ke treba da se kacat vo avtobusot od brojot
                //na vkupnite preostanati patnici
                ridersLeft -= n;

                //signaliziranje i cekanje na sekoj od patnicite da se kacat vo avtobusot
                for (int i = 0; i < n; i++) {
                    bus.release();
                    boarded.acquire();
                }

                //presmetuvanje na brojot na preostanati patnici na stanicata koi cekaat da se kacat
                //vo sledniot avtobus koj ke pristigne
                waiting = Math.max((waiting - 50), 0);
                mutex.release();

                //zaminuvanje na avtobusot
                state.busDeparts();

                //proverka na brojot na preostanati patnici
                if(ridersLeft == 0){
                    break;
                }
            }
        }
    }

    public static class Rider extends TemplateThread {

        public Rider(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //vzaemno isklucuvanje so pomos na mutex pri pristignuvanje na patnik 
            mutex.acquire();
            //pristignuvanje na patnik na avtobuskata stanica
            state.riderArrives();
            //zgolemuvanje na brojot na pristignati patnici koi cekaat da se kacat vo prviot
            //avtobus koj ke pristigne posle niv
            waiting++;
            mutex.release();
            
            //cekanje na moment vo koj patnikot ke moze da se kaci vo tekovniot avtobus
            bus.acquire();
            //kacuvanje na patnikot vo avtobusot
            state.riderBoardsBus();
            //signaliziranje deka patnikot e kacen vo tekovniot avtobus
            boarded.release();
        }
    }

    static SenateBusState state = new SenateBusState();

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("Run: " + i);
            run();
        }
    }

    public static void run() {
        try {
            int numRiders = 125;
            ridersLeft = numRiders;
            HashSet<Thread> threads = new HashSet<>();

            int numRuns = 1;

            Bus bus = new Bus(numRuns);
            threads.add(bus);

            for (int i = 0; i < numRiders; i++) {
                Rider rider = new Rider(numRuns);
                threads.add(rider);
            }

            init();

            ProblemExecution.start(threads, state);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
