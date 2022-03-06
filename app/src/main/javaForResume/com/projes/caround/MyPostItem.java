package com.projes.caround;

import android.graphics.drawable.Drawable;

public class MyPostItem {
    private String myPostContent;
    private String myPostDate;
    private String myPostLocation;
    private Drawable myPostBackground;
    private int posted;

    public MyPostItem(String myPostContent, String myPostDate, String myPostLocation, Drawable myPostBackground, int posted) {
        this.myPostContent = myPostContent;
        this.myPostDate = myPostDate;
        this.myPostLocation = myPostLocation;
        this.myPostBackground = myPostBackground;
        this.posted = posted;
    }

    public String getMyPostContent() {
        return myPostContent;
    }

    public void setMyPostContent(String myPostContent) {
        this.myPostContent = myPostContent;
    }

    public String getMyPostDate() {
        return myPostDate;
    }

    public void setMyPostDate(String myPostDate) {
        this.myPostDate = myPostDate;
    }

    public String getMyPostLocation() {
        return myPostLocation;
    }

    public void setMyPostLocation(String myPostLocation) {
        this.myPostLocation = myPostLocation;
    }

    public Drawable getMyPostBackground() {
        return myPostBackground;
    }

    public void setMyPostBackground(Drawable myPostBackground) {
        this.myPostBackground = myPostBackground;
    }

    public int getPosted() {
        return posted;
    }

    public void setPosted(int posted) {
        this.posted = posted;
    }
}
