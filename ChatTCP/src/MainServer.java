import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer {
    private ServerSocket server;
    private List<ManejadorClientes> clientes;

    public MainServer(int puerto){
        clientes = new CopyOnWriteArrayList<>();
        try {
            server = new ServerSocket(puerto);
            System.out.println("Empezó el servidor");
            while (true){
                Socket conexion = server.accept();
                ManejadorClientes cliente = new ManejadorClientes(this, conexion);
                clientes.add(cliente);
                System.out.println("Cliente Aceptado");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(server != null){
                try {
                    server.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void enviarMensajeAtodos(String mensaje){
        if (mensaje != null){
            for (ManejadorClientes cliente : clientes){
                cliente.enviarMensaje(mensaje);
            }
        }
    }

    public void eliminarCliente(ManejadorClientes cliente){
        clientes.remove(cliente);
    }

    public static void main(String[] args) {
        new MainServer(5000);
    }
}

