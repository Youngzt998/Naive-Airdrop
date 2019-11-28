package com.example.androidtopc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
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


    private String observePath;
    private String testPath = "/0_ComputerNetworkProject/test.jpeg";
    private String testDir = "0_ComputerNetworkProject";
    private String myCameraPath = "/DCIM/Camera";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Intent mainSercive = new Intent(this, MainService.class);
//        startService(mainSercive);

        //start client thread
        observePath = Environment.getExternalStorageDirectory().getPath()+ "/" + myCameraPath;
        TcpClientThread tcpClientThread = new TcpClientThread(observePath);
        tcpClientThread.start();


        //FileObserveThread fileObserveThread = new FileObserveThread(Environment.getExternalStorageDirectory().getPath()+testDir);

        //myFileObserver.startWatching();

        applyFileOperationPermission();

//        UdpSendThread udpSendThread = new UdpSendThread(this.getApplicationContext());
//        udpSendThread.start();

    }


    @Override
    public void finish()
    {
        moveTaskToBack(true);
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

}
