package com.example.androidtopc;

import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class FileObserveThread extends Thread
{
    private String TAG = "FileObserveThread";


    //bound to a certain file, detect the change inside it
    private File file;
    private String observePath;

    private ArrayList<String> fileInfo;
    private ArrayList<String> currentInfo;  // for tmp usage

    FileObserveThread(String observePath)
    {
        this.observePath = observePath;
    }

    @Override
    public void run()
    {
        while (!interrupted())
        {
            //Log.d(TAG, "scanning the observed path");
            //record all the files in the observing directory
            currentInfo = new ArrayList<String>();
            file = new File(observePath);
            traverseFile(file, "");

            fileInfo = currentInfo;

            try {
                sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void traverseFile(File file, String relativePath)
    {
        //Log.d(TAG, relativePath + " isDirectory(): "+ file.isDirectory());
        if(!file.isDirectory())
        {
            currentInfo.add(relativePath);
            return;
        }


        File[] files = file.listFiles();

        //currently we do not send an empty file...
        if(files == null)return;

        for (int i = 0;i< files.length;++i)
        {
            traverseFile(files[i], relativePath + "/" + files[i].getName() );
        }

    }

    public ArrayList<String> getInfo()
    {
        return fileInfo;
    }
}








/**********************************************************************************************************/



//not always work, stop using it...
class MyFileObserver extends FileObserver
{
    MyFileObserver(String path, int mask)
    {
            super(path, mask);
    }

    MyFileObserver(String path)
    {
        super(path);
    }

    @Override
    public void onEvent(int event, String path)
    {
        final int action = event & FileObserver.ALL_EVENTS;
        switch (action)
        {
            case FileObserver.ACCESS:
                System.out.println("event: 文件或目录被访问, path: " + path);
                break;
                case FileObserver.DELETE:
                    System.out.println("event: 文件或目录被删除, path: " + path);
                    break;
                    case FileObserver.OPEN:
                        System.out.println("event: 文件或目录被打开, path: " + path);
                        break;
                        case FileObserver.MODIFY:
                            System.out.println("event: 文件或目录被修改, path: " + path);
                            break;
                            case FileObserver.CREATE:
                                System.out.println("event: 文件或目录被创建, path: " + path);
                                break;
            case FileObserver.MOVED_TO:
                System.out.println("event: 文件或目录被移入, path: " + path);
                break;
            case FileObserver.MOVED_FROM:
                System.out.println("event: 文件或目录被移出, path: " + path);
                break;

                default:
                    break;


        }
    }


}
