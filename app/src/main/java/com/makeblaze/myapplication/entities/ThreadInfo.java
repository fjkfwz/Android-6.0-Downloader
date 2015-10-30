package com.makeblaze.myapplication.entities;

/**
 * Created by jz on 2015/10/29.
 * Thread Information
 */
public class ThreadInfo {
    private int id;
    private String url;
    private int start;
    private int finish;
    private int end;

    public ThreadInfo() {
    }

    public ThreadInfo(int id, String url, int start, int finish, int end) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.finish = finish;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getFinish() {
        return finish;
    }

    public void setFinish(int finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", finish=" + finish +
                '}';
    }
}
