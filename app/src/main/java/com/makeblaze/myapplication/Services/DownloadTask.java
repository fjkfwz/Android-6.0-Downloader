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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jz on 2015/10/30.
 */
public class DownloadTask {

    private Context mContext;
    private FileInfo mFileinfo;
    private ThreadDAO mDao;
    private int mFinish;
    protected volatile boolean isPause = false;
    private int mThreadCount = 1;
    private List<DownloadThread> downloadThreadList;
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public void download() {
        ThreadInfo threadinfo;
        List<ThreadInfo> threadinfos = mDao.getThreads(mFileinfo.getUrl());
        Log.i("download", threadinfos.size() + "");
        if (threadinfos.size() == 0) {
            Log.i("download", "threadinfos.size() == 0");
            int length = mFileinfo.getLenght() / mThreadCount;
            Log.i("mFileinfo.getLengtht()", mFileinfo.getLenght() + "");
            Log.i("mFileinfo.getLengtht()", mFileinfo.getLenght() / mThreadCount + "");
            for (int i = 0; i < mThreadCount; i++) {
                Log.i("i", i + "");
                threadinfo = new ThreadInfo(i, mFileinfo.getUrl(), length * i, length * (i + 1) - 1, 0);
                if (i == mThreadCount - 1) {
                    threadinfo.setEnd(mFileinfo.getLenght());
                }
                Log.i("i", i + "" + threadinfo.toString());
                threadinfos.add(threadinfo);
                mDao.insertThread(threadinfo);
            }

        }
        Log.i("threadinfos", threadinfos.toString());
        downloadThreadList = new ArrayList<>();
        for (ThreadInfo threadInfo : threadinfos) {
            Log.i("threadInfo", threadInfo.toString());
            DownloadThread thread = new DownloadThread(threadInfo);
            executorService.execute(thread);
            downloadThreadList.add(thread);
        }
    }

    public DownloadTask(Context mContext, FileInfo mFileinfo, int mThreadCount) {
        this.mContext = mContext;
        this.mFileinfo = mFileinfo;
        this.mThreadCount = mThreadCount;
        mDao = new ThreadDAOImpl(mContext);
    }

    private synchronized void checkAllThreadFinish(ThreadInfo mThreadinfo) {
        boolean allFinish = true;

        for (DownloadThread downloadThread : downloadThreadList) {
            if (!downloadThread.isFinish) {
                allFinish = false;
                break;
            }
            if (allFinish) {
                mDao.deleteThread(mThreadinfo.getUrl());
                Intent intent = new Intent(DownloadService.Action_FINISH);
                intent.putExtra("fileinfo", mFileinfo);
                intent.putExtra("id", mFileinfo.getId());
                mContext.sendBroadcast(intent);
            }
        }
    }

    class DownloadThread extends Thread {
        private ThreadInfo mThreadinfo = null;
        private HttpURLConnection conn;
        private RandomAccessFile randomAccessFile;
        private InputStream input;
        private boolean isFinish = false;

        public DownloadThread(ThreadInfo mThreadinfo) {
            this.mThreadinfo = mThreadinfo;
            Log.i("mThreadinfo", mThreadinfo.toString());
        }

        @Override
        public void run() {
            super.run();
            try {
                URL url = new URL(mThreadinfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int start = mThreadinfo.getStart();
                int end = mThreadinfo.getFinish();
                conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
                Log.i("RequestProperty", "bytes=" + start + "-" + end);
                File file = new File(DownloadService.DOWNLOAD_PATH, mFileinfo.getFilename());
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.seek(start);
                Log.i("CODE", "" + conn.getResponseCode());
                if (conn.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    Log.i("CODE", "" + conn.getResponseCode());
                    input = conn.getInputStream();
                    Intent intent = new Intent(DownloadService.Action_UPDATA);
                    byte[] buffer = new byte[1024 * 4];
                    Log.i("buffer", buffer.toString());
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = input.read(buffer)) != -1) {
                        randomAccessFile.write(buffer, 0, len);
                        mFinish += len;
                        mThreadinfo.setFinish(mThreadinfo.getFinish() + len);
                        if (System.currentTimeMillis() - time > 1000) {
                            intent.putExtra("finished", mFinish * 100 / mFileinfo.getLenght());
                            intent.putExtra("id", mFileinfo.getId());
                            mContext.sendBroadcast(intent);
                            time = System.currentTimeMillis();
                            Log.i("UPDATA", ">>>>>>>>>UPDATA>>>>>>>");
                            if (isPause) {
                                Log.i("PAUSE", ">>>>>>>>>PAUSING>>>>>>>");
                                mDao.updateThread(mThreadinfo.getUrl(), mThreadinfo.getId(), mThreadinfo.getFinish());
                                return;
                            }
                        }
                    }

                }
                isFinish = true;
                checkAllThreadFinish(mThreadinfo);
            } catch (IOException e) {
                Log.i("bug", "bugs");
                e.printStackTrace();
            } finally {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    Log.i("bug1", "bugs1");
                    e.printStackTrace();
                }
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                    Log.i("bug2", "bugs2");
                    e.printStackTrace();
                }
                conn.disconnect();
            }
        }
    }
}

