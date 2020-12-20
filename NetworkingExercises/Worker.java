package zadachiNetworking;

import sun.misc.Request;

import javax.swing.text.html.Option;
import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Worker extends Thread {

    private static int id = 0;

    private Socket client;
    private int workerId;

    public Worker(Socket socket) { //konstruktor
        this.client = socket;
        this.workerId = Worker.id++;
    }

    public void run(){
        try {

            //od ovde ke gi citame komandite na korisnikot
            BufferedReader br = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

            //koga sakame nesto da pratime do korisnikot,
            //ke go korisitme ovoj objekt
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream()));

            //znaeme deka sekogas prvata linija e: VERB URI
            Request request = new Request(br.readLine().split("\\s+"));
            System.err.println("Worker[" + this.workerId + "] <<< " + request.verb + " " + request.uri);

            //ostanatite linii se od tipot: NAME: VALUE
            String line = null;

            //citame se dodeka ne zavrsi baranjeto
            //zavrsuva so eden prazen red
            while(!(line = br.readLine()).equals("")){
                System.err.println("Worker[" + this.workerId + "] <<< " + line);
                String[] parts = line.split(":\\s+", 2);
                request.headers.put(parts[0], parts[1]); //parts[0] = NAME, parts[1] = VALUE
            }

            if(request.verb.equals("POST") && request.headers.get("Content-Length") != null){
                StringBuilder sb = new StringBuilder();
                //ovoj header ke ni kazuva kolku bajti ima vo teloto
                int length = Integer.parseInt(request.headers.get("Content-Length").trim());
                while(length --> 0)
                    sb.append((char)br.read());

                request.body = sb.toString();
                System.out.println("BODY: " + request.body);
            }


            //vo ovoj moment, go procituvame celoto baranje od korisnikot
            //i istoto go imame vo 'request' promenlivata

            String clientName = Optional.ofNullable(request.headers.get("USER"))
                    .orElse(request.headers.get("User-Agent"));
                    //ova e standarden Header vo HTTP za tipot na klient sto pobaruva
                    //voobicaeno toa e vasiot prebaruvac (Firefox, Chrome...)

            //vrakjame na korisnikot
            bw.write("HTTP/1.1 200 OK\n\n"); //za Chrome da ne se buni
            bw.write("Hello, " + clientName + "!\n");
            bw.write("You requested to " + request.verb + " the resource: " + request.uri + "\n");
            if(request.verb.equals("POST") && request.body != null)
                bw.write("You sent me: " + request.body + "\n");
            bw.write("\n"); //so eden prazen red ke mu kazeme na korisnikot deka zavrsivme so odgovorot
            bw.flush(); //mora da napravime flush za da go ispratime baferiraniot tekst

            this.client.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class Request{
        public String verb; //definira akcija: dali zemam ili prikacuvame nesto na server
        public String uri; //go identifikuva resursot sto sakame da go zememe/prikacime
        public String version; //verzija na protokolot
        public Map<String, String> headers; //niza na header-i, kako dopolnitelni informacii
        public String body; //teloto na baranjeto, dokolku saka nesto da prati korisnikot

        public Request(String[] line){
            this.verb = line[0];
            System.out.println(Arrays.asList(line));
            this.uri = String.join(" ", Arrays.copyOfRange(line, 1, line.length - 1));
            this.version = line[line.length - 1];
            this.headers = new HashMap<>();
        }

        //baranjeto e vo sledniot format
        //VERB URI
        //HeaderName1: HeaderValue1
        //HeaderName2: HeaderValue2
        //...
    }
}
