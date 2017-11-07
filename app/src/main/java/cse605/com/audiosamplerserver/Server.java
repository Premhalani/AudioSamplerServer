package cse605.com.audiosamplerserver;

/**
 * Created by premh on 26-Oct-17.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
/**
 * Created by premh on 16-Oct-17.
 */

public class Server {
    MainActivity mainActivity;
    ServerSocket serverSocket;
    String message;
    static final int socketServerPORT = 8080;

    public Server(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }
    public int getPort(){
        return socketServerPORT;
    }

    public void onDestroy(){
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private class SocketServerThread extends Thread{
        int count = 0;

        @Override
        public void run() {
            DataInputStream dataInputStream = null;
            try{
                serverSocket = new ServerSocket(socketServerPORT);

                while(true){

                    BufferedReader br = new BufferedReader(new InputStreamReader(serverSocket.accept().getInputStream()));
                    String headerLine = null;
                    while((headerLine = br.readLine()).length() != 0){
                        Log.d("Header",headerLine);
                    }
                    final StringBuilder payload = new StringBuilder();
                    while(br.ready()){
                        payload.append((char) br.read());
                    }
                    Log.d("Payload",payload.toString());
                    count++;
                    message += "\n #" + count + " Timestamp = "+payload;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mainActivity.textViewMsg.setText(message);

                        }
                    });

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public String getIpAddress() {
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
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }
}
