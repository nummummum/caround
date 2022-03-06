package com.projes.caround;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {//프래그먼트 교체를 보여주는곳을 구현한 함수
        switch(position)
        {
            case 0:
                return StoryFragment.newInstance();
            case 1:
                return ReplyFragment.newInstacne();
            case 2:
                return BookmarkFragment.newInstacne();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {// 갯수 명시
        return 3;
    }

    //상단의 탭 레이아웃 인디케이터쪽에 텍스트를 선언해주는곳
    //텍스트를 레이아웃에서 바꿔줘도 어뎁터쪽에서 다시한번 지정해줘야함

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position)
        {
            case 0:
                return "이야기";
            case 1:
                return "내 댓글";
            case 2:
                return "북마크";
            default:
                return null;
        }
    }
}
