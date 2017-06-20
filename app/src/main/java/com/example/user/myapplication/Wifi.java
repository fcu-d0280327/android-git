package com.example.user.myapplication;

import android.graphics.Bitmap;

public class Wifi {

    private String addr;

    private String name;
    private String LATITUDE;

    public String getName() {
        return name;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getLONGITUDE() {
        return LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    private String LONGITUDE;
    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getname() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}