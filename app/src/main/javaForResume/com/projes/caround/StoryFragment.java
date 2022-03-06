package com.projes.caround;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class StoryFragment extends Fragment {

    protected Activity mActivity;
    protected Context mContext;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.8";
    String towsomeAddress = "192.168.0.12";

    int userId;

    TextView centerStoryMessage;
    TextView subStoryMessage;
    TextView subsubStoryMessage;
    ListView myStoryListView;

    JSONArray myPosts;
    //MyPostItem myPostItem;

    ArrayList<MyPostItem> myPostItemList;
    MyPostAdapter myPostAdapter;

    String good;
    String mention_count;
    TextView myNumber_of_like_post;
    TextView myNumber_of_mention_post;

    ListView mention_listView;
    ArrayList<MentionItem> mentionList;
    MentionsAdapter mentionsAdapter;

    MentionBaseDialog mentionDialog;
    Button mention_send;
    EditText mention_write;
    RelativeLayout mention_relativeLayout;

    TextView text_distance;
    EditText contentText;
    FrameLayout writebackground;
    Bitmap img;
    byte[] post_background;
    String profileImageBase64;
    ByteArrayOutputStream outputStream;
    Resources res;
    String temp;

    boolean backgroundChanged = false;

    BaseDialog myPostUpdateClick;

    private static final int LocationPermission_requestCode = 100;
    private static final int gallary_requestCode = 101;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mActivity = getActivity();
        this.mContext = context;
    }

    public static StoryFragment newInstance(){
        StoryFragment storyFragment=new StoryFragment();
        return storyFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_story, container, false);

        userId = mActivity.getIntent().getIntExtra("userId", 0);

        centerStoryMessage = (TextView)rootView.findViewById(R.id.centerStoryMessage);
        subStoryMessage = (TextView) rootView.findViewById(R.id.subStoryMessage);
        subsubStoryMessage = (TextView) rootView.findViewById(R.id.subsubStoryMessage);
        myStoryListView = (ListView) rootView.findViewById(R.id.myStoryListView);

        selectMyPosts(userId); // 내가 작성한 게시글 목록을 보여줌

        //내 게시글 목록에서 특정 게시글을 누르면 해당 게시글이 보여짐
        myStoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BaseDialog myPostDialog = new BaseDialog(mContext, R.layout.dialog_post);
                MyPostItem nowItem = (MyPostItem) myPostAdapter.getItem(i);
                FrameLayout myStoryPostBackground = (FrameLayout)myPostDialog.findViewById(R.id.myStoryPostBackground);
                Button btn_myStoryPostClose = (Button) myPostDialog.findViewById(R.id.btn_myStoryPostClose);
                TextView myPostContent = (TextView) myPostDialog.findViewById(R.id.myPostContent);
                TextView myText_distance_post = (TextView) myPostDialog.findViewById(R.id.myText_distance_post);
                TextView myDateTime_post = (TextView) myPostDialog.findViewById(R.id.myDateTime_post);
                myNumber_of_like_post = (TextView)myPostDialog.findViewById(R.id.myNumber_of_like_post);
                ImageView myBtn_mention = (ImageView)myPostDialog.findViewById(R.id.myBtn_mention);
                myNumber_of_mention_post = (TextView)myPostDialog.findViewById(R.id.myNumber_of_mention_post);

                final int postId = nowItem.getPosted();

                String[] times = nowItem.getMyPostDate().split(" ")[1].split(":");
                String time = times[0] + ":" + times[1];

                myPostContent.setText(nowItem.getMyPostContent());

                if(nowItem.getMyPostLocation() == "null"){
                    myText_distance_post.setText("SomeWhere");
                } else{
                    myText_distance_post.setText(nowItem.getMyPostLocation());
                }

                myDateTime_post.setText(time);
                myStoryPostBackground.setBackground(nowItem.getMyPostBackground());
                setGood(userId, postId); // 게시글 좋아요 상태, 좋아요 갯수, 댓글 갯수

                btn_myStoryPostClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myPostDialog.cancel();
                    }
                });

                myBtn_mention.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mentionWindow(postId);
                    }
                });

                myPostDialog.show();
            }
        });

        return rootView;
    }

    //내 게시글 목록 어댑터
    class MyPostAdapter extends BaseAdapter{
        ArrayList<MyPostItem> items = new ArrayList<MyPostItem>();
        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MyPostItem myPostItem){
            items.add(myPostItem);
        }

        public void addItems(ArrayList<MyPostItem> myPostItems){
            this.items = myPostItems;
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyPostItemView myPostItemView = new MyPostItemView(mContext);

            final MyPostItem myPostItem = items.get(i);

            String[] titles = myPostItem.getMyPostContent().split(" ");
            String title = titles[0];

            myPostItemView.setMyPost_title(title);

            String[] times = myPostItem.getMyPostDate().split(" ")[1].split(":");
            String time = times[0] + ":" + times[1];

            myPostItemView.setMyPost_time(time);
            ImageView menuButton = (ImageView)myPostItemView.findViewById(R.id.myPost_menu);
            // 각 게시글의 메뉴 버튼을 누르면 옵션이 나옴
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final BaseDialog myPostMenuClick = new BaseDialog(mContext, R.layout.dialog_post_menu_click);
                    TextView myPost_unCover = (TextView) myPostMenuClick.findViewById(R.id.myPost_unCover);
                    TextView myPost_update = (TextView) myPostMenuClick.findViewById(R.id.myPost_update);
                    TextView myPost_delete = (TextView) myPostMenuClick.findViewById(R.id.myPost_delete);
                    final MyPostItem nowItem = myPostItem;
                    final int postId = nowItem.getPosted();

                    //수정하기 버튼을 누르면 수정할 수 있는 다이얼로그가 나옴
                    myPost_update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "수정하기 클릭됨." + postId, Toast.LENGTH_SHORT).show();
                            myPostMenuClick.cancel();
                            myPostUpdateClick = new BaseDialog(mContext, R.layout.activity_write);
                            TextView btn_save;
                            ImageView btn_emotion;
                            ImageView btn_location;
                            ImageView btn_gallary;
                            ImageView btn_mic;
                            ImageView emotionImg;
                            TextView emotionText;
                            GridView gridgallary;
                            ListView listEmotion;
                            Animation translateUp;
                            Animation translateDown;

                            ImageView img_gallary_bym;
                            ImageView img_close;

                            btn_save = (TextView) myPostUpdateClick.findViewById(R.id.btn_save);
                            contentText = (EditText) myPostUpdateClick.findViewById(R.id.centent_text);

                            writebackground = myPostUpdateClick.findViewById(R.id.writebackground);
                            text_distance = (TextView) myPostUpdateClick.findViewById(R.id.text_distance);
                            img_gallary_bym = (ImageView) myPostUpdateClick.findViewById(R.id.img_gallary_btm);
                            img_close = (ImageView) myPostUpdateClick.findViewById(R.id.img_close);

                            translateUp = AnimationUtils.loadAnimation(mContext,R.anim.translate_up);
                            translateDown = AnimationUtils.loadAnimation(mContext,R.anim.translate_down);

                            final LocationManager lm = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                            final Geocoder geocoder = new Geocoder(mContext);

                            btn_location = (ImageView) myPostUpdateClick.findViewById(R.id.img_location);
                            btn_location.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                                        ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LocationPermission_requestCode);
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

                                    int gallaryReadPermissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                                    int gallaryWritePermissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                    if(gallaryReadPermissionCheck == PackageManager.PERMISSION_GRANTED){

                                    } else {
                                        ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 102);
                                    }

                                    if(gallaryWritePermissionCheck == PackageManager.PERMISSION_GRANTED){

                                    } else {
                                        ActivityCompat.requestPermissions(mActivity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);
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
                            writebackground.setBackground(nowItem.getMyPostBackground());
                            //img = BitmapFactory.decodeResource(res, nowItem.getMyPostBackground());
                            contentText.setText(nowItem.getMyPostContent());

                            String location = nowItem.getMyPostLocation();

                            if(location == "null"){
                                location = "○○km 이내";
                            }
                            text_distance.setText(location);

                            btn_save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String content = contentText.getText().toString();
                                    UpdateMyPost updateMyPost = new UpdateMyPost();
                                    if(content != null){
                                        String location = text_distance.getText().toString();
                                        //String emot = emotionText.getText().toString();
                                        temp = "";
                                        if(backgroundChanged){
                                            img.compress(Bitmap.CompressFormat.JPEG, 69, outputStream);
                                            post_background = outputStream.toByteArray();
                                            profileImageBase64 = Base64.encodeToString(post_background, Base64.DEFAULT);
                                            try{
                                                temp = URLEncoder.encode(profileImageBase64, "utf-8");
                                            } catch(Exception e){
                                                e.printStackTrace();
                                            }//MiddleWare나 서버를 통해서 데이터 베이스에 넣으려면 한번 더 utf-8로 인코딩해주어야 한다.

                                        }
                                        updateMyPost.execute(content, temp, location, String.valueOf(userId), String.valueOf(postId));
                                    } else{
                                        Toast.makeText(mContext, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                            img_close.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    myPostUpdateClick.cancel();
                                }
                            });

                            myPostUpdateClick.show();
                        }
                    });

                    myPost_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(mContext, "삭제하기 클릭됨." + postId, Toast.LENGTH_SHORT).show();
                        }
                    });

                    myPostMenuClick.show();
                }
            });
            return myPostItemView;
        }
    }

    //데이터베이스로부터 받아온 내 게시글들의 값으로 아이템 생성후 리스트에 넣어서 어댑터로 보내줌
    protected void showMyPost(String mJson){
        try{
            myPostItemList = new ArrayList<MyPostItem>();
            JSONObject jsonObj = new JSONObject(mJson);
            myPosts = jsonObj.getJSONArray("result");

            if(myPosts.length() != 0) {
                centerStoryMessage.setVisibility(View.GONE);
                subStoryMessage.setVisibility(View.GONE);
                subsubStoryMessage.setVisibility(View.GONE);
            }

            for(int i=0; i<myPosts.length(); i++){
                JSONObject post = myPosts.getJSONObject(i);
                String content = post.getString("content");
                String location = post.getString("location");
                String pdate = post.getString("pdate");
                //String good = String.valueOf(post.getInt("good"));
                //String mention = String.valueOf(post.getInt("mention"));
                String background = post.getString("background");
                byte[] bytePlainOrg = Base64.decode(background, Base64.DEFAULT);
                int postId = Integer.parseInt(post.getString("postId"));

                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytePlainOrg);
                Bitmap img = BitmapFactory.decodeStream(inputStream);

                Resources res = mContext.getResources();

                Drawable backImg = new BitmapDrawable(res, img);

                MyPostItem myPostItem = new MyPostItem(content, pdate, location, backImg, postId);
                myPostItemList.add(myPostItem);
            }

            myPostAdapter = new MyPostAdapter();
            myPostAdapter.addItems(myPostItemList);
            myStoryListView.setAdapter(myPostAdapter);

        } catch(Exception e){
            e.printStackTrace();
        }
    }

    //데이터베이스로부터 내 게시글들을 받아옴
    public void selectMyPosts(int userId){
        class SelectMyPost extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                //String serverURL = "http://" + ipAddress + "/caround/selectMyPost.php";
                String serverURL = "http://" + towsomeAddress + "/caround/selectMyPost.php";

                int userId = Integer.parseInt(params[0]);

                String postParameters = "userId=" + userId;

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
                //Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                String mJson = s;
                showMyPost(mJson);
            }
        }
        SelectMyPost selectMyPost = new SelectMyPost();
        selectMyPost.execute(String.valueOf(userId));
    }

    public void setGood(int userId, int postId){
        class SelectGood extends AsyncTask<Integer, Void, String>{
            @Override
            protected String doInBackground(Integer... params) {
                //String serverURL = "http://" + ipAddress + "/caround/selectGood.php";
                String serverURL = "http://" + towsomeAddress + "/caround/selectGood.php";

                int userId = params[0];
                int postId = params[1];

                String postParameters = "userId=" + userId + "&postId=" + postId;

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
                try{
                    JSONObject jsonObj = new JSONObject(s);

                    good = String.valueOf(jsonObj.getInt("goodCount"));
                    mention_count = String.valueOf(jsonObj.getInt("mentionCount"));
                    myNumber_of_like_post.setText(good);
                    myNumber_of_mention_post.setText(mention_count);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        SelectGood selectGood = new SelectGood();
        selectGood.execute(userId, postId);
    }

    class MentionsAdapter extends BaseAdapter{
        ArrayList<MentionItem> items = new ArrayList<MentionItem>();

        @Override
        public int getCount() {
            return items.size();
        }

        public void addItem(MentionItem mentionItem){
            items.add(mentionItem);
        }

        public void addItems(ArrayList<MentionItem> mentionItems){
            this.items = mentionItems;
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MentionItemView mentionItemView = new MentionItemView(mContext);

            MentionItem item = items.get(i);
            mentionItemView.setUser_mention(item.getmContent());
            mentionItemView.setMention_time(item.getMdate());
            mentionItemView.setUser_image(item.getUser_image());
            return mentionItemView;
        }
    }

    private void mentionWindow(final int postId){
        mentionDialog = new MentionBaseDialog(mContext, R.layout.dialog_mention);
        mention_send = (Button)mentionDialog.findViewById(R.id.mention_send);
        mention_write = (EditText)mentionDialog.findViewById(R.id.mention_write);
        mention_listView = (ListView)mentionDialog.findViewById(R.id.mention_listView);
        mention_relativeLayout = (RelativeLayout)mentionDialog.findViewById(R.id.mention_relativeLayout);
        setMention(postId);
        mention_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mention_text = mention_write.getText().toString();
                if(mention_text != ""){
                    plusMention(mention_text, userId, postId);
                } else{
                    Toast.makeText(mContext, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mention_relativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                float Y = motionEvent.getY();

                Window window = mentionDialog.getmWindow();
                if(window != null){
                    WindowManager.LayoutParams params = mentionDialog.getmParams();
                    if(action == MotionEvent.ACTION_MOVE || params.height != WindowManager.LayoutParams.MATCH_PARENT){
                        //float newY = motionEvent.getY();
                        //Log.d("Attr Y","Y : " + curY + " & " + newY);
                        //int totalY = (int)(newY - curY);
                        mentionDialog.setParamsHeight((int)Y);
                        return true;
                    } else{
                        return false;
                    }
                }
                return false;
            }
        });

        mention_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BaseDialog mentionLongClick = new BaseDialog(mContext, R.layout.dialog_mentioned_long_click);
                TextView mention_update = (TextView) mentionLongClick.findViewById(R.id.mention_update);
                TextView mention_delete = (TextView) mentionLongClick.findViewById(R.id.mention_delete);
                MentionItem nowItem = (MentionItem) mentionsAdapter.getItem(i);
                final String pre_mention = nowItem.getmContent();
                final String mentioned_date = nowItem.getMdate();

                mention_update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext, "수정하기 클릭됨", Toast.LENGTH_SHORT).show();
                        mentionLongClick.cancel();
                        View dialogView = (View)View.inflate(mContext, R.layout.dialog_mention_update, null);
                        final EditText mention_sentence = (EditText) dialogView.findViewById(R.id.mention_sentence);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dialogView);
                        mention_sentence.setText(pre_mention);
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updateMention(mention_sentence.getText().toString(), userId, postId, mentioned_date);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });

                mention_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(mContext, "삭제하기 클릭됨", Toast.LENGTH_SHORT).show();
                        mentionLongClick.cancel();
                        View dialogView = (View)View.inflate(mContext, R.layout.dialog_mention_delete, null);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setView(dialogView);
                        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteMention(userId, postId, mentioned_date);
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                });

                mentionLongClick.show();
                return false;
            }
        });

        mentionDialog.show();
    }

    protected void ShowMention(String mJson, int postId){
        try {
            mentionList = new ArrayList<MentionItem>();
            JSONObject jsonObj = new JSONObject(mJson);
            JSONArray mentions = jsonObj.getJSONArray("result");

            for(int i=0; i<mentions.length(); i++){
                JSONObject mention = mentions.getJSONObject(i);
                String content = mention.getString("mention");
                String date = mention.getString("mdate");

                MentionItem mentionItem = new MentionItem(content, date, R.drawable.ic_12, postId);
                mentionList.add(mentionItem);
            }

            mentionsAdapter = new MentionsAdapter();
            mentionsAdapter.addItems(mentionList);
            mention_listView.setAdapter(mentionsAdapter);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMention(final int postId){
        class SelectMention extends AsyncTask<Integer, Void, String>{
            @Override
            protected String doInBackground(Integer... params) {
                //String serverURL = "http://" + ipAddress + "/caround/selectMention.php";
                String serverURL = "http://" + towsomeAddress + "/caround/selectMention.php";

                int postId = params[0];

                String postParameters = "postId=" + postId;

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
                String mJson = s;
                ShowMention(mJson, postId);

            }
        }
        SelectMention selectMention = new SelectMention();
        selectMention.execute(postId);
    }

    public void plusMention(final String mention, final int userId, final int postId){
        class InsertMention extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                //String serverURL = "http://" + ipAddress + "/caround/insertMention.php";
                String serverURL = "http://" + towsomeAddress + "/caround/insertMention.php";

                String mention = params[0];
                int userId = Integer.parseInt(params[1]);
                int postId = Integer.parseInt(params[2]);

                String postParameters = "mention=" + mention + "&userId=" + userId + "&postId=" + postId;

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
                Toast.makeText(mContext, "댓글이 추가되었습니다.", Toast.LENGTH_SHORT).show();
                mention_write.setText("");
                try{
                    //JSONObject jsonObj = new JSONObject(s);

                    //String content = jsonObj.getString("mention");
                    //String date = jsonObj.getString("mdate");
                    String content = mention;

                    MentionItem mentionItem = new MentionItem(content, "방금전", R.drawable.ic_12, postId);
                    mentionList.add(mentionItem);
                    mentionsAdapter.notifyDataSetChanged();
                    setGood(userId, postId);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        InsertMention insertMention = new InsertMention();
        insertMention.execute(mention, String.valueOf(userId), String.valueOf(postId));
    }

    public void updateMention(final String mention, final int userId, final int postId, final String mentioned_date){
        class UpdateMention extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                //String serverURL = "http://" + ipAddress + "/caround/updateMention.php";
                String serverURL = "http://" + towsomeAddress + "/caround/updateMention.php";

                String mention = params[0];
                int userId = Integer.parseInt(params[1]);
                int postId = Integer.parseInt(params[2]);
                String mentioned_date = params[3];

                String postParameters = "mention=" + mention + "&userId=" + userId + "&postId=" + postId + "&mentioned_date=" + mentioned_date;

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
                Toast.makeText(mContext, "댓글이 수정되었습니다.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                try{
                    //setGood(userId, postId);
                    setMention(postId);
                    setGood(userId, postId);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        UpdateMention updateMention = new UpdateMention();
        updateMention.execute(mention, String.valueOf(userId), String.valueOf(postId), mentioned_date);
    }

    public void deleteMention(final int userId, final int postId, final String mentioned_date){
        class DeleteMention extends AsyncTask<String, Void, String>{
            @Override
            protected String doInBackground(String... params) {
                //String serverURL = "http://" + ipAddress + "/caround/deleteMention.php";
                String serverURL = "http://" + towsomeAddress + "/caround/deleteMention.php";

                //String mention = params[0];
                int userId = Integer.parseInt(params[0]);
                int postId = Integer.parseInt(params[1]);
                String mentioned_date = params[2];

                String postParameters = "userId=" + userId + "&postId=" + postId + "&mentioned_date=" + mentioned_date;

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
                Toast.makeText(mContext, "댓글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                //Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                try{
                    //setGood(userId, postId);
                    setMention(postId);
                    setGood(userId, postId);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        DeleteMention deleteMention = new DeleteMention();
        deleteMention.execute(String.valueOf(userId), String.valueOf(postId), mentioned_date);
    }

    public void permissionCheck(){ //checkSelfPermission() 함수로 퍼미션의 상태를 확인, requestPermission()은 권한 대화상자 띄우기
        if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(mContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mActivity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            case gallary_requestCode:
                if(resultCode == mActivity.RESULT_OK){
                    try{
                        InputStream in = mActivity.getContentResolver().openInputStream(data.getData());

                        img = BitmapFactory.decodeStream(in);
                        Drawable ob = new BitmapDrawable(getResources(), img);

                        in.close();

                        writebackground.setBackground(ob);
                        backgroundChanged = true;
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(resultCode == mActivity.RESULT_CANCELED){
                    Toast.makeText(mContext, "사진 선택 취소", Toast.LENGTH_SHORT).show();
                }
        }
    }

    //내 게시글 옵션에서 수정하기 클릭하고 값을 변경후 저장 버튼을 누르면 데이터베이스에 반영
    class UpdateMyPost extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            //String serverURL = "http://" + ipAddress + "/caround/updateMyPost.php";
            String serverURL = "http://" + towsomeAddress + "/caround/updateMyPost.php";

            String content = (String)params[0];
            String background = (String)params[1];
            String location = (String)params[2];
            int userId = Integer.parseInt(params[3]);
            int postId = Integer.parseInt(params[4]);
            //String emot = (String)params[5];

            //String postParameters = "content=" + content + "&background=" + background + "&location=" + location + "&userId=" + userId;
            String postParameters = "content=" + content + "&background=" + background + "&location=" + location + "&userId=" + userId + "&postId=" + postId;

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
            Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
            selectMyPosts(userId);
            myPostUpdateClick.cancel();
        }
    }
}
