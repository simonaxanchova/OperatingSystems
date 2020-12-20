Hilzer's Barbershop Problem

Во овој проблем берберницата има повеќе столчиња за потстрижување (исто колку и бербери), повеќе бербери (но не повеќе од 15) и просторија за чекање која се состои од 
една софа на која може да се сместат најмногу 4 клиенти и простор за стоење. Ограничувањата се дека во берберницата не смее во ниеден момент да има повеќе од 
20 клиенти (ова ги вклучува столчињата, софата и просторот за стоење). 
Од ова ограничување произлегува и ограничувањето за берберите и столчињата, бидејќи секој клиент мора прво да чека стоечки, па да чека седечки, 
па да оди на столчето за потстрижување, па ако ставиме повеќе од 15 бербери ќе нема место за стоење.

Клиент не смее да влезе во берберницата ако нејзиниот капацитет е исполнет (внатре веќе има 20 клиенти).
Откако ќе влезе, клиентот стои додека да се ослободи место на софата (да има помалку од 4 клиенти на софата). 
Кога некој бербер е слободен, еден од клиентите (не е важно кој) кои седат на софата се префрлува на неговото столче за потстрижување и 
му сигнализира на берберот дека тој е спремен за потстрижување.

Во овој момент ако има клиенти кои стојат, еден од нив (не е важно кој) седнува на софата.
Кога некој од клиентите е готов со потстрижувањето тој плака кај било кој од берберите (немора кај тој што го потстрижувал). 
Повеќе клиенти може да плаќаат во исто време.
Берберите своето време го делат помегу: потстрижување, наплаќање и чекање да дојде клиент на нивото столче.

Со други зборови, следниве ограничувања треба да важат при синхронизацијата:

Клиентите ги повикуваат следниве функции и тоа стрикно во овој редослед: enterShop, sitOnSofa, sitInBarberChair, getHairCut, pay, exitShop.
Берберите ги повикуваат: cutHair, acceptPayment.

- enterShop()
Секој клиент ја повикува при влез во берберницата.
Клиентите не може да ја повикаат enterShop ако капацитетот на берберницата е исполнет.

- sitOnSofa()
Секој клиент ја повикува при седнување на софата.
Највеќе 4 клиенти можат да седат на софата истовремено.
Ако капацитетот на софата е полн, клиент којшто пристигнал не може да ја повика sitOnSofa додека еден од клиентите на софата не ја повика sitInBarberChair.

- sitInBarberChair()
Секој клиент ја повикува при седнување на берберскиот стол.
Ако сите бербер(и) се зафатени, клиентот не може да ја повика sitInBarberChair додека еден од клиентите кои се потстрижуваат не ја повика exitShop.

- getHairCut()
Секој клиент ја повикува по седнувањето на берберскиот стол.
Мора да биде повикана пред берберот да ја повика cutHair.

- pay()
Секој клиент ја повикува откако ке биде потстрижан.
Клиентот мора да ја повика pay пред берберот да ја повика acceptPayment.
Клиентот не може да ја повика exitShop пред да плати.

- exitShop()
Секој клиент мора да бил потстрижан пред да може да излезе.
Берберот мора да ја повика acceptPayment пред клиентот да ја повика exitShop.

- cutHair()
Секој бербер ја повикува кога ќе има присутен клиент на некој од столовите.
Клиентот мора да ја повикал getHairCut пред да биде повикана оваа функција.

- acceptPayment()
Секој бербер ја повикува по потстрижувањето на клиент.
Клиентот мора да ја повикал pay пред да биде повикана оваа функција.

_________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;
import zadachi.*;


public class HilzerBarberShopSolution {
    static State state;

    // TODO: Definicija na globalni promenlivi i semafori

    //Za musterijata da vleze vo berbernicata i stoecki da ceka red
    static Semaphore standingRoom;

    //Za musterija da sedne na sofata
    static Semaphore sofa;

    //za musterija da sedne na berberskiot stol
    static Semaphore chair;

    //za berberot da kaze deka e sloboden da go postrize musterijata
    static Semaphore barber;

    // za musterijata da oznaci deka sedi na stolot i saka da bide postrizan
    static Semaphore customer;

    //za musterijata da oznaci deka e spremen za potstrizuvanje
    static Semaphore readyForHaircut;

