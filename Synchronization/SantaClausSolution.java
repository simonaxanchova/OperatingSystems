THE SANTA CLAUS PROBLEM
Дедо Мраз одмара во својата фабрика на Северниот Пол. Него може да го повикаат ирвасите кога ќе се вратат од Јужниот Пацифик за да им ја припреми санката, 
или елфовите кои имаат потешкотии при правењето на играчки (им недостигаат алатки за работа). 
За ирвасите да го повикаат Дедо Мраз мора сите 9 да се вратени од нивниот одмор во тропските предели. 
За елфовите да го повикаат да им донесе алатки мора 3 од нив да се присутни во работилницата. 
9 - тиот ирвас и 3 - тиот елф го повикуваат Дедо Мраз за да им пружи помош. Ако истовремено се присутни 3 елфови и 9 - те ирваси, 
тогаш Дедо Мраз ќе им даде приоритет на ирвасите. Кога 3 елфови се наоѓаат во работилницата, секој нареден што ќе дојде мора да чека претходните 3 да завршат.

Има една инстанца од нишката Дедо Мраз (Santa), поголем број на инстанци од нишката Елф (Elf) и точно девет инстанци од нишката Ирвас (Reindeer). 
Santa ќе се изврши онолку пати колку што е потребно за да ги задоволи и елфовите и ирвасите. 
Сите инстанци од Elf ќе се извршат по еднаш и 9 - те инстанци од Reindeer ќе се извршат по еднаш.

Треба да се повикаат методите во секоја од класите како што е наведено:

• state.reindeerArrived() - го повикува секој од ирвасите кога ќе пристигне на Северниот Пол. Го симболизира враќањето на ирвасот од одмор.

• state.getHitched() - го повикува секој од ирвасите откако Дедо Мраз ќе ја припреми санката. 
Го симболизира поставувањето на ирвасот на своето место за да може да ја влече санката. Ако претходно не се сите ирваси пристигнати, овој метод ќе фрли исклучок. 
Ако Дедо Мраз не ја припремил претходно санката, овој метод ќе фрли исклучок.

• state.prepSleigh() - го повикува Дедо Мраз. Го симболизира подготвувањето на санката. 
Ако Дедо Мраз бил повикан да ја припреми санката пред да пристигнат сите 9 ирваси, овој метод ќе фрли исклучок. 
Откако ќе ја припреми санката Дедо Мраз ги остава ирвасите да се сместат.

• state.elfEntered() - го повикува секој од елфовите. Го симболизира влегувањето на елфот во работилницата. 
Ако некој елф влезе додека во работилницата има уште елфови кои работат или чекаат Дедо Мраз да им ги донесе алатките, овој метод ќе фрли исклучок.

• state.getHelp() - го повикува секој од елфовите. Го симболизира земањето на алатките од Дедо Мраз и завршувањето со својата работа на правење играчки. 
Ако при првиот повик на овој метод немало 3 елфови кои чекаат помош, овој метод ќе фрли исклучок. Ако Дедо Мраз не ги има донесено алатките, овој метод ќе фрли исклучок.

• state.helpElves() - го повикува Дедо Мраз. Го симболизира носењето на алатки на елфовите. 
Ако 9 - те ирваси чекаат кога овој метод ќе биде повикан, се фрла исклучок поради повисокиот приоритет на ирвасите. Ако не се присутни 3 елфови во 
работилницата (ако Дедо Мраз бил повикан да ги донесе алатките пред време), овој метод ќе фрли исклучок.

Се користат семафори и бројачи по желба. Тие треба да се иницијализираат во методот init(). 
Треба да се имплементираат методите execute() во секоја од трите класи користејќи ги претходно споменатите методи.

__________________________________________________________________________________________________________________________________________________________________________________

package zadachi;

