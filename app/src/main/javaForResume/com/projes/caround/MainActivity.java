package com.projes.caround;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentCall {
    BottomNavigationView bottomNavigationView;
    WriteFragment frag1;
    NotiFragment frag2;
    MyFragment frag3;
    Fragment1 fragment1;
    int userId;
    TextView selectTestText;
    ProgressBar progressBar;
    private long backBtnTime = 0;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.8";
    String towsomeAddress = "192.168.0.12";

    String mJson;
    JSONArray posts;
    ArrayList<PostFragment> contents;
    PostFragment postFragment;
    FrameLayout mainFrame;
    int pre_limit;
    //int post_limit;
    int limit;
    int count;
    PostFragment showed;
    boolean moreFlag;

    int start;
    boolean anotherFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);

        selectTestText = (TextView) findViewById(R.id.selectTestText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mainFrame = (FrameLayout) findViewById(R.id.Main_Frame);

        fragment1 = new Fragment1();
        //getSupportFragmentManager().beginTransaction().add(R.id.Main_Frame, fragment1).commit();

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
                        intent.putExtra("userId", userId);
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

        contents = new ArrayList<PostFragment>(); //띄워줄 게시글 배열

        pre_limit = 5; //처음에 받아올 게시글 수
        //post_limit = 5;
        limit = 5; //처음에 받아올 게시글 수
        count = 0; //현재 게시글 위치
        moreFlag = true; //더 받아올 게시글이 있는지 상태값
        anotherFragment = false; //다른 프래그먼트화면에 있는지

        SelectPost selectPost = new SelectPost(); //맨 처음에 게시글 받아옴
        //selectPost.execute("http://" + ipAddress + "/caround/selectPost.php", String.valueOf(userId), String.valueOf(limit));
        selectPost.execute("http://" + towsomeAddress + "/caround/selectPost.php", String.valueOf(userId), String.valueOf(limit));

        mainFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //게시글 화면을 클릭할 때마다 작동
                if(moreFlag){ // 더 받아올 게시글이 있으면
                    pre_limit++; //현재까지 받아온 갯수 1 증가
                    //post_limit++;
                    count++; // 다음 화면으로 넘어감
                    start++; // 현재 위치 저장 (다른 프래그먼트에 있다가 다시 돌아올때 사용)
                    showed = contents.get(count); //현재 게시글을 받아옴
                    getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, showed).commit(); // 화면으로 보여줌

                    AfterSelectPost afterSelectPost = new AfterSelectPost(); // 게시글 1개 더 받아옴
                    //afterSelectPost.execute("http://" + ipAddress + "/caround/selectPostByClick.php", String.valueOf(userId), String.valueOf(pre_limit), String.valueOf(start));
                    afterSelectPost.execute("http://" + towsomeAddress + "/caround/selectPostByClick.php", String.valueOf(userId), String.valueOf(pre_limit), String.valueOf(start));
                } else{ // 더 받아올 게시글이 없으면
                    count++; //다음화면으로만 넘어감
                    if(count < pre_limit - 1){ //현재 위치가 마지막 게시글 위치가 아니면
                        showed = contents.get(count); // 다음 화면으로 이동
                        getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, showed).commit();
                    } else { // 현재 위치가 마지막 게시글 위치면
                        getSupportFragmentManager().beginTransaction().remove(showed).commit();
                        selectTestText.setVisibility(View.VISIBLE);
                        selectTestText.setText("새 소식이 없습니다."); // 미리 작성된 문구 출력
                    }

                }

            }
        });

    }

    @Override
    public void onFragmentSelected(int position) {
        Fragment currentFragment = null;

        if(position == 2){
            currentFragment = frag2;
            anotherFragment = true;
        } else if(position == 3){
            currentFragment = frag3;
            anotherFragment = true;
        } else if(position == 0){
            if(anotherFragment){
                currentFragment = showed;
                anotherFragment = false;
            } else{
                if(count != 0){
                    count--;
                    showed = contents.get(count);
                    currentFragment = showed;
                }
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.Main_Frame, currentFragment).commit();

    }//하단 네비게이션 클릭시 호출되어 프래그먼트 변경

    @Override
    public void onBackPressed() {

        long curTime = System.currentTimeMillis();
        long gapTime = curTime - backBtnTime;

        if(gapTime >= 0 && gapTime <= 2000){
            super.onBackPressed();
        } else{
            backBtnTime = curTime;
            Toast.makeText(this, "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //데이터베이스로부터 받아온 게시글 값을 프래그먼트에게 전달해줌
    protected void ShowPosts(Boolean first){
        if(first){ //맨 처음에는 5개를 먼저 데이터베이스로부터 받아옴
            try{
                selectTestText.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                JSONObject jsonObj = new JSONObject(mJson);
                posts = jsonObj.getJSONArray("result");

                if(posts.length() < pre_limit){
                    moreFlag = false;
                    pre_limit = posts.length();
                }

                if(posts.length() != 0){
                    for(int i=0; i<posts.length(); i++){
                        JSONObject post = posts.getJSONObject(i);
                        String content = post.getString("content");
                        String location = post.getString("location");
                        String pdate = post.getString("pdate");
                        //String good = String.valueOf(post.getInt("good"));
                        String mention = String.valueOf(post.getInt("mention"));
                        String background = post.getString("background");
                        byte[] bytePlainOrg = Base64.decode(background, Base64.DEFAULT);
                        int postId = Integer.parseInt(post.getString("postId"));

                        postFragment = new PostFragment(); //게시글 프래그먼트 생성
                        Bundle bundle = new Bundle();
                        bundle.putString("content", content);
                        bundle.putString("location", location);
                        bundle.putString("pdate", pdate);
                        //bundle.putString("good", good);
                        bundle.putString("mention", mention);
                        bundle.putByteArray("background", bytePlainOrg);
                        bundle.putInt("postId", postId);

                        postFragment.setArguments(bundle);
                        contents.add(postFragment); //프래그먼트 배열에 추가

                        if(i == posts.length() - 1){
                            start = Integer.parseInt(post.getString("postId"));
                        }

                    }

                    showed = contents.get(count); //현재 위치에 해당하는 게시글을 받음
                    getSupportFragmentManager().beginTransaction().add(R.id.Main_Frame, showed).commit(); // 해당 화면으로 이동
                } else{
                    selectTestText.setVisibility(View.VISIBLE);
                    selectTestText.setText("새 소식이 없습니다.");
                }

            } catch(Exception e){
                e.printStackTrace();
            }
        } else{ //화면을 클릭할때마다 게시글 1개씩 받아옴
            try{
                JSONObject jsonObj = new JSONObject(mJson);

                String content = jsonObj.getString("content");
                String location = jsonObj.getString("location");
                String pdate = jsonObj.getString("pdate");
                //String good = String.valueOf(jsonObj.getInt("good"));
                String mention = String.valueOf(jsonObj.getInt("mention"));
                String background = jsonObj.getString("background");
                byte[] bytePlainOrg = Base64.decode(background, Base64.DEFAULT);
                int postId = Integer.parseInt(jsonObj.getString("postId"));

                postFragment = new PostFragment();
                Bundle bundle = new Bundle();
                bundle.putString("content", content);
                bundle.putString("location", location);
                bundle.putString("pdate", pdate);
                //bundle.putString("good", good);
                bundle.putString("mention", mention);
                bundle.putByteArray("background", bytePlainOrg);
                bundle.putInt("postId", postId);

                postFragment.setArguments(bundle);
                contents.add(postFragment);

                start = Integer.parseInt(jsonObj.getString("postId"));

            } catch(Exception e){
                e.printStackTrace();
                moreFlag = false;
            }
        }

    }

    //게시글을 데이터베이스로부터 받아옴
    class SelectPost extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];

            int userId = Integer.parseInt(params[1]);
            //int pre_limit = Integer.parseInt(params[2]);
            //int post_limit = Integer.parseInt(params[3]);
            int limit = Integer.parseInt(params[2]);

            String postParameters = "userId=" + userId + "&limit=" + limit;

            try{
                URL url = new URL(serverURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다
                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다
                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                //전송할 데이터가 저장된 변수를 이곳에 입력합니다. 인코딩을 고려해줘야 합니다.
                outputStream.flush();
                outputStream.close();

                // 3. 응답을 읽습니다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("PHP RESOPNSE", "POST responseCode: " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                    // 정상적인 응답 데이터
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                // 4. StringBuilder를 사용하여 수신되는 데이터를 저장합니다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!= null){
                    sb.append(line);
                }

                bufferedReader.close();

                // 5. 저장된 데이터를 스트링으로 변환하여 리턴합니다.
                return sb.toString();

            } catch (Exception e){
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mJson = s;
            ShowPosts(true);
        }
    }

    //화면을 클릭할때마다 데이터베이스로부터 게시글 1개씩 받아옴
    class AfterSelectPost extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];

            int userId = Integer.parseInt(params[1]);
            int pre_limit = Integer.parseInt(params[2]);
            //int post_limit = Integer.parseInt(params[3]);

            //String postParameters = "userId=" + userId + "&pre_limit=" + pre_limit + "&post_limit=" + post_limit;

            int start = Integer.parseInt(params[3]);

            //String postParameters = "userId=" + userId + "&preLimit=" + pre_limit;
            String postParameters = "userId=" + userId + "&preLimit=" + pre_limit + "&start=" + start;

            try{
                URL url = new URL(serverURL);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다
                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다
                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                //전송할 데이터가 저장된 변수를 이곳에 입력합니다. 인코딩을 고려해줘야 합니다.
                outputStream.flush();
                outputStream.close();

                // 3. 응답을 읽습니다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("PHP RESOPNSE", "POST responseCode: " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                    // 정상적인 응답 데이터
                } else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                // 4. StringBuilder를 사용하여 수신되는 데이터를 저장합니다.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!= null){
                    sb.append(line);
                }

                bufferedReader.close();

                // 5. 저장된 데이터를 스트링으로 변환하여 리턴합니다.
                return sb.toString();

            } catch (Exception e){
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mJson = s;
            ShowPosts(false);
        }
    }
}