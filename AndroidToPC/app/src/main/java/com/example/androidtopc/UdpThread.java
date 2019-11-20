package com.example.androidtopc;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

//receive UDP message
class UdpReceiveThread extends Thread
{
    private String TAG = "UDP Receive Thread";
    private MulticastSocket multicastSocket;
    private DatagramSocket datagramSocket;
    String multicastHost = "224.0.0.1";
    InetAddress inetAddress;        //reveive address

    UdpReceiveThread()
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
                Log.d("UDP Receive Thread", "receiving data");
                multicastSocket.receive(datagramPacket);
                Log.d("UDP Receive Thread", "read buffer: "+ new String(buffer, 0, datagramPacket.getLength()));
            }catch (Exception e){
                Log.d("UDP Receive Thread", "read buffer error ");
                e.printStackTrace();
            }
        }

    }
}

//send UDP message
class UdpSendThread extends Thread
{
    private String TAG = "UDP Send Thread";
    private String multicastHost = "224.0.0.1";     //broadcast
    private String broadCastIP = "255.255.255.255";
    private int targetPort = 8004;
    MulticastSocket multicastSocket;
    InetAddress inetAddress;
    UdpSendThread()
    {
        try{
            multicastSocket = new MulticastSocket();
            inetAddress = InetAddress.getByName(broadCastIP);
            System.out.println(inetAddress.isMulticastAddress());
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
}
