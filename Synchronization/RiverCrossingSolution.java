River Crossing Problem

Некаде близу Редмонд, Вашингтон, има брод кој го користат Linux хакерите (hackers) и вработените во Microsoft (serfs), за премин на реката за да стигнат на работното место. 
Бродот се дивижи од едната на другата страна на реката ако и само ако на него се качат точно четворица патници. 
Поради големото ривалство на вработените од двете компании, не е дозволено на бродот да има еден вработен од едната и тројца вработени од другата компанија. 
Сите други комбинации се дозволени. Од таа причина, ве изнајмуваат вас да направете софтвер кој ќе го регулира преминот на реката.

Во почетниот код кој е даден, дефинирани се класите Hacker и SerfОд секоја од класите паралелно се активни повеќе инстанци кои на бродот може да се качат само еднаш.

Во имплементацијата мора да ги искористите следните методи од веќе дефинираната променлива state:
- state.board()
секој вработен кој се качува на бродот ја повикува оваа функција,
треба да гарантирате дека секој од четирите патници кои се качуваат на бродот го има повикано овој метод пред да биде повикан методот rowboat().

- state.rowBoat()
методот треба да биде повикан откако претходно сите четири патници го повикале методот board()
треба да гарантирате дека методот ќе биде повикан само од еден од четворицата патници, независно од кој.

Вашата задача е да ги имплементирате методите
Hacker.cross()
Serf.cross() 
init()
____________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;


public class RiverCrossingSolution {

    //promenlivi koi ni kazuvaat kolku lugje od dvete grupni momentalno stojat
    //pokraj brodot i cekaat da se kacat
    static int hackers, serfs;

    //vo momentot koga moze da se formira kombinacija koja e dozvolena se
    //odlucuva na kolku lugje od sekoja od grupite im se dozvoluva da se kazat na brodot
    static Semaphore hackersWaiting;
    static Semaphore serfsWaiting;

    //za da ne nastane race condition pri proverka za toa kolku lugje ima vo brodot
    static Semaphore checkTheBoat;

    //otkako sme povikale pravilna kombinacija od lugje treba sekoj prvo da povika
    //board() pa posle toa posledniot sto e dojden i so kogo e kompletirana
    //kombinacijata da go povika row_boat()
    static Semaphore allowToRow;

    static Semaphore allowed;

    public RiverCrossingSolution() {

    }

    public static void init() {
        // иницијализација на семафорите и променливите
        allowToRow = new Semaphore(0);
        hackersWaiting = new Semaphore(0);
        serfsWaiting = new Semaphore(0);
        checkTheBoat = new Semaphore(1);
        allowed = new Semaphore(0);
        hackers = 0;
        serfs = 0;
    }

    static class Hacker extends TemplateThread {

        public Hacker(int numRuns) {
            super(numRuns);
        }

        //objasnuvanje na reshenieto:
        //do brodot doagaat lugje (nitki) od dvata tipa i idejakata e koga ke
        //dojde posledniot covek so koj se formira edna od validnite kombinacii
        //da dozvoloi lugjeto koi ja formiraat taa validna kombinacija da se kacat na brodot

        public void cross() throws InterruptedException {
            boolean isCaptain = false;

            //dozvoluvame samo na eden covek vo eden
            //moment da zastane pokraj brodot
            checkTheBoat.acquire();

            //go zgolemuvame brojot na hakeri koi momentalno stojat pokraj brodot
            hackers++;

            if(hackers == 4){
                //dokolku brojot na hakeri stignal 4 znaci nie ja imame kombinacijata
                //4h i 0s i vo ovoj slucaj brojt na hakeri koi stojat pokraj brodot
                //stanuva 0, a brojot na serfovi si ostanuva ist
                //dozvoluvame tie 4 hakeri da se kacat na brodot pritoa posledniot od niv
                //koj e dojden so koj brojot na hakeri se iskacil na 4 go stavame za
                //kapiten t.e. onoj koj ke ja povika row_boat()
                hackers = 0;
                hackersWaiting.release(4);
                isCaptain = true;
            }else if(hackers == 2 && serfs >=2){
                //ova e momentot koga pred brodot ima pristignato povekje od 2
                //ili 2 sefovi i tocno 2 hakeri pritoa mozeme da ja formirame
                //kombinacijata 2h i 2s so sto dozvoluvame da se kacat na
                //brodot 2 hakeri i 2 serfovi
                hackers = 0;
                serfs -= 2;
                hackersWaiting.release(2);
                serfsWaiting.release(2);
                isCaptain = true;
            }else{
                //ova e slucaj koga pred brodot nema potreben broj na hakeri i
                //serfovi so koi bi se formirala nekoja kombinacija, vo toj slucaj
                //samo go osloboduvame vlezot pred brodot za da moze da vleze drug covek
                checkTheBoat.release();
            }


            //ova e semaforot koj ovozmozuva hakerite koi ke dojdat pred brodot
            //da cekaat za validna kombinacija so sto bi se oslobodil semaforot
            //i tie moze da se kacat (toa osloboduvanje go pravi posledniot patnik
            //so koj bi se sklopila validnata kombinacija)
            hackersWaiting.acquire();

            //povik na funckijata board()
            state.board();

            //ova e momentot koga site se kaceni na brodot i dokolku covekot ne e
            //kapiten togas toj mu dava eden permit na kapitenot za da ja povika
            //row_boat(), a kapitenot treba da ceka tri takvi permiti so sto bi se
            //osigural deka trojcata patnici nekapiteni se kaceni na brodot i ja
            //imaat povikano board()
            if(!isCaptain){
                allowToRow.release();
            }

            //dokolku e kapiten togas gi ceka potrebnite 3 permiti t.e ceka site
            //trojca drugi patnici da povikaat board() za toj da povika row_boat()
            //sto e eden od glavnite uslovi na zadacata, posle toa toj samo go
            //osloboduva pristapot do brodot, za da moze drug covek da dojde do brodot
            if(isCaptain){
                allowToRow.acquire(3);
                state.rowBoat();
                checkTheBoat.release();
            }
        }

        @Override
        public void execute() throws InterruptedException {
            cross();
        }

    }

    static class Serf extends TemplateThread {

        public Serf(int numRuns) {
            super(numRuns);
        }

        // објаснувањето за серф класата е исто како и за хакер
        public void cross() throws InterruptedException {
            boolean isCaptain = false;

            checkTheBoat.acquire();
            serfs++;
            if(serfs == 4){
                serfs = 0;
                serfsWaiting.release(4);
                isCaptain = true;
            }else if(serfs == 2 && hackers >= 2){
                serfs = 0;
                hackers -= 2;
                isCaptain = true;
                serfsWaiting.release(2);
                hackersWaiting.release(2);
            }else{
                checkTheBoat.release();
            }

            serfsWaiting.acquire();
            state.board();
            if(!isCaptain){
                allowToRow.release();
            }
            if(isCaptain){
                allowToRow.acquire(3);
                state.rowBoat();
                checkTheBoat.release();
            }
        }

        @Override
        public void execute() throws InterruptedException {
            cross();
        }

    }

    static RiverCrossingState state = new RiverCrossingState();

    public static void main(String[] args) {
        for (int i = 0; i < 15; i++)
            run();
    }

    public static void run() {
        try {
            int numRuns = 1;
            int numIterations = 120;

            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numIterations; i++) {
                Hacker h = new Hacker(numRuns);
                Serf s = new Serf(numRuns);
                threads.add(h);
                threads.add(s);
            }

            init();

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
