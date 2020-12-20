package zadachiNetworking;

import zadachiNetworking.TCPClient;
import zadachiNetworking.TCPServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


//ovaa programa simluria poednostavna HTTP client-server komunikacija
//imame eden Server kojsto kreva Worker-i za da gi citaat baranjata i da prakjaat odgovori
//generirame 10 klienti koisto prakjaat sample baranje
//na kraj, kreirame edno nashe baranje preku tastatura
//pomegju sekoj klient i worker imame full-duplex stream komunikacija
//vo sekoj moment klientot ili worker-ot moze da pratat sto sakaat
public class TCPMain {

    public static void main(String[] args) throws InterruptedException, IOException {
        TCPServer server = new TCPServer();
        server.start();

        //ke spieme 1 sekunda za da se osigurame
        //deka serverot ima vreme da se inicijalizira
        Thread.sleep(1000);

        //kreirame 10 korisnici koi ke pratat po edno baranje
        for(int i = 0; i < 10; i++){
            TCPClient client = new TCPClient();
            client.start();
        }

        //ke pocekame da zavrsat site klienti
        Thread.sleep(1000);

        System.out.println("\nGET/POST a new movie from/to the server");
        System.out.println("=========================================\n");

        //ke kreirame i edno nashe baranje
        //da vidime dali serverot ke ni vrati
        Scanner key = new Scanner(System.in);
        String verb, resource, user;

        System.out.println("Enter action: ");
        verb = key.nextLine();

        System.out.println("Enter movie: ");
        resource = key.nextLine();

        System.out.println("What is your name?");
        user = key.nextLine();

        // Kreirame socket za komunikacija so serverot
        Socket socket = new Socket(InetAddress.getLocalHost(), 8000);

        //gi zemame dvata stream-a
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        //go prakajme naseto baranje do serverot
        bw.write(verb + " " + resource + "\n");
        bw.write("USER: " + user + "\n");
        bw.write("\n");
        bw.flush();

        //System.err. kasneshe so printanje pa ispagjase deka odgovorot se dobiva pred baranjeto
        //ova go resava toa
        Thread.sleep(1000);

        String line = null;

        System.out.println("\n=================================\n");

        System.out.println("We are done!");
    }
}
