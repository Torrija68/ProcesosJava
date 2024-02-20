import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ManejadorClientes extends Thread {
    private MainServer mainServer;
    private Socket sk;
    private String nombre;
    private DataOutputStream dos;
    private DataInputStream dis;
    private static List<String> listaNombres = new ArrayList<>();


    public ManejadorClientes(MainServer mainServer, Socket sk) {
        this.mainServer = mainServer;
        this.sk = sk;

        new Thread(this).start();
    }

    public void run() {
        try {
            dis = new DataInputStream(sk.getInputStream());
            dos = new DataOutputStream(sk.getOutputStream());

            nombre = dis.readUTF();
            boolean existe = validarUsuario(nombre);
            dos.writeBoolean(existe);

            if (!existe){
                listaNombres.add(nombre);
                mainServer.enviarMensajeAtodos(nombre + " conectado");
            }

            String mensaje = dis.readUTF();
            while (mensaje != null) {
                mainServer.enviarMensajeAtodos(nombre + ": " + mensaje);
                mensaje = dis.readUTF();
            }
        } catch (IOException e) {
            try {
                sk.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } finally {
            mainServer.eliminarCliente(this);
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    try {
                        sk.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
            mainServer.enviarMensajeAtodos(nombre + " desconectado");
        }
    }



    public void enviarMensaje(String mensaje) {
        try {
            dos = new DataOutputStream(sk.getOutputStream());
            dos.writeUTF(mensaje);
        } catch (IOException e) {
            try {
                sk.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    public boolean validarUsuario(String nombre){
        return listaNombres.contains(nombre);
    }
}

