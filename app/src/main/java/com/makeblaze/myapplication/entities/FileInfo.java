package com.makeblaze.myapplication.entities;

import java.io.Serializable;

/**
 * Created by jz on 2015/10/29.\
 * File Information
 */
public class FileInfo implements Serializable{
    private int id;
    private String url;
    private String filename;
    private int lenght;
    private int finfished;

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }

    public int getFinfished() {
        return finfished;
    }

    public void setFinfished(int finfished) {
        this.finfished = finfished;
    }

    public String getFilename() {

        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public FileInfo(int id, String url, String filename, int lenght, int finfished) {
        this.id = id;
        this.url = url;
        this.filename = filename;
        this.lenght = lenght;
        this.finfished = finfished;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", filename='" + filename + '\'' +
                ", lenght=" + lenght +
                ", finfished=" + finfished +
                '}';
    }
}
