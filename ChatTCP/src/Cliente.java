import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Cliente extends JFrame implements ActionListener{
    private String nombre;
    private int puerto;
    private boolean existe;
    private Socket conexion;
    private DataInputStream dis;
    private DataOutputStream dos;
    private JTextArea txtChat;
    private JTextField txtMensaje;
    private JScrollPane scroll;
    private Button btnEnviar;
    public Cliente(int puerto){
        super("Chat");
        this.puerto = puerto;

        nombre = JOptionPane.showInputDialog("Ingresa tu nombre");
        if(nombre != null){
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
    @Override
    public void actionPerformed(ActionEvent e) {
        String mensaje = txtMensaje.getText();
        if(!mensaje.isEmpty()){
            try {
                dos = new DataOutputStream(conexion.getOutputStream());
                dos.writeUTF(mensaje);
                txtMensaje.setText("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }


    private void recibirMensajes() {
        boolean nombreValido = false;
        while (!nombreValido) {
            try {
                conexion = new Socket("localhost", puerto);
                dis = new DataInputStream(conexion.getInputStream());
                dos = new DataOutputStream(conexion.getOutputStream());

                dos.writeUTF(nombre);

                boolean usuarioExiste = dis.readBoolean();

                if (usuarioExiste) {
                    int opcion = JOptionPane.showConfirmDialog(this, "El nombre de usuario ya está en uso. ¿Desea intentarlo de nuevo?", "Nombre de usuario duplicado", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (opcion == JOptionPane.YES_OPTION) {
                        nombre = JOptionPane.showInputDialog(this, "Ingresa tu nombre");
                    } else {
                        conexion.close();
                        return;
                    }
                } else {
                    nombreValido = true;
                    initComponents();

                    while (true) {
                        String mensaje = dis.readUTF();
                        txtChat.append(mensaje + "\n");
                        scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                dispose();
            } finally {
                if (conexion != null) {
                    try {
                        conexion.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        new Cliente(5000);
    }
}
