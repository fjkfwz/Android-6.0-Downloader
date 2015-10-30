package com.makeblaze.myapplication.DB;

import com.makeblaze.myapplication.entities.ThreadInfo;

import java.util.List;

/**
 * Created by jz on 2015/10/30.
 */
public interface ThreadDAO {
    public void insertThread(ThreadInfo threadInfo);

    public void deleteThread(String url, int thread_id);

    public void updateThread(String url, int thread_id,int finished);

    public List<ThreadInfo> getThreads(String url);
    public boolean isExist(String url,int thread_id);
}
