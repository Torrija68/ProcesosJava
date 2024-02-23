import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class MainServer {
    private static DatagramSocket ds;
    private byte[] bufferEnvio = new byte[256];
    private byte[] bufferReceptor = new byte[256];
    private static Map<String, InetAddress> clientes = new HashMap<>();
    private static Set<String> nombresRegistrados = new HashSet<>();

    public static void main(String[] args) {
        iniciarServidor(5555);
    }

    private static void iniciarServidor(int puerto) {
        try {
            ds = new DatagramSocket(puerto);
            System.out.println("Servidor Iniciado en " + puerto);
            while (true) {
                byte[] bufferReceptor = new byte[256];
                DatagramPacket receptor = new DatagramPacket(bufferReceptor, bufferReceptor.length);
                ds.receive(receptor);
                InetAddress direccionCliente = receptor.getAddress();
                int puertoCliente = receptor.getPort();
                String idCliente = direccionCliente.getHostAddress() + ":" + puertoCliente;
                clientes.put(idCliente, direccionCliente);

                String mensajeRecibido = new String(receptor.getData(), 0, receptor.getLength());

                if(!mensajeRecibido.isEmpty()) {
                    String[] partesMensaje = mensajeRecibido.split(":", 2);
                    if (partesMensaje.length == 2 && partesMensaje[1].equals("REGISTRO")) {
                        String nombreUsuario = partesMensaje[0];
                        if (!nombresRegistrados.contains(nombreUsuario)) {
                            nombresRegistrados.add(nombreUsuario);
                            enviarMensaje("REGISTRO_EXITOSO", direccionCliente, puertoCliente);
                        } else {
                            enviarMensaje("NOMBRE_EN_USO", direccionCliente, puertoCliente);
                        }
                    } else if (partesMensaje.length == 2) {
                        String nombreUsuario = partesMensaje[0];
                        String contenidoMensaje = partesMensaje[1];
                        enviarMensajeATodos(nombreUsuario + ": " + contenidoMensaje, direccionCliente, puertoCliente);
                    }
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (ds != null) {
                ds.close();
            }
        }
    }

    private static void manejarDesconexion(String idCliente) {
        clientes.remove(idCliente);
    }

    private static void enviarMensajeATodos(String mensaje, InetAddress direccionOrigen, int puertoOrigen) {
        for (Map.Entry<String, InetAddress> entry : clientes.entrySet()) {
            String idCliente = entry.getKey();
            InetAddress direccionCliente = entry.getValue();
            int puertoCliente = Integer.parseInt(idCliente.substring(idCliente.lastIndexOf(":") + 1));
            if (!idCliente.equals(direccionOrigen.getHostAddress() + ":" + puertoOrigen)) {
                enviarMensaje(mensaje, direccionCliente, puertoCliente);
            }
        }
    }

    private static void enviarMensaje(String mensaje, InetAddress direccionCliente, int puertoCliente) {
        try {
            byte[] bufferEnvio = mensaje.getBytes();
            DatagramPacket envio = new DatagramPacket(bufferEnvio, bufferEnvio.length, direccionCliente,
                    puertoCliente);
            ds.send(envio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}




