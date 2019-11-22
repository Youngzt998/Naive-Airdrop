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
import java.util.logging.Handler;
import java.util.logging.LogRecord;


// the thread for connecting to the server
class TcpClientThread extends Thread
{
    private String TAG = "TcpClientThread";


    private String testFileName = "test.mp4";
    private String testFilePath = "/0_ComputerNetworkProject/"+testFileName;
    private String myCameraPath = "/DCIM/Camera";

    private String UPLOAD_FILE_REQUEST = "UPLOADFILE";

    private String ASK_FILE_NAME_ANSWER = "ASK_FILE_NAME";
    private String RECEIVE_FILE_ANSWER = "RECEIVE_FILE";

    private String CANCEL_UPLOAD_REQUEST = "CANCELUPLOAD";

    private String serverName;
    private String serverIP;
    private int serverPort;

    private int DEFAULT_PACKET_SIZE = 1024;

    BroadCastMessageHandler broadCastMessageHandler;
    UdpReceiveThread udpReceiveThread;

    Socket clientSocket;
    InputStream inputStream;
    OutputStream outputStream;

    TcpClientThread()
    {
        try {
            // for receiving broad cast message from subthread
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

            String fileName = testFileName;
            String filePath = Environment.getExternalStorageDirectory().getPath()
                    + testFilePath;

            //starting upload a file
            if (uploadFile(fileName, filePath) != 0)
            {
                Log.d(TAG, "upload failed!");
            }
            Log.d(TAG, "connect finished sucessfully!");
            while (true){}
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
    int uploadFile(String filename, String localPath)
    {

        //start file transfer
        byte[] receiveBuffer = new byte[32];
        byte[] header = new byte[4];
        String prefix = "filename ";

        String receoveData;
        int receiveLen;
        try {
            //TBD: while still being connected
            while (true)
            {

                try {
                    // ask to upload a file
                    outputStream.write(UPLOAD_FILE_REQUEST.getBytes());
                    //receive "asking name answer"
                    receiveLen = inputStream.read(receiveBuffer);
                    receoveData = new String(receiveBuffer, 0, receiveLen);
                    System.out.println("yzt1: "+ receoveData);
                    if (receoveData.compareTo(ASK_FILE_NAME_ANSWER)!=0)
                    {
                        outputStream.write(CANCEL_UPLOAD_REQUEST.getBytes());
                        continue;
                    }
                    //tell file name
                    outputStream.write((prefix+filename).getBytes());

                    //receive upload file answer
                    receiveLen = inputStream.read(receiveBuffer);
                    receoveData = new String(receiveBuffer, 0, receiveLen);
                    System.out.println("yzt2: "+receoveData.toString());
                    if(receoveData.compareTo(RECEIVE_FILE_ANSWER)!=0)
                    {
                        outputStream.write(CANCEL_UPLOAD_REQUEST.getBytes());
                        continue;
                    }

                }catch (Exception e){
                    Log.d(TAG, "pre trans error");
                    e.printStackTrace();
                    //continue;
                }

                try {
                    //open target file
                    File file = new File(localPath);
                    FileInputStream fileOutStream = new FileInputStream(file);
                    int count = fileOutStream.available();
                    System.out.println("yzt3: test file size is "+count);

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

                    System.out.println("yzt4: client finished sending file");

                    //outputStream.flush();
                    fileOutStream.close();



                }catch (Exception e){
                    Log.d(TAG, "Open trans error");
                    e.printStackTrace();
                }

                // outputStream.write("One plus 7p".getBytes());
                interrupted();
            }

        }catch (Exception e){
            Log.d(TAG, "write failed!");
            e.printStackTrace();

        }
        return 0;
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