    //za berberot da kaze deka e gotovo potstrizuvanjeto
    static Semaphore hairDone;

    //za musterijata da gi dade parite za postrizuvanjeto
    static Semaphore cash;

    //za berberot da ja dade fiskalnata smetka
    static Semaphore receipt;

    public static void init(int numBarbers) {
        //Bidejki vkupno mora da ima ne povekje od 20 musterii vnatre
        //a 4 mesta se rezervirani za softa
        //ostanuvaat 16 mesta, od koi numBarbers se rezervirani za stolovite
        //zatoa moze da ima maksimalno 15 barber (t.e stolovi)
        standingRoom = new Semaphore(16 - numBarbers);

        //4 mesta rezervirani za sofata
        sofa = new Semaphore(4);

        //po eden stol za sekoj berber
        chair = new Semaphore(numBarbers);

        //inicijalno nema spremni berberi
        barber = new Semaphore(0);

        //inicijalno nema pristignati musterii koi sedat na stol
        customer = new Semaphore(0);

        //inicijalno nema spremni musterii
        readyForHaircut = new Semaphore(0);

        //inicijalno nema potstrizani musterii
        hairDone = new Semaphore(0);

        //inicijalno nikoj ne dal pari
        cash = new Semaphore(0);

        //inicijano ne se izdadeni fiskalni smetki
        receipt = new Semaphore(0);

    }

    public static class Barber extends TemplateThread {
        public int barberId;

        public Barber(int numRuns, int id) {
            super(numRuns);
            barberId = id;
        }

        @Override
        public void execute() throws InterruptedException {
            //ceka musterija da sedne na stolot
            customer.acquire();
            //kazuva deka e slobden da go potstrize
            barber.release();
            //ceka musterijata da bide spremen za potstrizuvanje
            readyForHaircut.acquire();
            //go potstrizuva musterijata
            state.cutHair();
            //kazuva deka musterijata e potstrizan
            hairDone.release();

            //ceka musterijata da plati
            cash.acquire();
            //gi prifkaja parite (i pecati smetka)
            state.acceptPayment();
            //dava fiskalna smetka
            receipt.release();
        }
    }

    public static class Customer extends TemplateThread {
        public int custId;

        public Customer(int numRuns, int cId) {
            super(numRuns);
            this.custId = cId;
        }

        @Override
        public void execute() throws InterruptedException {
            //Ogranicuva max 20 musterii da bidat prisutni vnatre
            standingRoom.acquire();
            //otkako se oslobodilo mesto vnatre, vleguva
            state.enterShop();

            //ceka red za sedenje na sofata
            sofa.acquire();
            //sednuva na sofata
            state.sitOnSofa();
            //osloboduva mesto za stoenje
            standingRoom.release();

            //ceka red za sedenje na berberskiot stol
            chair.acquire();
            //sednuva na stolot
            state.sitInBarberChair();
            //osloboduva edno mesto na sofata
            sofa.release();

            //kazuva deka e pristignat i sedi na stolot
            customer.release();
            //ceka da se oslobdi berber
            barber.acquire();
            //signalizira deka saka da bide potstrizan
            state.getHairCut();
            //kazuva deka e spremen za potstrizuvanje
            readyForHaircut.release();

            //ceka da bide potstrizan
            hairDone.acquire();
            //signalizira deka e spremen da plati
            state.pay();
            //gi dava parite
            cash.release();
            //ceka fiskalna smetka
            receipt.acquire();

            //trgnuva kon izlezot
            state.exitShop();
            //kazuva deka ima sloboden stol
            chair.release();
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            System.out.print((i + 1) + ": ");
            run();
        }
    }

    public static void run() {
        try {
            int numBarbers = 5; // Max: 15
            int numCustomers = 10 * numBarbers;

            state = new State(numBarbers, numCustomers);
            HashSet<Thread> threads = new HashSet<Thread>();

            for (int i = 0; i < numBarbers; i++) {
                Barber b = new Barber(Math.max(numCustomers / numBarbers, 1), i);
                threads.add(b);
            }
            for (int i = 0; i < numCustomers; i++) {
                Customer cust = new Customer(1, i);
                threads.add(cust);
            }

            init(numBarbers);

            ProblemExecution.start(threads, state);
            System.out.println(new Date().getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
