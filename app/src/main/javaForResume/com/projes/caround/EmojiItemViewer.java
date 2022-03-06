package com.projes.caround;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

public class EmojiItemViewer extends LinearLayout {
    ImageView imageView;
    TextView textView;

    public EmojiItemViewer(Context context) {
        super(context);
        init(context);
    }
    public EmojiItemViewer(Context context, @Nullable AttributeSet attrs){
        super(context,attrs);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.emotionitem,this,true);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
    }
    public void setItem(EmojiItem emojiItem){
        textView.setText(emojiItem.getText());
        imageView.setImageResource(emojiItem.getImage());
    }
}
