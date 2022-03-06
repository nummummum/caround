package com.projes.caround;

public class MentionItem {
    private String mContent;
    private String mdate;
    private int user_image;
    private int posted;
    //private int mId;


    public MentionItem(String mContent, String mdate, int user_image, int posted) {
        this.mContent = mContent;
        this.mdate = mdate;
        this.user_image = user_image;
        this.posted = posted;
        //this.mId = mId;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public int getUser_image() {
        return user_image;
    }

    public void setUser_image(int user_image) {
        this.user_image = user_image;
    }

    public int getPosted() {
        return posted;
    }

//    public int getmId(){
//        return mId;
//    }
}
