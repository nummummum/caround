package com.projes.caround;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class PostFragment extends Fragment {

    //private Context mContext;
    //Resources res;
    protected Activity mactivity;
    protected Context mContext;

    int postId;
    int userId;

    boolean btn_like_selected;

    //TextView postContent;
    String good;
    String mention_count;
    ImageView btn_like;
    TextView number_of_like_post;
    TextView number_of_mention_post;
    MentionBaseDialog mentionDialog;
    Button mention_send;
    EditText mention_write;
    ListView mention_listView;
    RelativeLayout mention_relativeLayout;

    ArrayList<MentionItem> mentionList;
    MentionsAdapter mentionsAdapter;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.8";
    String towsomeAddress = "192.168.0.12";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //mContext = context;
        //res = getActivity().getResources();
        this.mactivity = getActivity();
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_post, container, false);
        FrameLayout postBackground = rootView.findViewById(R.id.postBackground);
        TextView postContent = rootView.findViewById(R.id.postContent);
        //postContent = rootView.findViewById(R.id.postContent);
        TextView text_distance_post = rootView.findViewById(R.id.text_distance_post);
        TextView dateTime_post = rootView.findViewById(R.id.dateTime_post);
        //final TextView number_of_like_post = rootView.findViewById(R.id.number_of_like_post);
        number_of_like_post = rootView.findViewById(R.id.number_of_like_post);
        //TextView number_of_mention_post = rootView.findViewById(R.id.number_of_mention_post);
        number_of_mention_post = rootView.findViewById(R.id.number_of_mention_post);
        //final ImageView btn_like = rootView.findViewById(R.id.btn_like);
        btn_like = rootView.findViewById(R.id.btn_like);
        ImageView btn_mention = rootView.findViewById(R.id.btn_mention);

        String[] dateTime;
        String time;

        Bundle bundle = getArguments();

        String content = bundle.getString("content");
        String location = bundle.getString("location");
        String pdate = bundle.getString("pdate");
        dateTime = pdate.split(" ");
        time = dateTime[1];
        dateTime = time.split(":");
        time = dateTime[0] + ":" + dateTime[1];
        //final String good = bundle.getString("good");
        //good = bundle.getString("good");
        //String mention = bundle.getString("mention");
        byte[] background = bundle.getByteArray("background");
        //final int postId = bundle.getInt("postId");
        postId = bundle.getInt("postId");
        //final int userId = mactivity.getIntent().getIntExtra("userId", 0);
        userId = mactivity.getIntent().getIntExtra("userId", 0);
        //메인액티비티로부터 게시글 값을 전달받음

