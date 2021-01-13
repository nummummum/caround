package com.projes.caround;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MyFragment extends Fragment {
    TextView alert;
    EditText sentence;
    View dialogView;
    StoryFragment storyFragment;
    TagFragment tagFragment;
    ReplyFragment replyFragment;
    BookmarkFragment bookmarkFragment;
    BaseDialog buzziDialog,totalDialog;
    Button btn_buzzy,btn_total;
    ImageView buzzi_close,total_close;

    ImageView left, right;
    ViewFlipper flipper;

    private FragmentPagerAdapter fragmentPagerAdapter;

    TextView[] total;
    Integer[] totalId={R.id.total1,R.id.total2,R.id.total3,R.id.total4};

    TextView []lastWeek;
    TextView []thisWeek;
    Integer[] lastWeekId={R.id.lastWeek_Mon,R.id.lastWeek_Tue,R.id.lastWeek_Wed,R.id.lastWeek_Thu,R.id.lastWeek_Fri,R.id.lastWeek_Sat,R.id.lastWeek_Sun};
    Integer[] thisWeekId={R.id.thisWeek_Mon,R.id.thisWeek_Tue,R.id.thisWeek_Wed,R.id.thisWeek_Thu,R.id.thisWeek_Fri,R.id.thisWeek_Sat,R.id.thisWeek_Sun};

    int[] lastWeek_count={2,3,1,4,5,6,7};
    int[] thisWeek_count={5,7,15,5,1,7,15};

    static int[] week1={8, 5, 1, 0};
    static int[] week2={3, 2, 9, 11};
    static int[] week3={7, 1, 1, 0};

    int weekcount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_mypage, container, false);
        weekcount=0;// 2->week1, 1->week2, 0->week3
        total=new TextView[totalId.length];
        lastWeek=new TextView[lastWeekId.length];
        thisWeek=new TextView[thisWeekId.length];
        storyFragment = new StoryFragment();
        tagFragment = new TagFragment();
        replyFragment = new ReplyFragment();
        bookmarkFragment = new BookmarkFragment();
        //뷰페이저 세팅
        ViewPager viewPager=(ViewPager) rootView.findViewById(R.id.viewPager);
        fragmentPagerAdapter= new ViewPagerAdapter(getChildFragmentManager());

        TabLayout tabLayout=(TabLayout) rootView.findViewById(R.id.tab_layout);
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //여기까지 마이페이지 안의 상세 프래그먼트 바꿔주는 코드

        alert=(TextView) rootView.findViewById(R.id.btn_textalert);
        alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView=(View)View.inflate(getContext(), R.layout.mypage_dialog,null);
                sentence=(EditText)dialogView.findViewById(R.id.sentence);
                AlertDialog.Builder dlg= new AlertDialog.Builder(getContext());
                dlg.setView(dialogView);
                sentence.setText(alert.getText().toString());
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.setText(sentence.getText().toString());
                        dialog.dismiss();//닫기
                    }
                });
                dlg.show();//dialog 창을 보여주는 역할
            }
        });

        btn_buzzy = (Button) rootView.findViewById(R.id.btn_buzzy);
        btn_buzzy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BuzziWindow();
            }
        });

        btn_total=(Button)rootView.findViewById(R.id.btn_total);
        btn_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TotalWindow();
            }
        });

        /*ViewPager myPagePager = (ViewPager) rootView.findViewById(R.id.myPagePager);
        myPagePager.setOffscreenPageLimit(4);
        MyPagePagerAdapter myPagePagerAdapter = new MyPagePagerAdapter(getChildFragmentManager());
        StoryFragment storyFragment = new StoryFragment();
        TagFragment tagFragment = new TagFragment();
        ReplyFragment replyFragment = new ReplyFragment();
        BookmarkFragment bookmarkFragment = new BookmarkFragment();
        myPagePagerAdapter.addItem(storyFragment);
        myPagePagerAdapter.addItem(tagFragment);
        myPagePagerAdapter.addItem(replyFragment);
        myPagePagerAdapter.addItem(bookmarkFragment);
        myPagePager.setAdapter(myPagePagerAdapter);*/


        return rootView;
    }
    class MyPagePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> myPageItems = new ArrayList<Fragment>();

        public MyPagePagerAdapter(@NonNull FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return myPageItems.get(position);
        }

        @Override
        public int getCount() {
            return myPageItems.size();
        }

        public void addItem(Fragment item){
            myPageItems.add(item);
        }
    }

    private void BuzziWindow(){
        buzziDialog = new BaseDialog(getContext(), R.layout.buzzi_layout);
        buzzi_close=(ImageView) buzziDialog.findViewById(R.id.buzzi_close);
        for(int i=0;i<lastWeekId.length;i++)
        {
            lastWeek[i]=(TextView)buzziDialog.findViewById(lastWeekId[i]);
            thisWeek[i]=(TextView)buzziDialog.findViewById(thisWeekId[i]);
            buzziBarGraph(0,i,lastWeek_count[i]);
            buzziBarGraph(1,i,thisWeek_count[i]);
        }

        buzzi_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buzziDialog.cancel();
            }
        });
        buzziDialog.show();
    }

    private void TotalWindow(){

        totalDialog=new BaseDialog(getContext(),R.layout.total_layout);
        flipper = (ViewFlipper)totalDialog.findViewById(R.id.ViewFlipper);
        total_close=(ImageView)totalDialog.findViewById(R.id.total_close);
        left=(ImageView)totalDialog.findViewById(R.id.left);
        right=(ImageView)totalDialog.findViewById(R.id.right);

        for(int i=0;i<totalId.length;i++)
        {
            total[i]=(TextView)totalDialog.findViewById(totalId[i]);
        }
        emotionBarGraph(weekcount);
        total_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalDialog.cancel();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekcount > 0 && weekcount <= 2) {
                    weekcount--;
                    flipper.showPrevious();
                    emotionBarGraph(weekcount);
                }
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (weekcount < 2 && weekcount >= 0) {
                    weekcount++;
                    flipper.showNext();
                    emotionBarGraph(weekcount);
                }
            }
        });
        totalDialog.show();
    }


    //버찌 그래프를 그려주는 함수
    private void buzziBarGraph(int compare,int i,int count){
        if(compare==0)
        {
            ViewGroup.LayoutParams params = lastWeek[i].getLayoutParams();
            if(count>0){
                params.height = 30*count; //그래프바를 35*count 픽셀 단위로 올려줌
                lastWeek[i].setLayoutParams(params);//setLayoutParams() ->
                String s= Integer.toString(count);
                lastWeek[i].setText(s);}

            else if(count==0){
                params.height = 1; //그래프바를 35*count 픽셀 단위로 올려줌
                lastWeek[i].setLayoutParams(params);//setLayoutParams() ->
                lastWeek[i].setText("");}
        }
        if(compare==1)
        {
            ViewGroup.LayoutParams params = thisWeek[i].getLayoutParams();
            if(count>0){
                params.height = 30*count; //그래프바를 35*count 픽셀 단위로 올려줌
                thisWeek[i].setLayoutParams(params);//setLayoutParams() ->
                String s= Integer.toString(count);
                thisWeek[i].setText(s);}

            else if(count==0){
                params.height = 1; //그래프바를 35*count 픽셀 단위로 올려줌
                thisWeek[i].setLayoutParams(params);//setLayoutParams() ->
                thisWeek[i].setText("");}
        }
    }


    //감정표현 그래프를 그려주는 함수
    private void emotionBarGraph(int number){
        if(number==2)
        {
            for(int i=0;i<week3.length;i++)
            {
                BarHeight(i,week3[i]);
            }
        }
        if(number==1)
        {
            for(int i=0;i<week3.length;i++)
            {
                BarHeight(i,week2[i]);
            }
        }
        if(number==0)
        {
            for(int i=0;i<week3.length;i++)
            {
                BarHeight(i,week1[i]);
            }
        }
    }

    private void BarHeight(int i,int count)//xml코드에서 정의한 textView코드 변경
    {
        //일반속성의 경우에는 set을 통해 속성값을 변경할수 있지만, 레이아웃 파라미터를 변경하는 메서드는 제공하지 않는다
        //void TextView.setLayoutWidth(), setLayoutWeight(), setLayoutX() 사용불가
        ViewGroup.LayoutParams params = total[i].getLayoutParams();
        //ViewGroup.LayoutParams -> layout_width,layout_height를 참조할수 있음
        //getLayoutParams() -> 현재 레이아웃 요소의 속성 객체를 얻어온다.
        if(count>0){
            params.width = 22*count; //그래프바를 35*count 픽셀 단위로 올려줌
            total[i].setLayoutParams(params);//setLayoutParams() ->
            String s= Integer.toString(count);
            total[i].setText(s);}

        else if(count==0){
            params.width = 1; //그래프바를 35*count 픽셀 단위로 올려줌
            total[i].setLayoutParams(params);//setLayoutParams() ->
            total[i].setText("");}
    }

}



