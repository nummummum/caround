package com.projes.caround;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements FragmentCall{
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
                    case R.id.buttonWrite:
                        onFragmentSelected(1);
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
        });

        frag1 = new WriteFragment();
        frag2 = new NotiFragment();
        frag3 = new MyFragment();
    }

    @Override
    public void onFragmentSelected(int position) {
        Fragment currentFragment = null;

        if(position == 1){
            currentFragment = frag1;
        } else if(position == 2){
            currentFragment = frag2;
        } else if(position == 3){
            currentFragment = frag3;
        } else if(position == 0){
            onBackPressed();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, currentFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}