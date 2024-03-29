package com.projes.caround;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WriteActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView btn_cancle;
    TextView btn_save;
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
    FrameLayout writebackground;
    TextView text_distance;
    ImageView img_gallary_bym;
    protected Activity mActivity;
    private static final int LocationPermission_requestCode = 100;
    String[] REQUIRED_PERMISSION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private static final int gallary_requestCode = 101;

    int userId;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.34";
    String towsomeAddress = "192.168.0.12";

    Bitmap img;
    byte[] post_background;
    String profileImageBase64;
    ByteArrayOutputStream outputStream;
    Resources res;
    String temp;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_write);

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 0);

        btn_save = (TextView) findViewById(R.id.btn_save);
        contentText = (EditText) findViewById(R.id.centent_text);

        btn_gallary = findViewById(R.id.img_gallary);
        btn_gallary.setOnClickListener(this);
        btn_emotion = findViewById(R.id.img_emotion);
        btn_emotion.setOnClickListener(this);
        btn_mic = findViewById(R.id.img_mic_btm);
        btn_mic.setOnClickListener(this);

        emotionImg = findViewById(R.id.emotion_image);
        emotionText = findViewById(R.id.emotion_text);
        gridgallary = findViewById(R.id.grid_gallary);
        listEmotion = findViewById(R.id.list_emotion);
        contentText = findViewById(R.id.centent_text);
        writebackground = findViewById(R.id.writebackground);
        text_distance = (TextView) findViewById(R.id.text_distance);
        img_gallary_bym = (ImageView) findViewById(R.id.img_gallary_btm);

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

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final Geocoder geocoder = new Geocoder(this);

        btn_location = (ImageView) findViewById(R.id.img_location);
        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(WriteActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LocationPermission_requestCode);
                } else{
                    List<Address> Addr = null;
                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //String provider = location.getProvider();
                    Double latitude = location.getLatitude();
                    Double longitude = location.getLongitude();

                    //text_distance.setText("위치정보: " + provider + " 경도: " + latitude + " 고도: " + longitude);
                    try{
                        Addr = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (Exception e){
                        text_distance.setText("지오코더 오류");
                    }

                    if(Addr != null){
                        if(Addr.size() == 0){
                            text_distance.setText("주소 미발견");
                        } else{
                            String ada = (String) Addr.get(0).getAdminArea();
                            text_distance.setText(ada);
                            //contentText.setText(Addr.get(0).toString());
                        }
                    }
                }
            }
        });

        img_gallary_bym.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int gallaryReadPermissionCheck = ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                int gallaryWritePermissionCheck = ContextCompat.checkSelfPermission(WriteActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(gallaryReadPermissionCheck == PackageManager.PERMISSION_GRANTED){

                } else {
                    ActivityCompat.requestPermissions(WriteActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                }

                if(gallaryWritePermissionCheck == PackageManager.PERMISSION_GRANTED){

                } else {
                    ActivityCompat.requestPermissions(WriteActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);
                }

                if(gallaryReadPermissionCheck == PackageManager.PERMISSION_GRANTED && gallaryWritePermissionCheck == PackageManager.PERMISSION_GRANTED){
                    Intent gallaryIntent = new Intent(Intent.ACTION_PICK);
                    gallaryIntent.setType("image/*");
                    //gallaryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(gallaryIntent, gallary_requestCode);
                }
            }
        });

        outputStream = new ByteArrayOutputStream();
        res = getResources();
        writebackground.setBackgroundResource(R.drawable.main_background);
        img = BitmapFactory.decodeResource(res, R.drawable.main_background);
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 69, outputStream);
//        post_background = outputStream.toByteArray();
//        profileImageBase64 = Base64.encodeToString(post_background, Base64.DEFAULT);
//        temp = "";

