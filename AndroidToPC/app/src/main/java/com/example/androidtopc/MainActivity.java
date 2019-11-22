package com.example.androidtopc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
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



    private String testPath = "/0_ComputerNetworkProject/test.jpg";
    private String myCameraPath = "/DCIM/Camera";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //start client thread
        TcpClientThread tcpClientThread = new TcpClientThread();
        tcpClientThread.start();

        MyFileObserver myFileObserver =
                new MyFileObserver(Environment.getExternalStorageDirectory().getPath()
                        + testPath);

        myFileObserver.startWatching();

        applyFileOperationPermission();


//        UdpSendThread udpSendThread = new UdpSendThread(this.getApplicationContext());
//        udpSendThread.start();

    }


    //dynamically apply permission
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    void applyFileOperationPermission()
    {
        try {
            if (ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
