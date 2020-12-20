package zadachiNetworking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class UDPServer extends Thread {

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(8009);
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);

            while (true) {
                // Chekaj nekoja poraka da stigne
                // Stavi ja vo packet
                socket.receive(packet);

                // Isprintaj shto dobi
                System.out.println("SERVER: <<< " + new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength())));

                // Vrati mu ja na korisnikot negovata IP adresa
                String message = "Hello there! Your address is " + packet.getAddress() + ":" + packet.getPort() + "\n";
                socket.send(new DatagramPacket(message.getBytes(), message.getBytes().length, packet.getAddress(), packet.getPort()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}