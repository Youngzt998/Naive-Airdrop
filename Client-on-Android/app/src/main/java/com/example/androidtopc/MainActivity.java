package com.example.androidtopc;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.MulticastSocket;
import java.nio.file.Path;
import java.util.concurrent.locks.Lock;


public class MainActivity extends AppCompatActivity {

    ServerSocket serverSocket;
    Socket socket;
    private int serverPort;

    private static String TAG = "MainActivity";
    private static int SET_FILE_DIR = 1000;
    private static int REQUEST_CODE_QR_STRING = 1001;

    private String observePath;
    private String path_1;
    private String path_2;
    private String testPath = "/0_ComputerNetworkProject/test.jpeg";
    private String testDir = "0_ComputerNetworkProject";
    private String myCameraPath = "/DCIM/Camera";

    private String id = "";
    private String serverId = "";
    private String key = "";

    EditText editText;

    TcpClientThread tcpClientThread;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        applyFileOperationPermission();

        //start client thread
        path_1 = Environment.getExternalStorageDirectory().getPath();
        try {
            // create file path configure file if it do not exist
            (new FileOutputStream(this.getFilesDir().getAbsolutePath() + "/filepath.config", true)).close();
            (new FileOutputStream(this.getFilesDir().getAbsolutePath() + "/register.config", true)).close();

            FileInputStream fileInputStream = new FileInputStream(this.getFilesDir().getAbsolutePath() + "/filepath.config");
            byte[] buffer = new byte[256];
            int l = fileInputStream.read(buffer);
            StringBuilder stringBuilder = new StringBuilder("");
            while (l>0)
            {
                stringBuilder.append(new String(buffer, 0, l));
                l = fileInputStream.read(buffer);
            }
            path_2 = stringBuilder.toString();
            Log.d(TAG, "path_2 is : " + path_2);



            fileInputStream = new FileInputStream(this.getFilesDir().getAbsolutePath() + "/register.config");
            buffer = new byte[256];
            l = fileInputStream.read(buffer);
            stringBuilder = new StringBuilder("");
            while (l>0)
            {
                stringBuilder.append(new String(buffer, 0, l));
                l = fileInputStream.read(buffer);
            }
            String[] code = (stringBuilder.toString()).split(" ");
            id = code[0];
            serverId = code[1];
            key = code[2];
            Log.d(TAG, "code is : " + id + " " + serverId + " " + key);
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "failed opening filepath.config");
        }

        handler = new Handler();

        observePath = Environment.getExternalStorageDirectory().getPath() + myCameraPath;
        tcpClientThread = new TcpClientThread(path_1 + path_2, id, serverId, key);
        tcpClientThread.start();

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(path_2);


        //UdpSendThread udpSendThread = new UdpSendThread(this.getApplicationContext());
        //udpSendThread.start();

    }


    // set the observing directory
    public void onClickBtnSetFileDirByText(View view)
    {
        Log.d(TAG, "Content is " + editText.getText().toString());

        String newPath_2 = editText.getText().toString();

        if(!(new File(path_1 + newPath_2)).exists())
        {
            Toast.makeText(MainActivity.this,"No such directory!", Toast.LENGTH_SHORT).show();
            return;
        }
        tcpClientThread.interrupt();

        path_2 = newPath_2;
        editText.setText(path_2);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.getFilesDir().getAbsolutePath() + "/filepath.config");
            fileOutputStream.write(path_2.getBytes());
            Log.d(TAG, this.getFilesDir().getAbsolutePath());
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "failed opening filepath.config");
        }

        tcpClientThread = new TcpClientThread(path_1 + path_2, id, serverId, key);
        tcpClientThread.start();
    }



    public void onClickBtnRegister(View view)
    {
        Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CODE_QR_STRING);
        Log.d(TAG, "QR Code is: " + newActivityResultString);
        if(newActivityResultString == null || newActivityResultString.equals("null") )
        {
            Log.d(TAG, "Scan failed: string is null");
            Toast.makeText(MainActivity.this,"Scan failed, retry !!", Toast.LENGTH_LONG).show();
            return;
        }
        String[] codes = newActivityResultString.split(" ");
        try {
            if(codes[0].length()!=16 || codes[1].length()!=16 || codes[2].length()!= 16)
            {
                Log.d(TAG, "Scan failed: wrong QR Code");
                Toast.makeText(MainActivity.this,"Wrong QR Code, retry !!", Toast.LENGTH_LONG).show();
                return;
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        Toast.makeText(MainActivity.this,"Register succussesfully!", Toast.LENGTH_LONG).show();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(this.getFilesDir().getAbsolutePath() + "/register.config");
            fileOutputStream.write(newActivityResultString.getBytes());
            Log.d(TAG, this.getFilesDir().getAbsolutePath());
        }
        catch (Exception e){
            e.printStackTrace();
            Log.d(TAG, "failed opening filepath.config");
        }

        id = codes[0];
        serverId = codes[1];
        key = codes[2];

        tcpClientThread.interrupt();
        tcpClientThread = new TcpClientThread(path_1 + path_2, id, serverId, key);
        tcpClientThread.start();
    }


    // TBD...
    public void onClickBtnSetFileDir(View view)
    {
        try{
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, SET_FILE_DIR);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("MainActivity ", "open file manager failed");
        }
    }

    public String newActivityResultString = "";
    public int test = -1;
    private int UPLOADING_SINGLE_FILE = 2000;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        test = 0;
        super.onActivityResult(requestCode, resultCode, data);
        //IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_QR_STRING)
        {
            Log.d(TAG, "REQUEST_CODE_QR_STRING");
            if(data!=null)
            {
                newActivityResultString= data.getStringExtra("resultString");
                Log.d(TAG, "intent data is: " + data.getDataString());
                Log.d(TAG, "intent data is: " + data.getStringExtra("resultString"));
            }
            else {
                Log.d(TAG, "intent data is null");
            }
        }
        else if(requestCode == SET_FILE_DIR)
        {
            Uri uri = data.getData();
            Toast.makeText(this, "single file path："+ uri.getPath().toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "single file path: " + uri.getPath().toString());
            newActivityResultString = getPhotoPathFromContentUri(this, uri)  ;
        }

        Message msg = new Message();
        msg.obj = newActivityResultString;
        tcpClientThread.handler.sendMessage(msg);

    }


    //learn from github
    public static String getPhotoPathFromContentUri(Context context, Uri uri) {
        String photoPath = "";
        if (context == null || uri == null) {
            return photoPath;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (isExternalStorageDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            } else if (isDownloadsDocument(uri)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                photoPath = getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String[] split = docId.split(":");
                if (split.length >= 2) {
                    String type = split[0];
                    Uri contentUris = null;
                    if ("image".equals(type)) {
                        contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            photoPath = uri.getPath();
        } else {
            photoPath = getDataColumn(context, uri, null, null);
        }

        return photoPath;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
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
