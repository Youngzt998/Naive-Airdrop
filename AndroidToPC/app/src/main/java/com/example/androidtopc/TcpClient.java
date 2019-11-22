package com.example.androidtopc;

import android.os.Looper;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


// the thread for connecting to the server
class TcpClientThread extends Thread
{
    private String TAG = "TcpClientThread";

    private String serverName;
    private String serverIP;
    private int serverPort;
    BroadCastMessageHandler broadCastMessageHandler;
    UdpReceiveThread udpReceiveThread;

    Socket clientSocket;
    InputStream inputStream;
    OutputStream outputStream;

    TcpClientThread()
    {
        try {
            broadCastMessageHandler = new BroadCastMessageHandler();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        Looper.prepare();
        while (!interrupted())
        {

            udpReceiveThread = new UdpReceiveThread(broadCastMessageHandler);
            udpReceiveThread.start();

            String[] serverInfo = null;
            Log.d(TAG, "waiting for a broadcast");
            while (serverInfo==null)  //wait for available broad cast
            {
                serverInfo = broadCastMessageHandler.getMessage();
            }
            Log.d(TAG, "receive a broadcast, try building connection");

            serverName = serverInfo[0];
            serverIP = serverInfo[1];
            serverPort = Integer.parseInt(serverInfo[2]);

            try {
                clientSocket = new Socket(serverIP, serverPort);
                outputStream=clientSocket.getOutputStream();
                inputStream=clientSocket.getInputStream();
            }catch (Exception e){
                Log.d(TAG, "connect failed!");
                e.printStackTrace();
                continue;
            }
            Log.d(TAG, "connect successed, close broad cast thread!");
            udpReceiveThread.interrupt();

            try {
                while (true)
                {
                    outputStream.write("One plus 7p".getBytes());
                    sleep(2000);
                }
            }catch (Exception e){
                Log.d(TAG, "write failed!");
                e.printStackTrace();
                continue;
            }


        }

        Looper.loop();

    }

    //subthread
    class receiveThread extends Thread
    {
        @Override
        public void run()
        {

        }
    }

    class sendThread extends Thread
    {
        @Override
        public void run()
        {

        }
    }
}







