import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ManejadorClientes extends Thread{

    DatagramSocket ds;
    DatagramPacket envio;
    DatagramPacket receptor;
    byte[] bufferEnvio;
    byte[] bufferReceptor;


}
