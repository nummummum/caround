package com.projes.caround;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MentionItemView extends RelativeLayout {
    ImageView user_image;
    TextView user_mention;
    TextView mention_time;
    ImageView btn_user_buzzi;

    public MentionItemView(Context context) {
        super(context);

        init(context);
    }

    public MentionItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.item_mention, this,true);
        user_image = (ImageView) findViewById(R.id.user_image);
        user_mention = (TextView) findViewById(R.id.user_mention);
        mention_time = (TextView) findViewById(R.id.mention_time);
        btn_user_buzzi = (ImageView) findViewById(R.id.btn_user_buzzi);
    }

    public void setUser_mention(String mention){
        user_mention.setText(mention);
    }
    public void setMention_time(String time){
        mention_time.setText(time);
    }

    public void setUser_image(int user_img){
        user_image.setImageResource(user_img);
    }
}
