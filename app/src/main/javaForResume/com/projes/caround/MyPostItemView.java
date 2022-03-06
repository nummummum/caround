package com.projes.caround;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyPostItemView extends RelativeLayout {
    TextView myPost_title;
    TextView myPost_time;
    ImageView myPost_menu;

    public MyPostItemView(Context context) {
        super(context);

        init(context);
    }

    public MyPostItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_mypost, this,true);
        myPost_title = (TextView)findViewById(R.id.myPost_title);
        myPost_time = (TextView)findViewById(R.id.myPost_time);
        myPost_menu = (ImageView)findViewById(R.id.myPost_menu);
    }

    public void setMyPost_title(String post_title) {
        myPost_title.setText(post_title);
    }

    public void setMyPost_time(String post_time) {
        myPost_time.setText(post_time);
    }
}
