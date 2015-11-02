package com.makeblaze.myapplication.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.makeblaze.myapplication.entities.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by jz on 2015/10/29.
 */
public class DownloadService extends Service {
    public static final String Action_START = "ACTION_START";
    public static final String Action_STOP = "ACTION_STOP";
    public static final String Action_UPDATA = "ACTION_UPDATA";
    public static final String Action_FINISH = "ACTION_FINISH";
    protected static String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
    public static final int MSG_INIT = 0;
    private Map<Integer, DownloadTask> mTasks = new LinkedHashMap<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /**
         * Action
         */
        if (intent != null) {
            Log.i("Action", intent.getAction().toString());
            if (Action_START.equals(intent.getAction())) {
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                if (fileInfo != null) {
                    Log.i("test_ACTION_START", fileInfo.toString());
//                    new InitThread(fileInfo).start();
                    DownloadTask.executorService.execute(new InitThread(fileInfo));
                }
            } else if (Action_STOP.equals(intent.getAction())) {
                Log.i("test_Action_STOP", ">>>>>>>Download Task>>>>>>>>>");
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                if (fileInfo != null) {
                    Log.i("test_Action_STOP", ">>>>>>>Download Tasking>>>>>>>>>");
                    DownloadTask downloadTask = mTasks.get(fileInfo.getId());
                    if (downloadTask == null) {
                        Log.i("test_Action_STOP", ">>>>>>>Download Task Null>>>>>>>>>");
                    } else if (downloadTask != null) {
                        Log.i("test_Action_STOP", ">>>>>>>Download Task Stopping>>>>>>>>>");
                        downloadTask.isPause = true;
                    }

                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    Log.i("length", "length" + fileInfo.getLenght());
                    DownloadTask downloadTask = new DownloadTask(DownloadService.this, fileInfo, 3);
                    downloadTask.download();
                    mTasks.put(fileInfo.getId(), downloadTask);
                    break;
            }
        }

    };

    /**
     * Init Thread
     */
    class InitThread extends Thread {
        private FileInfo mFileInfo;

        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile randomAccessFile = null;
            try {
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                File dir = new File(DOWNLOAD_PATH);
                Log.i("PATH", dir.toString());
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, mFileInfo.getFilename());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(length);
                randomAccessFile.close();
                mFileInfo.setLenght(length);
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
        }
    }
}

