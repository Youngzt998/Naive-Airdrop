package com.example.androidtopc;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

//receive UDP message, locate target ip address
class UdpReceiveThread extends Thread
{
    private String TAG = "UDP Receive Thread";
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    String multicastHost = "224.0.0.1";
    InetAddress inetAddress;        //reveive address
    BroadCastMessageHandler broadCastMessageHandler;

    UdpReceiveThread(BroadCastMessageHandler broadCastMessageHandler)
    {
        try {
            multicastSocket = new MulticastSocket(8003);
            multicastSocket.setBroadcast(true);
            inetAddress = InetAddress.getByName(multicastHost);
            multicastSocket.joinGroup(inetAddress);
//
//                datagramSocket = new  DatagramSocket(null);
//                datagramSocket.setBroadcast(true);
//                datagramSocket.bind(InetSocketAddress(8003));
            Log.d(TAG, "finish initializing thread ");
            this.broadCastMessageHandler = broadCastMessageHandler;
        }catch (Exception e){
            Log.d(TAG, "init error ");
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        byte buffer[] = new byte[32];
        DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
        while (!interrupted())
        {
            try {
                multicastSocket.receive(datagramPacket);
                String receiveString = new String(buffer, 0, datagramPacket.getLength());
                //Log.d(TAG, "read buffer: "+ receiveString);

                String[] receiveData = splitData(receiveString);
                if(receiveData==null)
                {
                    Log.d(TAG, "wrong receive data ");
                    continue;
                    //
                }
                //Log.d(TAG, "\n"+"\nName: " + receiveData[0] + "\nIP: " + receiveData[1] + "\nPort:" + receiveData[2]);
                broadCastMessageHandler.sendString(receiveData);
            }catch (Exception e){
                Log.d(TAG, "read buffer error ");
                e.printStackTrace();
            }
        }
    }

    private String[] splitData(String receiveData)
    {
        try {
            String[] result = receiveData.split(" ");
            return result;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}


// receive message: from thread to main activity
class BroadCastMessageHandler extends Handler
{
    private String TAG = "messageHandler";
    private static final int BROAD_CAST_MESSAGE = 0;
    String[] receiveData = null;
    Looper looper;
    BroadCastMessageHandler()
    {

    }

    @Override
    public void handleMessage(Message msg)
    {
        Log.d(TAG, "handleMessage");
        switch (msg.what)
        {
            case BROAD_CAST_MESSAGE:
                try {
                    receiveData = (String[]) msg.obj;
                    Log.d(TAG, "\n"+"\nName: " + receiveData[0] + "\nIP: " + receiveData[1] + "\nPort:" + receiveData[2]);
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public void sendString(String[] receiveData)
    {
        Message msg = new Message();
        msg.what = BROAD_CAST_MESSAGE;
        msg.obj = receiveData;
        this.sendMessage(msg);
    }

    public String[] getMessage()
    {
        String[] result = receiveData;
        //receiveData = null;
        return receiveData;
    }

}




//send UDP message; for test;  no use in this project
class UdpSendThread extends Thread
{
    private String TAG = "UDP Send Thread";
    private String multicastHost = "224.0.0.1";     //broadcast
    private String broadCastIP = "255.255.255.255";
    private String hotplotBreadCastIp = "192.168.43.255";
    private int targetPort = 8004;
    MulticastSocket multicastSocket;
    InetAddress inetAddress;
    Context context;

    //parameter:  Context of mainActivity
    UdpSendThread(Context context)
    {
        this.context = context;
        try{
            multicastSocket = new MulticastSocket();
            Log.d(TAG, "init finished ");
        }catch (Exception e){
            Log.d(TAG, "init error ");
            e.printStackTrace();
        }

    }

    @Override
    public void run()
    {
        DatagramPacket datagramPacket = null;
        while (!interrupted())
        {
            try {

                if(isWifiApEnabled(this.context))
                {
                    inetAddress = InetAddress.getByName(hotplotBreadCastIp);
                }
                else {
                    inetAddress = InetAddress.getByName(broadCastIP);
                }


                Log.d(TAG, "sending UDP message ");
                //System.out.println(inetAddress.isMulticastAddress());

                multicastSocket.setTimeToLive(2);
                byte[] sendData = "One Plus 7p".getBytes();
                datagramPacket = new DatagramPacket(sendData, sendData.length, inetAddress, targetPort);
                multicastSocket.send(datagramPacket);
                sleep(2000);

            }catch (Exception e){
                Log.d(TAG, "send error ");
                e.printStackTrace();
            }
        }
    }

    //is our phone using Hotspot?
    public  Boolean isWifiApEnabled(Context context) {
        try {
            WifiManager manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            Method method = manager.getClass().getMethod("isWifiApEnabled");
            return (Boolean)method.invoke(manager);
        }
        catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)  {
            e.printStackTrace();
        }
        return false;
    }
}