import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class SantaClausSolution {

    // Brojac koj ke gi broi elfovite koi vleguvaat vo rabotilnicata
    static int elfCount;
    // Brojac koj ke gi broi irvasite koi pristignale na severniot pol
    static int reindeerCount;

    // Semafor koj regulira koga Dedo Mraz treba da asistira na elfovite ili
    // irvasite
    static Semaphore santaSem;
    // Semafor na koj treba da zastane sekoj irvas pred Dedo Mraz da ja pripremi
    // sankata
    static Semaphore reindeerSem;
    // Semafor na koj treba da zastane sekoj elf pred Dedo Mraz da gi donese
    // alatkite za izrabotka na igracki
    static Semaphore elfSem;

    // Muteks za brojacite
    static Semaphore mutex;
    // Muteks koj kje kazuva dali e slobodna rabotilnicata za vleguvanje
    static Semaphore elfMutex;

    public static void init() {
        elfCount = 0;
        reindeerCount = 0;

        santaSem = new Semaphore(0);
        reindeerSem = new Semaphore(0);
        elfSem = new Semaphore(0);

        mutex = new Semaphore(1);
        elfMutex = new Semaphore(1);
    }

    public static class Reindeer extends TemplateThread {

        public Reindeer(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //za da sprecime race condition
            mutex.acquire();
            //irvas pristiganl -> go zgolemuvame brojacot
            state.reindeerArrived();
            reindeerCount++;
            //ako pristignal i posledniot irvars - dozvoli mu na Dedo Mraz da ja pripremi sankata
            if(reindeerCount == 9){
                santaSem.release();
            }
            mutex.release();

            //cekanje dodeka Dedo Mraz ne ja podgotvi sankata
            reindeerSem.acquire();
            //spregni se na sankata
            state.getHitched();
        }
    }

    public static class Elf extends TemplateThread {

        public Elf(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            //elfMutex nema da dade pristap ako prethodno vlegle tri elfovi da
            //rabotat i ne se zavrsni se uste
            elfMutex.acquire();
            //za da nema race condition
            mutex.acquire();
            //pristignal elf -- go zgolemuvame brojacot
            state.elfEntered();
            elfCount++;
            //ako pristignal i tret elf, togas vikni go Dedo Mraz da gi donese alatkite
            //ako ne, togas oslobodi ja vratata od rabotilnicata za da moze uste nekoj elf da vleze
            if(elfCount == 3){
                santaSem.release();
            }else{
                elfMutex.release();
            }
            mutex.release();

            //cekaj dodeka ne bidat alatkite doneseni
            elfSem.acquire();
            //elfot raboti, pa izleguva
            state.getHelp();

            //go stitime brojacot
            mutex.acquire();
            //namaluvanje na brojot na elfovi
            elfCount--;
            //ako zavrsile site elfoci, togas oslobodi ja vratata za da vlezat tie so cekaat
            if(elfCount == 0){
                elfMutex.release();
            }
            mutex.release();
        }
    }

    public static class Santa extends TemplateThread {

        public Santa(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            // Cekaj dodeka elf ili irvas ne te povika
            santaSem.acquire();
            // Stiti gi brojacite
            mutex.acquire();
            // Prvo se proveruva dali irvasite se sobrale, bidejki tie imaat
            // povisok prioritet
            // Ako se sobrale, togas podgotvi ja sankata
            // Ako ne - togas asistiraj na elfovite, odnosno donesi gi alatkite
            if (reindeerCount == 9) {
                reindeerCount = 0;
                state.prepSleigh();
                // Otkako sankata kje se podgotvi, ovozmozi im na irvasite da se
                // spregnat
                reindeerSem.release(9);
            } else if (elfCount == 3) {
                state.helpElves();
                // Otkako kje bidat doneseni alatkite, ovozmozi im na elfovite
                // da rabotat
                elfSem.release(3);
            }
            mutex.release();
        }
    }

    static SantaClausState state = new SantaClausState();

    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            System.out.println("Run: " + i);
            run();
        }
    }

    public static void run() {
        try {
            int numElves = 180;
            HashSet<Thread> threads = new HashSet<>();
            Santa santa = new Santa((numElves / 3) + 1);
            threads.add(santa);

            int numRuns = 1;
            for (int i = 0; i < numElves; i++) {
                Elf elf = new Elf(numRuns);
                threads.add(elf);
                if (i % 20 == 0) {
                    Reindeer reindeer = new Reindeer(numRuns);
                    threads.add(reindeer);
                }
            }

            init();

            ProblemExecution.start(threads, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}