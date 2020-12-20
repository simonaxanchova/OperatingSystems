Gym
Потребно е да направите систем за синхронизација на играчи во една сала според следното сценарио:

Во салата истовремено може да влезат најмногу 12 играчи, по што треба да се пресоблечат за што имаат на располагање кабина со капацитет 4. 
По пресоблекувањето, играчите се чекаат меѓусебно. Откако сите ќе завршат со пресоблекувањето, започнуваат со спортување. 
Играчите излегуваат од салата откако ќе завршат со спортувањето, а последниот го повикува методот state.slobodnaSala() за да каже дека салата е слободна. 
Потоа, во салата може да влезат нови 12 играчи и сценариото започнува одново.

Потребно е да ги синхронизирате играчите со користење на следните методи:

state.presobleci() - Кажува дека играчот се пресоблекува во кабината
Се повикува кај сите играчи
Доколку методот истовремено го повикаат повеќе од четири играчи, ќе добиете порака за грешка
Доколку методот се извршува секвентно (се пресоблекува само еден во кабината во еден момент), ќе добиете порака за грешка

state.sportuvaj() - Кажува дека играчот спортува
Се повикува кај сите играчи
Доколку методот се повика, а не се пресоблечени сите 12 играчи, ќе добиете порака за грешка
Доколку методот истовремено го повикаат повеќе од 12 играчи, ќе добиете порака за грешка
Доколку методот се извршува секвентно (само еден играч спортува во еден момент), ќе добиете порака за грешка

state.slobodnaSala() - Кажува дека салата е слободна
Се повикува само од еден играч (оној кој последен завршил со спортувањето)
Доколку методот се повика, а со спортувањето не се завршени сите 12 играчи, ќе добиете порака за грешка
Доколку методот истовремено се повика од повеќе од 1 играч, ќе добиете порака за грешка

_________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class GymSolution {

    public static Semaphore sala;
    public static Semaphore kabina;
    public static Semaphore start;
    static int i = 0;

    public static void init() {
        sala = new Semaphore(12);
        kabina = new Semaphore(4);
        start = new Semaphore(0);
    }



    public static class Player extends TemplateThread {

        public Player(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            sala.acquire();
            kabina.acquire();
            state.presobleci();
            kabina.release();
            synchronized (Player.class){
                i++;
                if (i == 12){
                    start.release(12);
                }
            }
            start.acquire();
            state.sportuvaj();
            synchronized (Player.class){
                i--;
                if (i == 0){
                    state.slobodnaSala();
                    sala.release(12);
                }
            }
        }
    }

    static GymState state = new GymState();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            run();
        }
    }

    public static void run() {
        try {
            Scanner s = new Scanner(System.in);
            int numRuns = 1;
            int numIterations = 1200;
            s.close();

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Player h = new Player(numRuns);
                threads.add(h);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
