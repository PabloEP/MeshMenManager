package com.ManagerMain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ManagerMain extends AppCompatActivity {

    private static final String debugString = "debug";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_main);
        Socket clienteSocket = null;
        ServerSocket serverSocket = null;


        try {
            serverSocket = new ServerSocket(8080);
            Log.i(debugString, "Server Started..");
            clienteSocket = serverSocket.accept();
            Log.i(debugString, "ClienteSocket");
            Scanner in1 = null;
            Log.i(debugString, "Scanner");
            in1 = new Scanner(clienteSocket.getInputStream());
            String mes;
            while(true){
                if (in1.hasNext()){
                    Log.i(debugString, "While...");
                    mes = in1.nextLine();
                    Log.i(debugString, "Cliente message: " + mes);
                }
            }


        } catch (IOException e) {
            Log.e(debugString, "Error creando el socket");
        }


    }
}