//        try{
//            temp = "&background=" + URLEncoder.encode(profileImageBase64, "utf-8");
//        } catch(Exception e){
//            e.printStackTrace();
//        }//MiddleWare나 서버를 통해서 데이터 베이스에 넣으려면 한번 더 utf-8로 인코딩해주어야 한다.

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = contentText.getText().toString();
                if(content != null){
                    String location = text_distance.getText().toString();
                    //String emot = emotionText.getText().toString();
                    img.compress(Bitmap.CompressFormat.JPEG, 69, outputStream);
                    post_background = outputStream.toByteArray();
                    profileImageBase64 = Base64.encodeToString(post_background, Base64.DEFAULT);
                    temp = "";
                    try{
                        temp = "&background=" + URLEncoder.encode(profileImageBase64, "utf-8");
                    } catch(Exception e){
                        e.printStackTrace();
                    }//MiddleWare나 서버를 통해서 데이터 베이스에 넣으려면 한번 더 utf-8로 인코딩해주어야 한다.

                    InsertPost insertPost = new InsertPost();
                    //insertPost.execute("http://" + ipAddress + "/caround/insertPost.php", content, temp, location, String.valueOf(userId));
                    insertPost.execute("http://" + towsomeAddress + "/caround/insertPost.php", content, temp, location, String.valueOf(userId));
                } else{
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onClick(View view) {

        if(view==btn_gallary && gridgallary.getVisibility()==View.VISIBLE)
        {
            gridgallary.setVisibility(View.INVISIBLE);
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
                    gridgallary.startAnimation(translateUp);
                }
            },200);
        }
        else if(view==btn_emotion && listEmotion.getVisibility()==View.VISIBLE)
        {
            listEmotion.setVisibility(View.INVISIBLE);
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
                    listEmotion.startAnimation(translateUp);
                }
            },200);
        }
        else if(view==btn_mic){
            permissionCheck();
            MicDialog micDialog = new MicDialog(mActivity,WriteActivity.this);
            micDialog.callFunction();
        }
    }
    public void permissionCheck(){ //checkSelfPermission() 함수로 퍼미션의 상태를 확인, requestPermission()은 권한 대화상자 띄우기
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(WriteActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){

            case gallary_requestCode:
                if(resultCode == RESULT_OK){
                    try{
                        InputStream in = getContentResolver().openInputStream(data.getData());

                        img = BitmapFactory.decodeStream(in);
                        Drawable ob = new BitmapDrawable(getResources(), img);


//                        img.compress(Bitmap.CompressFormat.JPEG, 69, outputStream);
//                        post_background = outputStream.toByteArray();
//                        profileImageBase64 = Base64.encodeToString(post_background, Base64.DEFAULT);
//                        try{
//                            temp = "&background=" + URLEncoder.encode(profileImageBase64, "utf-8");
//                        } catch(Exception e){
//                            e.printStackTrace();
//                        } //MiddleWare나 서버를 통해서 데이터 베이스에 넣으려면 한번 더 utf-8로 인코딩해주어야 한다.

                        in.close();

                        //writebackground.setBackgroundResource(ob);
                        //writebackground.setBackgroundDrawable(ob);
                        writebackground.setBackground(ob);
                    } catch(Exception e){

                    }

                    /*Uri gallaryURI = data.getData();
                    Cursor cursor = null;

                    try{
                        String[] proj = {MediaStore.Images.Media.DATA};

                        assert gallaryURI != null;
                        cursor = getContentResolver().query(gallaryURI, proj, null, null, null);

                        assert cursor != null;
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        cursor.moveToFirst();

                        //File tempFile = new File(cursor.getShort(column_index))
                    } finally {
                        if(cursor != null){
                            cursor.close();
                        }
                    }*/
                }
                else if(resultCode == RESULT_CANCELED){
                    Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_SHORT).show();
                }
        }
    }

    class InsertPost extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];

            String content = (String)params[1];
            String background = (String)params[2];
            String location = (String)params[3];
            int userId = Integer.parseInt(params[4]);
            //String emot = (String)params[5];

            //String postParameters = "content=" + content + "&background=" + background + "&location=" + location + "&userId=" + userId;
            String postParameters = "content=" + content + background + "&location=" + location + "&userId=" + userId;

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
            finish();
        }
    }
}
