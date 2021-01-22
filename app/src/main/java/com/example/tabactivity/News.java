package com.example.tabactivity;

import java.io.Serializable;

public class News implements Serializable {
    public String headline;
    public String imageUrl;
    public String url;
    public String section;
    public String date;

    News(String h,String u,String i,String s,String d){
        headline = h;
        imageUrl = i;
        url = u;
        section = s;
        date=d;
    }
}
