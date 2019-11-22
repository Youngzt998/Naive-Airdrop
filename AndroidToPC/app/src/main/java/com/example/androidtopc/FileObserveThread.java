package com.example.androidtopc;

import android.os.Environment;
import android.os.FileObserver;

import java.util.EventListener;

public class FileObserveThread extends Thread
{
    private MyFileObserver myFileObserver;

    FileObserveThread()
    {
        myFileObserver = new MyFileObserver(Environment.getExternalStorageDirectory().getPath());
        myFileObserver.startWatching();

    }

    @Override
    public void run()
    {

    }
}

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

        }
    }


}
