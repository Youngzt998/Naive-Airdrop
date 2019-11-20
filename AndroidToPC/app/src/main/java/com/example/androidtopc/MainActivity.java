package com.example.androidtopc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.MulticastSocket;

public class MainActivity extends AppCompatActivity {

    ServerSocket serverSocket;
    Socket socket;
    private int serverPort;
    private OutputStream outputStream;
    private InputStream inputStream;
    SocketBuildThread socketBuildThread;
    SocketReceiveThread socketReceiveThread;
    SocketSendThread socketSendThread;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startServer();

        UdpReceiveThread udpReceiveThread = new UdpReceiveThread();
        udpReceiveThread.start();


        UdpSendThread udpSendThread = new UdpSendThread(this.getApplicationContext());
        udpSendThread.start();
    }

    public void startServer()
    {
        try {
            serverSocket = new ServerSocket(8002);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "绑定端口失败...", Toast.LENGTH_SHORT).show();
            return;
        }
        socketBuildThread = new SocketBuildThread();
        socketBuildThread.start();
        //Toast.makeText(this, "服务开启", Toast.LENGTH_SHORT).show();
        socketReceiveThread = new SocketReceiveThread();
        socketReceiveThread.start();
        Toast.makeText(this, "服务开启", Toast.LENGTH_SHORT).show();

    }

    class SocketBuildThread extends Thread
    {
        @Override
        public void run()
        {
            //while (!interrupted())
            {
                try {
                    socket = serverSocket.accept();
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    class SocketReceiveThread extends Thread
    {
        @Override
        public void run()
        {
            byte[] buffer = new byte[64];
            while (!interrupted())
            {
                try {
                    if(buffer==null){continue;}
                    int count = inputStream.read(buffer);
                    if(count==-1){continue;}

                    String receiveData = new String(buffer, 0, count);
                    Log.d("Receive Thread", "read buffer: "+ receiveData + ", count: "+ count);

                }catch (Exception e){
                    //e.printStackTrace();
                }
            }

        }
    }

    class SocketSendThread extends Thread
    {

    }


}
