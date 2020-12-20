The Party Room Problem

The Party Room Problem започнува од деканот кој има потреба да ја пребара собата на студентите. Има неколку главни
ограничувања на кои мора да се задржиме :
- Бројот на студенти во собата не е ограничен
- Деканот може да влезе во собата ако бројот на студенти во неа е 0 (празна) или ако има повќе од 50 студенти(ја растура нивната забава)
- Кога деканот е во собата студентите може само да излегуваат но не и да влегуваат
- Деканот не смее да ја напушти собата додека сите студенти не ја напуштат
- Има само еден декан (не мора да се синхронизира слочајот за влез на повеќе декани)


Променливата state содржи повеќе функции и тоа се : 

 - void studentEnter()
	Сигнализира влез на еден студент. Ако студентот проба да влезе додека 
	деканот се наоѓа во собата исфрла грешка
	
 - void dance()
    Сигнализира почеток на забавата

 - void deanEnter()
    Сигнализира влез на деканот. Тоа е возможно во два случаеви и тоа да нема 
	студенти во собата или да има повеќе од 50 студенти во собата(има забава)
	
 - void studentLeave()
    Сигнализира заминување на студент од собата

 - void deanLeave()
    Сигнализира заминување на деканот. Кој не смее да ја напушти собата додека сите студенти не излезат
	
 - void breakUpParty()
    Деканот ја прекинува забавата. Може да ја расипе забавата ако има повеќе од 50 студенти.
	
 - void conductSearch()
    Деканот извршува пребарување на собата. Тоа е возможно ако нема присутни студенти.
    
_________________________________________________________________________________________________________________________________________________________________________________
package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;


public class PartySolution {

        static final int DEAN_NOT_HERE = 0;
        static final int DEAN_WAITING = 1;
        static final int DEAN_INSIDE = 2;

        static int students = 0;
        static int dean = DEAN_NOT_HERE;

        static Semaphore mutex; //sinhronizacija na if razgranuvanjeto
        static Semaphore turn;  //koj e na red da proba da vleze
        static Semaphore clear; //signalizira na dekanot koga sobata e prazna
        static Semaphore lieIn; //signalizira na dekanot da vleze ako cekal pred sobata


        public static void init() {
            mutex = new Semaphore(1);
            turn = new Semaphore(1);
            clear = new Semaphore(0);
            lieIn = new Semaphore(0);
        }

        static class Student extends TemplateThread {

            public Student(int numRuns) {
                super(numRuns);
            }

            // studentot pristignuva i moze
            @Override
            public void execute() throws InterruptedException {
                mutex.acquire();
                //dokolku dekanot e vo sobata, studentot nema pravo da vleze
                if(dean == DEAN_INSIDE){
                    mutex.release();
                    turn.acquire();
                    turn.release();
                    mutex.acquire();
                }
                students++;
                state.studentEnter();

                //51 student mu signalizira na dekanot deka moze da vleze
                if(students == 51 && dean == DEAN_WAITING){
                    lieIn.release();
                }else{
                    mutex.release();
                }
                state.dance();
                mutex.acquire();
                students--;
                state.studentLeave();

                //ako dekanot ceka pred sobata posledniot student mu signalizira deka moze da vleze
                if(students == 0 && dean == DEAN_WAITING){
                    lieIn.release();
                }
                //ako dekanot e vnatre, posledniot mu signalizira deka sobata e prazna
                else if(students == 0 && dean == DEAN_INSIDE){
                    clear.release();
                }else{
                    mutex.release();
                }
            }
        }

        static class Dean extends TemplateThread {

            public Dean(int numRuns) {
                super(numRuns);
            }

            // dekanot pristignuva i ima 3 mozni slucaevi
            @Override
            public void execute() throws InterruptedException {
                mutex.acquire();
                //dokolku brojot na studenti e pomegju 0 i 51 togas dekanot mora da ceka pred sobata
                if(students > 0 && students < 51){
                    dean = DEAN_WAITING;
                    mutex.release();
                    //dekanot ceka pogoden moment da vleze vo sobata
                    lieIn.acquire();
                }

                //dokolku ima nad 50 studenti koga ke dojde dekanot
                //vo ovoj slucaj dekanot ceka na studentite
                if(students > 50){
                    dean = DEAN_INSIDE;
                    state.deanEnter();
                    state.breakUpParty();

                    //ceka na studentite da izlezat
                    turn.acquire();
                    mutex.release();
                    //posledniot signalizira deka sobata e prazna
                    clear.acquire();
                    turn.release();
                }
                //dekanot nema potreba da ceka na studentite
                else{
                    //ako brojot na studenti e 0 dekanot vleguva i go izvrzuva prebaruvanjeto
                    state.deanEnter();
                    state.conductSearch();
                }
                state.deanLeave();
                dean = DEAN_NOT_HERE;
                mutex.release();
            }

        }

        static PartyState state = new PartyState();

        public static void main(String[] args) {
            for (int i = 0; i < 10; i++) {
                run();
            }
        }

        public static void run() {
            try {
                int numRuns = 1;
                int numScenarios = 100;

                HashSet<Thread> threads = new HashSet<Thread>();

                for (int i = 0; i < numScenarios; i++) {
                    Student s = new Student(numRuns);
                    threads.add(s);

                }
                threads.add(new Dean(numRuns));

                init();

                ProblemExecution.start(threads, state);
                System.out.println(new Date().getTime());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
}
    
    
