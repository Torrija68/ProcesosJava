import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

public class Cliente extends JFrame implements ActionListener {
    int puerto = 8888;
    String direccion = "225.0.0.1";
    String nombre;
    MulticastSocket msk;
    InetAddress grupo;
    DatagramPacket envio;
    DatagramPacket receptor;
    DatagramSocket ds;
    byte[] bufferEnvio = new byte[256];
    byte[] bufferReceptor = new byte[256];
    private JTextArea txtChat;
    private JTextField txtMensaje;
    private Button btnEnviar;
    private JScrollPane scroll;

    public Cliente() {

        nombre = JOptionPane.showInputDialog("Ingresa tu nombre");
        if(nombre != null){
            initComponents();
            recibirMensajes();
        }

    }
    private void initComponents(){
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

        btnEnviar.addActionListener( this);

        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400,500);
        setLocationRelativeTo(null);

    }

    public void recibirMensajes(){
            try {
                ds = new DatagramSocket();
                grupo = InetAddress.getByName(direccion);

                msk = new MulticastSocket(puerto);
                msk.joinGroup(grupo);

                while (true){
                    receptor = new DatagramPacket(bufferReceptor,bufferReceptor.length,grupo,puerto);
                    msk.receive(receptor);
                    String mensaje = new String(receptor.getData(),0, receptor.getLength());
                    txtChat.append(mensaje + "\n");
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String mensaje = txtMensaje.getText();
        if(!mensaje.isEmpty()){
            enviarMensaje(nombre +": "+ mensaje);
            txtMensaje.setText("");
        }
    }
    private void enviarMensaje(String mensaje){
        try {
            bufferEnvio = mensaje.getBytes();
            envio = new DatagramPacket(bufferEnvio,bufferEnvio.length,grupo,puerto);
            msk.send(envio);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        new Cliente();
    }
}
