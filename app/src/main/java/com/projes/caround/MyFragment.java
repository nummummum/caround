package com.projes.caround;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class MyFragment extends Fragment {
    TextView alert;
    EditText sentence;
    View dialogView;
    StoryFragment storyFragment;
    TagFragment tagFragment;
    ReplyFragment replyFragment;
    BookmarkFragment bookmarkFragment;
    BaseDialog buzziDialog;
    Button btn_buzzy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_mypage, container, false);

        storyFragment = new StoryFragment();
        tagFragment = new TagFragment();
        replyFragment = new ReplyFragment();
        bookmarkFragment = new BookmarkFragment();

        getChildFragmentManager().beginTransaction().replace(R.id.myContainer, storyFragment).commit();

        TextView btn_story = (TextView) rootView.findViewById(R.id.btn_story);
        btn_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.myContainer, storyFragment).commit();
            }
        });
        TextView btn_tag = (TextView) rootView.findViewById(R.id.btn_tag);
        btn_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.myContainer, tagFragment).commit();
            }
        });
        TextView btn_review = (TextView) rootView.findViewById(R.id.btn_review);
        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.myContainer, replyFragment).commit();
            }
        });
        TextView btn_bookmark = (TextView) rootView.findViewById(R.id.btn_bookmark);
        btn_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChildFragmentManager().beginTransaction().replace(R.id.myContainer, bookmarkFragment).commit();
            }
        });
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
                buzziDialog = new BaseDialog(getContext(), R.layout.buzzi_layout);
                buzziDialog.show();
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
}

