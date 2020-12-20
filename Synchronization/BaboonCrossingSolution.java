Бабуни на јаже

1. Проблем
Некаде во националниот парк Кругер во Јужна Африка има голем кањон. Двете страни на кањонот ги спојува јаже. Бабуните можат да го преминат кањонот користејќи го јажето.
Доколку два бабуни кои одат во спротивни насоки се сретнат тогаш ќе настане борба и двата бабуни ќе паднат од јажето. 
Јажето може да издржи највеќе пет бабуни во исто време. Ако има повеќе од пет бабуни во даден момент, јажето ќе се скине.

2. Барања
Да претпоставиме дека може да ги научиме бабуните да користат семафори. Потребно е да се напише решение за синхронизација кое ќе ги задоволи следниве услови:

Откако еден бабун ќе се качи на јажето, тој треба да помине безбедно на другата страна без притоа да се сретне со некој друг бабун од спротивната насока.
На јажето во исто време може да има највеќе пет бабуни.
Група бабуни кои минуваат во една насока не треба да го спречат минувањето на бабуните од спортивна насока, т.е тие не треба да чекаат бесконечно. (Проблем на изгладнување)
3. Методи
Да претпоставиме дека кањонот има две страни, лева и десна. Во решението мора да се искористат неколку методи кои ќе ја дефинираат состојбата.

state.leftPassing() и state.rightPassing() методите соодветно, се повикуваат за да се назначи дека јажето моментално ќе го користат само бабуните кои минуваат од соодветната страна. 
Доколку бабуните од лева страна го започнат нивното минување, првиот бабун од групата е должен да го повика методот state.leftPassing(). 
Напомена: Овој метод го повикува само првиот бабун од соодветната страна. Методот се повикува секогаш кога ќе настане промена во насоките на движење.

state.cross(this) методот се повикува кај секој од бабуните (вклучувајќи го и првиот). Овој метод означува дека бабунот го започнал неговото изминување на јажето.
state.leave(this) методот се повкува кај секој од бабуните откако бабунот ќе премине на другата страна на каноњот.

________________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class BaboonCrossingSolution {

    //Kontorla na jazheto od koja strana pominuvaat
    static Semaphore mutexRope;

    //Muteksi za promenlivite left i right
    static Semaphore mutexLeft;
    static Semaphore mutexRight;

    //Kontrola na majmuni za vlez vo chekalnata
    static Semaphore turnStyle;

    //Kontrola na brojot na majmuni koi se kacheni ja jazhe
    static Semaphore onRope;

    static int left;
    static int right;

    public static void init() {
        mutexRope = new Semaphore(1);
        mutexLeft = new Semaphore(1);
        mutexRight = new Semaphore(1);
        turnStyle = new Semaphore(1);
        onRope = new Semaphore(5);
        left = right = 0;
    }

    public static class BaboonLeft extends TemplateThread {

        public BaboonLeft(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            turnStyle.acquire();
            state.enter(this);

            mutexLeft.acquire();
            left++;
            if(left == 1){
                mutexRope.acquire();
                state.leftPassing();
            }
            mutexLeft.release();
            turnStyle.release();

            onRope.acquire();
            state.cross(this);
            onRope.release();

            mutexLeft.acquire();
            left--;
            state.leave(this);
            if(left == 0){
                mutexRope.release();
            }
            mutexLeft.release();
        }

    }

    public static class BaboonRight extends TemplateThread {

        public BaboonRight(int numRuns) {
            super(numRuns);
        }

        // Istiot koe e prepishan od BaboonLeft klasata so promena na left vo
        // right i obratno
        @Override
        public void execute() throws InterruptedException {
            turnStyle.acquire();
            state.enter(this);

            mutexRight.acquire();
            right++;
            if(right == 1){
                mutexRope.acquire();
                state.rightPassing();
            }
            mutexRight.release();
            turnStyle.release();

            onRope.acquire();
            state.cross(this);
            onRope.release();

            mutexRight.acquire();
            right--;
            state.leave(this);
            if(right == 0){
                mutexRope.release();
            }
            mutexRight.release();
        }
    }

    static BaboonCrossingState state = new BaboonCrossingState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numScenarios = 500;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numScenarios; i++) {
                BaboonLeft l = new BaboonLeft(numRuns);
                BaboonRight r = new BaboonRight(numRuns);
                threads.add(l);
                threads.add(r);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}