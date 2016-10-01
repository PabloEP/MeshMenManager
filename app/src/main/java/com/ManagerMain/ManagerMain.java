package com.ManagerMain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

/**
 * Clase principal del manager, se encarga del server y la GUI con sus cambios
 * también podrá comunicarse con el API.
 * */
public class ManagerMain extends Activity {

    TextView puertoManager, ipManager, dispositivos;
    String mensaje = "";
    ServerSocket serverSocket;
    private static final String debugString = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        puertoManager = (TextView) findViewById(R.id.info);
        ipManager = (TextView) findViewById(R.id.infoip);
        dispositivos = (TextView) findViewById(R.id.msg);


        ipManager.setText(obtenerIPManager());

        Thread socketServerThread = new Thread(new iniciarServerSocket());
        socketServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * Se encarga de inciar el servidor
     * @see java.lang.Thread que es el hilo del servidor y espera rspuestas
     * @see java.lang.Runnable ejecuta la aplicacion, al igual que genera cambios de GUI
     * */
    private class iniciarServerSocket extends Thread {

        static final int SocketServerPORT = 5656;

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(SocketServerPORT);
                ManagerMain.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        puertoManager.setText("Esperando Dispositivos en el puerto: "
                                + serverSocket.getLocalPort());
                    }
                });

                while (true) {

                    Socket socket = serverSocket.accept();
                    //recibe los datos del node entrante
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    mensaje += br.readLine() + "\n";
                    //imprime estos datos en pantalla
                    ManagerMain.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            dispositivos.setText(mensaje);
                        }
                    });
                    //envia los datos que del nuevo nodo
                    //en esta linea es donde se puede llamar al api y ademas guardar los datos
                    //en la lista
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    bw.write(mensaje);
                    bw.newLine();
                    bw.flush();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /**
     * Método encargado de obtener la ip del server.
     * @return un string con el numero de la ip
     *
     * */
    private String obtenerIPManager() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Direccion IP Local: "
                                + inetAddress.getHostAddress() + "\n";
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Problemas de IP " + e.toString() + "\n";
        }

        return ip;
    }
}
