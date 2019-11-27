package com.example.androidtopc;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;


public class MainService extends Service {

    private String testPath = "/0_ComputerNetworkProject/test.jpeg";
    private String testDir = "/0_ComputerNetworkProject";
    private String myCameraPath = "/DCIM/Camera";

    public MainService()
    {

    }

    @Override
    public void onCreate()
    {
        //start client thread
//        TcpClientThread tcpClientThread = new TcpClientThread();
//        tcpClientThread.start();
//
//        MyFileObserver myFileObserver =
//                new MyFileObserver(Environment.getExternalStorageDirectory().getPath()
//                        + testDir);
//
//        myFileObserver.startWatching();

    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
