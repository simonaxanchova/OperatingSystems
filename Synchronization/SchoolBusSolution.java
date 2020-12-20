School Bus
Од ФИНКИ ве ангажираат за синхронизација на процесот на пренос на студенти на екскурзија, каде повеќе возачи ќе пренесуваат студенти со ист автобус. Екскурзијата се изведува во повеќе термини, каде во секој термин мора да има присутно еден возач и 50 студенти. По завршувањето на терминот, од автобусот прво излегуваат студентите и возачот, а потоа влегува нов возач и нови 50 студенти.

Притоа важат следните услови:

Во автобусот може да има само еден возач и точно 50 студенти.
Студентите не смеат да влезат ако во автобусот нема возач
Студентите не смеат да излезат додека возачот не каже дека автобусот пристигнал
Возачот не може да излезе додека има студенти во автобусот
Автобусот иницијално е празен
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирани се класите Driver и Student, кои го симболизираат однесувањето на возачите и студентите, соодветно. Има повеќе инстанци од двете класи кај кои методот execute() се повикува само еднаш.

Во имплементацијата, можете да ги користите следните методи од веќе дефинираната променлива state:

state.driverEnter()
Означува дека возачот влегува во автобусот.
Се повикува од сите возачи.
Доколку автобусот не е празен во моментот на повикувањето, ќе се јави исклучок.

state.passengerEnter()
Означува дека студентот влегува во автобусот.
Се повикува од сите студенти.
Доколку нема возач во автобусот (претходно не е повикан state.driverEnter()), или има повеќе од 50 студенти внатре, ќе се јави исклучок.
Доколку студентите не влегуваат паралелно (повеќе истовремено), ќе јави исклучок.

state.busDeparture()
Го симболизира тргнувањето на автобусот.
Се повикува од сите возачи по влегувањето на сите 50 студенти.
Доколку нема 50 присутни студенти во автобусот, ќе се јави исклучок.

state.busArrive()
Го симболизира пристигнувањето на автобусот.
Се повикува од сите возачи.
Доколку претходно не е повикан state.busDeparture(), ќе јави исклучок.

state.passengerLeave()
Го симболизира излегувањето на студентот од автобусот.
Се повикува од сите студенти.
Доколку се повика пред state.busArrive(), или ако претходно излегол возачот, ќе се јави исклучок.

state.driverLeave()
Го симболизира излегувањето на возачот од автобусот.
Се повикува од сите возачи.
Доколку методот се повика, а сеуште има студенти во автобусот, ќе добиете порака за грешка.

_________________________________________________________________________________________________________________________________________________________________________________

package zadachi;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;


public class SchoolBusSolution {

    static int count = 0;
    static Semaphore lock;
    static Semaphore driver;
    static Semaphore passenger;
    static Semaphore departure;
    static Semaphore passengerLeave;
    static Semaphore driverLeave;


    public static void init() {
        lock = new Semaphore(1);
        driver = new Semaphore(1);
        passenger = new Semaphore(0);
        departure = new Semaphore(0);
        passengerLeave = new Semaphore(0);
        driverLeave = new Semaphore(0);

    }

    public static class Driver extends TemplateThread {

        public Driver(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            driver.acquire();
            lock.acquire();
            state.driverEnter();
            passenger.release(50);
            lock.release();

            departure.acquire();

            lock.acquire();
            state.busDeparture();
            state.busArrive();
            passengerLeave.release(50);
            lock.release();

            driverLeave.acquire();
            lock.acquire();
            state.driverLeave();
            driver.release();
            lock.release();
        }
    }

    public static class Student extends TemplateThread {

        public Student(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            passenger.acquire();
            state.passengerEnter();
            lock.acquire();
            count++;
            if (count == 50){
                count = 0;
                departure.release();
            }
            lock.release();

            passengerLeave.acquire();
            lock.acquire();
            state.passengerLeave();
            count++;
            if (count == 50){
                driverLeave.release();
                count = 0;
            }
            lock.release();
        }
    }
    static SchoolBusState state = new SchoolBusState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 1000;
            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                Student p = new Student(numRuns);
                threads.add(p);
                if (i % 50 == 0) {
                    Driver c = new Driver(numRuns);
                    threads.add(c);
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


