package com.projes.caround;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ScrollView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_cancle;
    Button btn_save;
    Button btn_emotion;
    Button btn_background;
    Button btn_location;
    Button btn_gallary;
    Button btn_mic;
    Button btn_music;
    Button btn_sharp;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.aaa); //실험을 위해 aaa 레이아웃으로 대체중.
    }

    @Override
    public void onClick(View view) {

    }
}
