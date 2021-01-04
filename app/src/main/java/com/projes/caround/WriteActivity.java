package com.projes.caround;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_cancle;
    ImageView btn_save;
    ImageView btn_emotion;
    ImageView btn_background;
    ImageView btn_location;
    ImageView btn_gallary;
    ImageView btn_mic;
    ImageView btn_music;
    ImageView btn_sharp;
    ImageView emotionImg;
    TextView emotionText;
    EditText contentText;
    GridView gridgallary;
    ListView listEmotion;
    Animation translateUp;
    Animation translateDown;
    GridAdapter gridAdapter;
    EmojiAdapter emojiAdapter;
    LinearLayout linearLayouttop;
    LinearLayout linearLayoutcenter;
    LinearLayout linearLayoutblack;
    FrameLayout writebackground;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_write);
        btn_gallary = findViewById(R.id.img_gallary);
        btn_gallary.setOnClickListener(this);
        btn_emotion = findViewById(R.id.img_emotion);
        btn_emotion.setOnClickListener(this);
        emotionImg = findViewById(R.id.emotion_image);
        emotionText = findViewById(R.id.emotion_text);
        gridgallary = findViewById(R.id.grid_gallary);
        listEmotion = findViewById(R.id.list_emotion);
        linearLayouttop = findViewById(R.id.linearLayout2);
        linearLayoutcenter = findViewById(R.id.linearLayout);
        linearLayoutblack = findViewById(R.id.linearblack);
        contentText = findViewById(R.id.centent_text);
        writebackground = findViewById(R.id.writebackground);
        gridAdapter = new GridAdapter();
        gridAdapter.addItem(new GridItem(R.drawable.exam1));
        gridAdapter.addItem(new GridItem(R.drawable.exam2));
        gridAdapter.addItem(new GridItem(R.drawable.exam3));
        gridAdapter.addItem(new GridItem(R.drawable.exam4));
        gridAdapter.addItem(new GridItem(R.drawable.exam5));
        gridgallary.setAdapter(gridAdapter);
        emojiAdapter = new EmojiAdapter();
        emojiAdapter.addItem(new EmojiItem("웃음",R.drawable.emoji1));
        emojiAdapter.addItem(new EmojiItem("행복",R.drawable.emoji2));
        emojiAdapter.addItem(new EmojiItem("Flex",R.drawable.emoji4));
        emojiAdapter.addItem(new EmojiItem("사랑",R.drawable.emoji5));
        emojiAdapter.addItem(new EmojiItem("서운",R.drawable.emoji6));
        emojiAdapter.addItem(new EmojiItem("찡긋",R.drawable.emoji7));
        emojiAdapter.addItem(new EmojiItem("웃겨죽음",R.drawable.emoji8));
        listEmotion.setAdapter(emojiAdapter);
        translateUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_up);
        translateDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_down);

        gridgallary.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                writebackground.setBackgroundResource(gridAdapter.getItem(i).getImage());
            }
        });
        listEmotion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                emotionImg.setImageResource(emojiAdapter.getItem(i).getImage());
                emotionText.setText(emojiAdapter.getItem(i).getText());
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(view==btn_gallary && gridgallary.getVisibility()==View.VISIBLE)
        {
            gridgallary.setVisibility(View.INVISIBLE);
            linearLayouttop.setVisibility(View.VISIBLE);
            linearLayoutcenter.setVisibility(View.VISIBLE);
            linearLayoutblack.setVisibility(View.VISIBLE);
            gridgallary.startAnimation(translateDown);
            Handler kHandler =new Handler();
            kHandler.postDelayed(new Runnable(){
                public void run(){
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(contentText,InputMethodManager.SHOW_IMPLICIT);
                    //키보드 올리기
                }
            },200);
        }
        else if(view==btn_gallary && gridgallary.getVisibility()==View.INVISIBLE)
        {
//            if(listEmotion.getVisibility()==View.VISIBLE)
//            {
//                listEmotion.setVisibility(View.INVISIBLE);
//            }
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            //키보드 내리기
            Handler kHandler =new Handler();
            kHandler.postDelayed(new Runnable(){
                public void run(){
                    gridgallary.setVisibility(View.VISIBLE);
                    linearLayouttop.setVisibility(View.INVISIBLE);
                    linearLayoutcenter.setVisibility(View.INVISIBLE);
                    linearLayoutblack.setVisibility(View.INVISIBLE);
                    gridgallary.startAnimation(translateUp);
                }
            },200);
        }
        else if(view==btn_emotion && listEmotion.getVisibility()==View.VISIBLE)
        {
            listEmotion.setVisibility(View.INVISIBLE);
            linearLayouttop.setVisibility(View.VISIBLE);
            linearLayoutcenter.setVisibility(View.VISIBLE);
            linearLayoutblack.setVisibility(View.VISIBLE);
            listEmotion.startAnimation(translateDown);
            Handler kHandler =new Handler();
            kHandler.postDelayed(new Runnable(){
                public void run(){
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.showSoftInput(contentText,InputMethodManager.SHOW_IMPLICIT);
                    //키보드 올리기
                }
            },200);
        }
        else if(view==btn_emotion && listEmotion.getVisibility()==View.INVISIBLE)
        {
//            if(gridgallary.getVisibility()==View.VISIBLE)
//            {
//                gridgallary.setVisibility(View.INVISIBLE);
//            }
            InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            //키보드 내리기
            Handler kHandler =new Handler();
            kHandler.postDelayed(new Runnable(){
                public void run(){
                    listEmotion.setVisibility(View.VISIBLE);
                    linearLayouttop.setVisibility(View.INVISIBLE);
                    linearLayoutcenter.setVisibility(View.INVISIBLE);
                    linearLayoutblack.setVisibility(View.INVISIBLE);
                    listEmotion.startAnimation(translateUp);
                }
            },200);
        }
    }
    class GridAdapter extends BaseAdapter{
        ArrayList<GridItem> items = new ArrayList<GridItem>();
        //GridItem 데이터 객체 생성할 수 있도록 ArrayList형태로 여러개 저장하게 함.
        @Override
        public int getCount() { //items개수 관리
            return items.size();
        }

        public void addItem(GridItem gridItem){ //GridItem 객체를 items목록에 저장
            items.add(gridItem);
        }
        @Override
        public GridItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) { //아이템에 id세팅. 삭제,검색 등의 이유로 설정해줘야함
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //가장 중요. 이 메소드에서 리턴하는 뷰가 선택 위젯에 하나의 목록으로 출력됨.
            GridItemViewer gridItemViewer = new GridItemViewer(getApplicationContext());
            gridItemViewer.setItem(items.get(i));
            return gridItemViewer;
        }
    }
    class EmojiAdapter extends BaseAdapter{
        ArrayList<EmojiItem> items=new ArrayList<EmojiItem>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(EmojiItem emojiItem){
            items.add(emojiItem);
        }
        @Override
        public EmojiItem getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            EmojiItemViewer emojiItemViewer = new EmojiItemViewer(getApplicationContext());
            emojiItemViewer.setItem(items.get(i));
            return emojiItemViewer;
        }
    }
}
