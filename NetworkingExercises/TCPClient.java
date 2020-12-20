package zadachiNetworking;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread {
    private static int id = 0;

    public void run(){
        Socket socket;
        int clientId = TCPClient.id++;

        try {
            socket = new Socket(InetAddress.getByName("localhost"), 8000);

            //od ovie ke gi citame komandiite na serverot
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            //preku ovoj objekt ke go pratime baranjeto na serverot
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            //go prakjame naseto baranje do serverot
            bw.write((clientId % 2 == 0 ? "GET" : "POST") + " /Movies/" + clientId + " HTTP/1.1\n");
            bw.write("USER: Simona Anchova\n");
            bw.write("\n"); //so eden prazen red ke mu kazeme na serverot deka zavrsivme so baranjeto

            //mora da povikame flush bidejki koristime BufferedReader
            //i site write-ovi dasega bea baferirani
            bw.flush();

            String line = "/";

            //ovde ke blokirame se dodeka serverot nema nisto za citanje
            //stom serverot ni prati cela linija, ke ja isprintame ovde
            while((line = br.readLine()) != null)
                System.out.println("Client[" + clientId + "] <<< " + line);

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

