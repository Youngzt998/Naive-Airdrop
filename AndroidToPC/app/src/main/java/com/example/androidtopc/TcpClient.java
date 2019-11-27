package com.example.androidtopc;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.ViewDebug;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


// the thread for connecting to the server
class TcpClientThread extends Thread
{
    private String TAG = "TcpClientThread";


    private String observePath;
    private String testFileName1 = "test.mp4";
    private String testFilePath1 = "/0_ComputerNetworkProject/"+testFileName1;
    private String testFileName2 = "test.jpg";
    private String testFilePath2 = "/0_ComputerNetworkProject/"+testFileName2;

    private String myCameraPath = "/DCIM/Camera";

    private String UPLOAD_FILE_REQUEST = "UPLOADFILE";

    private String ASK_FILE_NAME_ANSWER = "ASK_FILE_NAME";
    private String RECEIVE_FILE_ANSWER = "RECEIVE_FILE";
    private String FILE_EXISTS_ANSWER = "FILE_EXISTS";

    private String CANCEL_UPLOAD_REQUEST = "CANCELUPLOAD";

    private String serverName;
    private String serverIP;
    private int serverPort;

    private int DEFAULT_PACKET_SIZE = 1024;

    BroadCastMessageHandler broadCastMessageHandler;
    UdpReceiveThread udpReceiveThread;

    FileObserveThread fileObserveThread;


    Socket clientSocket;
    InputStream inputStream;
    OutputStream outputStream;

    TcpClientThread(String observePath)
    {
        this.observePath = observePath;
        try {
            // for receiving broad cast message from subthread
            broadCastMessageHandler = new BroadCastMessageHandler();
            fileObserveThread = new FileObserveThread(observePath);
            fileObserveThread.start();
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
            //start a subthread to receive broadcast from PC
            udpReceiveThread = new UdpReceiveThread(broadCastMessageHandler);
            udpReceiveThread.start();

            String[] serverInfo = null;
            Log.d(TAG, "waiting for a broadcast");
            while (serverInfo==null)  //wait for an available broadcast
            {
                serverInfo = broadCastMessageHandler.getMessage();
            }
            Log.d(TAG, "receive a broadcast, try building connection");

            try {
                //store server's information
                serverName = serverInfo[0];
                serverIP = serverInfo[1];
                serverPort = Integer.parseInt(serverInfo[2]);

                //try building a connection
                clientSocket = new Socket(serverIP, serverPort);
                outputStream=clientSocket.getOutputStream();
                inputStream=clientSocket.getInputStream();
            }catch (Exception e){
                Log.d(TAG, "connect failed!");
                e.printStackTrace();
                continue;
            }
            Log.d(TAG, "connect successed, close broadcast receiving thread!");
            udpReceiveThread.interrupt();

            while (true)
            {
                //the list of current file
                ArrayList<String> fileInfo = fileObserveThread.getInfo();
                Log.d(TAG, "Start uploading file, number is: " + fileInfo.size());
                for (int i =0; i< fileInfo.size(); ++i)
                {
                    Log.d(TAG, i + " " + observePath + fileInfo.get(i));
                    uploadFile(fileInfo.get(i), observePath + fileInfo.get(i));
                }
                try {
                    sleep(10000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            //Map<String, Integer> map = new HashMap();
        }
        Looper.loop();

    }

    private byte[] intToByte(int x)
    {
        return new byte[] {
                (byte) (x & 0xFF),
                (byte) ((x >> 8) & 0xFF),
                (byte) ((x >> 16) & 0xFF),
                (byte) ((x >> 24) & 0xFF)
        };
    }

    private int UPLOAD_SUCESS = 0;
    private int UPLOAD_FAILED = 1;
    private int FILE_EXISTS = 2;
    //filename: relative path
    int uploadFile(String filename, String absolutePath)
    {

        //start file transfer
        byte[] receiveBuffer = new byte[32];
        byte[] header = new byte[4];
        String prefix = "filename ";

        String receiveData;
        int receiveLen;
        try {
                try {
                    // ask to upload a file
                    outputStream.write(UPLOAD_FILE_REQUEST.getBytes());
                    //receive "asking name answer"
                    receiveLen = inputStream.read(receiveBuffer);
                    receiveData = new String(receiveBuffer, 0, receiveLen);
                    //System.out.println("yzt1: "+ receiveData);
                    if (receiveData.compareTo(ASK_FILE_NAME_ANSWER)!=0)
                    {
                        if(receiveData.compareTo(FILE_EXISTS_ANSWER)==0)
                        {
                            Log.d(TAG, "file "+ filename + " already exists in remote server!");
                            return FILE_EXISTS;
                        }

                        outputStream.write(CANCEL_UPLOAD_REQUEST.getBytes());
                        return UPLOAD_FAILED;
                    }
                    //tell file name
                    outputStream.write((prefix+filename).getBytes());

                    //receive upload file answer
                    receiveLen = inputStream.read(receiveBuffer);
                    receiveData = new String(receiveBuffer, 0, receiveLen);
                    //System.out.println("yzt2: "+receiveData.toString());
                    if(receiveData.compareTo(RECEIVE_FILE_ANSWER)!=0)
                    {
                        outputStream.write(CANCEL_UPLOAD_REQUEST.getBytes());
                        return UPLOAD_FAILED;
                    }


                }catch (Exception e){
                    Log.d(TAG, "pre trans error");
                    e.printStackTrace();
                    return UPLOAD_FAILED;

                }

                try {
                    //open target file
                    File file = new File(absolutePath);
                    FileInputStream fileOutStream = new FileInputStream(file);
                    int count = fileOutStream.available();
                    System.out.println("yzt3: test file size is "+ count/1024 + "KB");

                    //send the file size
                    header = intToByte(count);
                    outputStream.write(header);

                    int packetSize = DEFAULT_PACKET_SIZE;
                    int packetCount = count/packetSize;
                    int lastSize = count - packetSize * packetCount;

                    //send the file
                    byte[] sendFileBuffer = new byte[packetSize];
                    for (int i = 0; i<packetCount; ++i)
                    {
                        fileOutStream.read(sendFileBuffer, 0, packetSize);
                        outputStream.write(sendFileBuffer);
                    }
                    if (lastSize!=0)
                    {
                        sendFileBuffer = new byte[lastSize];
                        fileOutStream.read(sendFileBuffer, 0, lastSize);
                        outputStream.write(sendFileBuffer);
                    }

                    //System.out.println("yzt4: client finished sending file");
                    Log.d(TAG, "Finish upload" + filename);
                    //outputStream.flush();
                    fileOutStream.close();

                }catch (Exception e){
                    Log.d(TAG, "Open trans error");
                    e.printStackTrace();
                    return UPLOAD_FAILED;
                }

                // outputStream.write("One plus 7p".getBytes());
                //interrupted();

        }catch (Exception e){
            Log.d(TAG, "write failed!");
            e.printStackTrace();

        }
        return UPLOAD_SUCESS;
    }

    //subthread
    class ReceiveThread extends Thread
    {
        ReceiveThread(String filename, String path)
        {

        }

        @Override
        public void run()
        {

        }
    }

    class SendThread extends Thread
    {

        SendThread(String filename, String path)
        {

        }

        @Override
        public void run()
        {

        }
    }
}







