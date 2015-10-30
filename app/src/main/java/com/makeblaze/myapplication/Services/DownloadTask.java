package com.makeblaze.myapplication.Services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.makeblaze.myapplication.DB.ThreadDAO;
import com.makeblaze.myapplication.DB.ThreadDAOImpl;
import com.makeblaze.myapplication.entities.FileInfo;
import com.makeblaze.myapplication.entities.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jz on 2015/10/30.
 */
public class DownloadTask {

    private Context mContext;
    private FileInfo mFileinfo;
    private ThreadDAO mDao;
    private int mFinish;
    protected boolean isPause = false;

    public void download() {
        List<ThreadInfo> threadinfos = mDao.getThreads(mFileinfo.getUrl());
        ThreadInfo threadinfo;
        if (threadinfos.size() == 0) {
            threadinfo = new ThreadInfo(0, mFileinfo.getUrl(), 0, 0, mFileinfo.getLenght());
        } else {
            threadinfo = threadinfos.get(0);
        }
        new DownloadThread(threadinfo).start();
    }

    public DownloadTask(Context mContext, FileInfo mFileinfo) {
        this.mContext = mContext;
        this.mFileinfo = mFileinfo;
        mDao = new ThreadDAOImpl(mContext);
    }

    class DownloadThread extends Thread {
        private ThreadInfo mThreadinfo = null;
        private HttpURLConnection conn;
        private RandomAccessFile randomAccessFile;
        private InputStream input;

        public DownloadThread(ThreadInfo mThreadinfo) {
            this.mThreadinfo = mThreadinfo;
        }

        @Override
        public void run() {
            super.run();
            if (!mDao.isExist(mThreadinfo.getUrl(), mThreadinfo.getId())) ;
            mDao.insertThread(mThreadinfo);
            try {
                URL url = new URL(mThreadinfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start = mThreadinfo.getStart() + mThreadinfo.getFinish();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadinfo.getEnd());
                Log.i("RequestProperty","bytes=" + start + "-" + mThreadinfo.getEnd());
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileinfo.getFilename());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(start);
                mFinish += mThreadinfo.getFinish();
                Log.i("CODE", "" + conn.getResponseCode());
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    input = conn.getInputStream();
                    Intent intent = new Intent(DownloadService.Action_UPDATA);
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        Log.i("lenght", "" + len);
                        randomAccessFile.write(buffer, 0, len);
                        mFinish += len;
                        if (System.currentTimeMillis() - time > 500)
                            intent.putExtra("finish", mFinish * 100 / mFileinfo.getLenght());
                        mContext.sendBroadcast(intent);
                    }
                    if (isPause) {
                        mDao.updateThread(mThreadinfo.getUrl(), mThreadinfo.getId(), mFinish);
                        return;
                    }
                }
                mDao.deleteThread(mThreadinfo.getUrl(), mThreadinfo.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("CLOSE", "OK");
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("CLOSE", "OK");
                conn.disconnect();
                Log.i("CLOSE", "OK");
            }
        }
    }
}

