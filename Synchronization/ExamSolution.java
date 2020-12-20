Exam
Од владата на РМ ве ангажираат за синхронизација на процесот на државен испит, каде професорите ќе го изведуваат испитот во една просторија. 
Испитот се одржува во повеќе термини, каде во секој термин мора да има присутно еден професор и 50 студенти. По завршувањето на терминот, 
од просторијата прво излегуваат студентите и професорот, а потоа влегува нов професор и нови 50 студенти.

Притоа важат следните услови:

Во просторијата може да има само еден професор и точно 50 студенти.
Студентите не смеат да влезат ако во просторијата нема професор
Студентите не смеат да излезат додека професорот не каже дека испитот завршил
Професорот не може да излезе додека има студенти во просторијата
Просторијата иницијално е празна
Вашата задача е да го синхронизирате претходното сценарио.

Во почетниот код кој е даден, дефинирани се класите Teacher и Student, кои го симболизираат однесувањето на професорите и студентите, соодветно. 
Има повеќе инстанци од двете класи кај кои методот execute() се повикува само еднаш.

Во имплементацијата, можете да ги користите следните методи од веќе дефинираната променлива state:

state.teacherEnter()
Означува дека професорот влегува во училницата.
Се повикува од сите професори.
Доколку училницата не е празна во моментот на повикувањето, ќе се јави исклучок.

state.studentEnter()
Означува дека студентот влегува во училницата.
Се повикува од сите студенти.
Доколку нема професор во училницата (претходно не е повикан state.teacherEnter()), или има повеќе од 50 студенти внатре, ќе се јави исклучок.
Доколку студентите не влегуваат паралелно (повеќе истовремено), ќе јави исклучок.

state.distributeTests()
Го симболизира делењето на тестови на студентите и започнувањето на испитот.
Се повикува од сите професори по влегувањето на сите 50 студенти.
Доколку нема 50 присутни студенти во училницата, ќе се јави исклучок.

state.examEnd()
Го симболизира истекувањето на времето за испитот.
Се повикува од сите професори.
Доколку претходно не е повикан state.distributeTests(), ќе јави исклучок.

state.studentLeave()
Го симболизира излегувањето на студентот од училницата.
Се повикува од сите студенти.
Доколку се повика пред state.examEnd(), или ако претходно излегол професорот, ќе се јави исклучок.

state.teacherLeave()
Го симболизира излегувањето на професорот од училницата.
Се повикува од сите професори.
Доколку методот се повика, а сеуште има студенти во училницата, ќе добиете порака за грешка.
_________________________________________________________________________________________________________________________________________________________________________________


package zadachi;

import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.Semaphore;

import zadachi.ProblemExecution;
import zadachi.TemplateThread;

public class ExamSolution {

    public static Semaphore teacher;
    public static Semaphore studentEnter;
    public static Semaphore studentLeave;
    public static Semaphore studentHere;
    public static Semaphore canStudentLeave;

    public static void init() {
        teacher = new Semaphore(1);
        studentEnter = new Semaphore(0);
        studentLeave = new Semaphore(0);
        studentHere = new Semaphore(0);
        canStudentLeave = new Semaphore(0);
    }

    public static class Teacher extends TemplateThread {

        public Teacher(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            teacher.acquire();
            state.teacherEnter();

            studentEnter.release(50);
            studentHere.acquire(50);
            state.distributeTests();
            state.examEnd();
            canStudentLeave.release(50);
            studentLeave.acquire(50);
            state.teacherLeave();
            teacher.release();
        }
    }

    public static class Student extends TemplateThread {

        public Student(int numRuns) {
            super(numRuns);
        }

        @Override
        public void execute() throws InterruptedException {
            studentEnter.acquire();
            state.studentEnter();
            studentHere.release();

            canStudentLeave.acquire();
            state.studentLeave();

            studentLeave.release();
        }
    }

    static ExamState state = new ExamState();

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
                    Teacher c = new Teacher(numRuns);
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
