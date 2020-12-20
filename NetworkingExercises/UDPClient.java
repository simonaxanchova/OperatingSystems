package zadachiNetworking;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class UDPClient extends Thread {

    public static int port = 8010;

    @Override
    public void run() {
        try {
            DatagramSocket socket = new DatagramSocket(port++);
            DatagramPacket packet = new DatagramPacket(new byte[256], 256);

            // Ova kje go prakjame do serverot
            String message = "This is a message from the client w/ port: " + socket.getLocalPort();

            socket.send(new DatagramPacket(
                    message.getBytes(),
                    message.getBytes().length,
                    InetAddress.getLocalHost(), // Lokacijata na serverot
                    8009)); // Portata na serverot

            // Chekame da ni vrati
            socket.receive(packet);

            System.out.println("CLIENT: <<< " + new String(Arrays.copyOfRange(packet.getData(), 0, packet.getLength())));

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}