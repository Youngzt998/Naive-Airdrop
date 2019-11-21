package com.example.androidtopc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TcpClientThread tcpClientThread = new TcpClientThread();
        tcpClientThread.start();




//        UdpSendThread udpSendThread = new UdpSendThread(this.getApplicationContext());
//        udpSendThread.start();


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
//        socketBuildThread = new SocketBuildThread();
//        socketBuildThread.start();
//        //Toast.makeText(this, "服务开启", Toast.LENGTH_SHORT).show();
//        socketReceiveThread = new SocketReceiveThread();
//        socketReceiveThread.start();
//        Toast.makeText(this, "服务开启", Toast.LENGTH_SHORT).show();

    }





}
