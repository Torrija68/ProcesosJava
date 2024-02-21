import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer {
    private MulticastSocket msk;
    private InetAddress address;
    List<ManejadorClientes> listaClientes;

    public MainServer(int puerto){
        listaClientes = new CopyOnWriteArrayList<>();
        try {
            msk = new MulticastSocket(puerto);
            address = InetAddress.getByName("255.0.0.1");

            while (true){

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new MainServer(5000);
    }
}