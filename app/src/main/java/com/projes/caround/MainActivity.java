package com.projes.caround;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements FragmentCall {
    BottomNavigationView bottomNavigationView;
    WriteFragment frag1;
    NotiFragment frag2;
    MyFragment frag3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment1 fragment1 = new Fragment1();
        getSupportFragmentManager().beginTransaction().add(R.id.Main_Frame, fragment1).addToBackStack(null).commit();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.buttonBack:
                        onFragmentSelected(0);
                        return true;
                    case R.id.buttonWrite:
                        Intent intent = new Intent(getApplicationContext(), WriteActivity.class);
                        startActivity(intent); //작성 액티비티 호출로 변경
                        return true;
                    case R.id.buttonNotice:
                        onFragmentSelected(2);
                        return true;
                    case R.id.buttonMy:
                        onFragmentSelected(3);
                        return true;
                }
                return false;
            }
        }); //하단 네비게이션 아이템 선택시 프래그먼트 이동 메소드 호출

        frag1 = new WriteFragment();
        frag2 = new NotiFragment();
        frag3 = new MyFragment();
    }

    @Override
    public void onFragmentSelected(int position) {
        Fragment currentFragment = null;

        if(position == 2){
            currentFragment = frag2;
        } else if(position == 3){
            currentFragment = frag3;
        } else if(position == 0){
            onBackPressed();
        }

        if(position != 0 && position != 1){
            getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, currentFragment).addToBackStack(null).commit();
        }
    }//하단 네비게이션 클릭시 호출되어 프래그먼트 변경

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}