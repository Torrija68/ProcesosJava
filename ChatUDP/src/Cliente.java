import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

public class Cliente extends JFrame implements ActionListener {
    public int puerto;
    public InetAddress servidor;
    private DatagramSocket ds;
    private String nombre;
    private Set<String> nombresRegistrados;
    private Set<String> mensajesRecibidos = new HashSet<>();
    private byte[] bufferReceptor = new byte[256];
    private byte[] bufferEnvio = new byte[256];
    private JTextArea txtChat;
    private JTextField txtMensaje;
    private Button btnEnviar;
    private JScrollPane scroll;

    public Cliente() {
        nombresRegistrados = new HashSet<>();
        nombre = obtenerNombreUsuario();
        initComponents();
        try {
            ds = new DatagramSocket();
            servidor = InetAddress.getByName("localhost");
            puerto = 5555;
            registrarUsuario(nombre);
            Thread HiloMensajes = new Thread(this::recibirMensajes);
            HiloMensajes.start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                cerrarSocket();
            }
        });

    }

    private void cerrarSocket() {
        if(ds != null && !ds.isClosed()){
            ds.close();
            txtChat.append(nombre + " desconectado");
        }
    }

    private void initComponents() {

        txtChat = new JTextArea();
        txtChat.setEditable(false);
        txtChat.setBorder(BorderFactory.createTitledBorder("Chat"));

        scroll = new JScrollPane(txtChat);

        txtMensaje = new JTextField();
        txtMensaje.setBorder(BorderFactory.createTitledBorder("Mensaje"));

        btnEnviar = new Button("Enviar");

        JPanel panelEnviarMensaje = new JPanel(new BorderLayout());
        panelEnviarMensaje.add(txtMensaje, BorderLayout.CENTER);
        panelEnviarMensaje.add(btnEnviar, BorderLayout.EAST);

        add(scroll, BorderLayout.CENTER);
        add(panelEnviarMensaje, BorderLayout.SOUTH);

        btnEnviar.addActionListener(this);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
    }
    private void registrarUsuario(String nombreUsuario) {
        String mensaje = nombreUsuario + ":REGISTRO";
        enviarMensaje(mensaje);
    }

    private String obtenerNombreUsuario() {
        String nombreUsuario = null;
        while (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            nombreUsuario = JOptionPane.showInputDialog("Ingrese su nombre de usuario");
            if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario no puede estar vacío");
            }
        }
        return nombreUsuario;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnEnviar) {
            String mensaje = nombre + ":" + txtMensaje.getText();
            enviarMensaje(mensaje);
            txtMensaje.setText("");
        }
    }

    public void recibirMensajes() {
        while (true) {
            DatagramPacket receptor = new DatagramPacket(bufferReceptor, bufferReceptor.length);
            try {
                ds.receive(receptor);
                String mensaje = new String(receptor.getData(), 0, receptor.getLength());
                if (mensaje.equals("REGISTRO_EXITOSO")) {
                    JOptionPane.showMessageDialog(this, "Registro exitoso!");
                } else if (mensaje.equals("NOMBRE_EN_USO")) {
                    JOptionPane.showMessageDialog(this, "El nombre de usuario ya está en uso. Por favor, elija otro.");
                    nombre = obtenerNombreUsuario();
                    registrarUsuario(nombre);
                } else {

                    agregarMensaje(mensaje);
                }
            } catch (IOException e) {
                if(ds != null){
                    ds.close();
                }

            }
        }
    }
    private void enviarMensaje(String mensaje) {
        try {
            bufferEnvio = mensaje.getBytes();
            DatagramPacket envio = new DatagramPacket(bufferEnvio, bufferEnvio.length, servidor, puerto);
            ds.send(envio);
            agregarMensaje(mensaje);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void agregarMensaje(String mensaje) {
        String[] partesMensaje = mensaje.split(":", 2);
        if (!(partesMensaje.length == 2 && partesMensaje[1].equals("REGISTRO"))) {
            String messageContent = partesMensaje[1].trim(); // Extract message content
            if (!mensajesRecibidos.contains(messageContent)) { // Check if message is unique
                SwingUtilities.invokeLater(() -> {
                    if (!mensaje.trim().isEmpty()) {
                        txtChat.append(mensaje + "\n");
                        mensajesRecibidos.add(messageContent); // Add message to set
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        new Cliente();
    }

}
