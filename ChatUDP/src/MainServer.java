import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer {
    int puerto = 8888;
    String direccion = "230.0.0.0";
    private MulticastSocket msk;
    private InetAddress grupo;
    DatagramPacket envio;
    DatagramPacket receptor;
    byte[] bufferEnvio = new byte[256];
    byte[] bufferReceptor = new byte[256];
    List<Integer> clientes;

    public MainServer(){
        try {
            msk = new MulticastSocket();
            grupo = InetAddress.getByName(direccion);
            System.out.println("Esperando cliente");

            Thread HiloReceptor = new Thread(() -> {
                    try {
                        while (true) {
                            receptor = new DatagramPacket(bufferReceptor, bufferReceptor.length);
                            msk.receive(receptor);

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                });
            HiloReceptor.start();


            } catch (UnknownHostException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }


    }

    public static void main(String[] args) {
        new MainServer();
    }
}


