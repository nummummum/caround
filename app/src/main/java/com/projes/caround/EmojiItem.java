package com.projes.caround;

public class EmojiItem {
    private int image;
    private String text;

    public EmojiItem(String text, int image){
        this.text=text;
        this.image=image;
    }
    public void setImage(int image){
        this.image = image;
    }
    public void setText(String text){
        this.text = text;
    }
    public int getImage(){
        return image;
    }
    public String getText(){
        return text;
    }
}