//        SelectGood selectGood = new SelectGood();
//        selectGood.execute(userId, postId);
        setGood(userId, postId); //좋아요와 댓글 갯수 셋팅해줌


        if(location == "null"){
            location = "SomeWhere";
        }

        postContent.setText(content);
        text_distance_post.setText(location);
        dateTime_post.setText(time);
        //number_of_like_post.setText(good);
        //number_of_mention_post.setText(mention);

        String txtPlainOrg = "";
        //byte[] bytePlainOrg = Base64.decode(background, 0);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(background);
        Bitmap img = BitmapFactory.decodeStream(inputStream);

        //Resources res = myContext
        Resources res = mContext.getResources();

        Drawable backImg = new BitmapDrawable(res, img);
        //postBackground.setBackgroundDrawable(backImg);
        postBackground.setBackground(backImg);
        // 값들을 넣어야할 뷰에 넣어줌


        btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PressGood pressGood = new PressGood();
                if(btn_like_selected == true){
                    pressGood.execute(userId, postId, Integer.parseInt(good));
                    btn_like.setImageResource(R.drawable.ic_baseline_favorite_24);
                    number_of_like_post.setText(String.valueOf(Integer.parseInt(good) - 1));
                    good = String.valueOf(Integer.parseInt(good) - 1);
                    btn_like_selected = false;
                } else{
                    pressGood.execute(userId, postId, Integer.parseInt(good));
                    btn_like.setImageResource(R.drawable.ic_baseline_favorite_24_selected);
                    number_of_like_post.setText(String.valueOf(Integer.parseInt(good) + 1));
                    good = String.valueOf(Integer.parseInt(good) + 1);
                    btn_like_selected = true;
                }
            }
        }); // 좋아요 버튼을 누르면 작동, 현재 좋아요 상태에 따라서 반대로 작동

        btn_mention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mentionWindow();
            }
        });
        //댓글 뷰를 누르면 댓글 목록을 표시해줌

        return rootView;
    }

    protected void setLikeImage(boolean btn_like_selected){
        if(btn_like_selected == true){
            btn_like.setImageResource(R.drawable.ic_baseline_favorite_24_selected);
        } else{
            btn_like.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    } // 좋아요 상태에 따라서 좋아요 버튼 모양을 잡아줌

    //좋아요버튼을 누르면 작동 좋아요를 안누르면 상태면 좋아요 갯수1증가, 좋아요버튼 빨간색, 좋아요를 누른 상태면 반대로작동
    class PressGood extends AsyncTask<Integer, Void, String>{

        @Override
        protected String doInBackground(Integer... params) {
            String serverURL;
            if(btn_like_selected == false){
                //serverURL = "http://" + ipAddress + "/caround/clickGood.php";
                serverURL = "http://" + towsomeAddress + "/caround/clickGood.php";
            } else{
                //serverURL = "http://" + ipAddress + "/caround/unClickGood.php";
                serverURL = "http://" + towsomeAddress + "/caround/unClickGood.php";
            }

            int userId = params[0];
            int postId = params[1];
            int pre_good = params[2];

            String postParameters = "userId=" + userId + "&postId=" + postId + "&good=" + pre_good;

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
        }
    }

    //좋아요 상태와 좋아요 갯수 그리고 댓글 갯수를 파악함
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

                    btn_like_selected = jsonObj.getBoolean("state"); // 좋아요 상태값을 받아옴
                    good = String.valueOf(jsonObj.getInt("goodCount")); //좋아요 갯수를 받아옴
                    mention_count = String.valueOf(jsonObj.getInt("mentionCount")); //댓글 갯수를 받아옴
                    //postContent.setText(String.valueOf(btn_like_selected));
                    //postContent.setText(s);
                    setLikeImage(btn_like_selected);
                    number_of_like_post.setText(good);
                    number_of_mention_post.setText(mention_count); // 알맞게 셋팅해줌
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        SelectGood selectGood = new SelectGood();
        selectGood.execute(userId, postId);
    }

    private void mentionWindow(){
        mentionDialog = new MentionBaseDialog(mContext, R.layout.dialog_mention); // 댓글 다이얼로그 생성
        mention_send = (Button)mentionDialog.findViewById(R.id.mention_send);
        mention_write = (EditText)mentionDialog.findViewById(R.id.mention_write);
        mention_listView = (ListView)mentionDialog.findViewById(R.id.mention_listView);
        mention_relativeLayout = (RelativeLayout)mentionDialog.findViewById(R.id.mention_relativeLayout);
        setMention(postId); //댓글 목록을 세팅함
        mention_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mention_text = mention_write.getText().toString();
                if(mention_text != ""){
                    plusMention(mention_text, userId, postId); // 데이터베이스에 댓글을 추가해줌
                } else{
                    Toast.makeText(mContext, "내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }); // 보내기 버튼을 누르면 댓글이 추가됨
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
        }); // 댓글 목록을 스크롤하면 높이가 변함

        //댓글을 꾹 누르면 작동
        mention_listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final BaseDialog mentionLongClick = new BaseDialog(mContext, R.layout.dialog_mentioned_long_click);
                TextView mention_update = (TextView) mentionLongClick.findViewById(R.id.mention_update);
                TextView mention_delete = (TextView) mentionLongClick.findViewById(R.id.mention_delete);
                // 수정, 삭제 버튼이 나옴
                MentionItem nowItem = (MentionItem) mentionsAdapter.getItem(i); //현재 댓글아이템을 받아옴
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
                }); // 댓글 수정

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
                }); // 댓글 삭제

                mentionLongClick.show();
                return false;
            }
        });

        mentionDialog.show();
    }

    //댓글 추가
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
                    mentionsAdapter.notifyDataSetChanged(); // 목록 내용이 변했다는 것을 알려줌
                    setGood(userId, postId); // 데이터베이스로부터 다시 좋아요와 댓글 상태를 받아옴
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        InsertMention insertMention = new InsertMention();
        insertMention.execute(mention, String.valueOf(userId), String.valueOf(postId));
    }

    //댓글 목록 어댑터
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

    //댓글 버튼을 누르면 댓글 목록이 나옴
    protected void ShowMention(String mJson){
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

    //댓글들을 데이터베이스로부터 받아옴
    public void setMention(int postId){
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
                ShowMention(mJson);

            }
        }
        SelectMention selectMention = new SelectMention();
        selectMention.execute(postId);
    }

    //댓글 수정
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

    //댓글 삭제
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
}
