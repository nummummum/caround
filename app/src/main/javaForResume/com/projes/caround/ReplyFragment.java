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
import android.util.Base64;
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

public class ReplyFragment extends Fragment {

    protected Activity mactivity;
    protected Context mContext;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.8";
    String towsomeAddress = "192.168.0.12";

    ArrayList<MentionItem> mentionList;
    MentionsAdapter mentionsAdapter;

    TextView centerStoryMessage;
    TextView subStoryMessage;
    ListView myMention_listView;

    int userId;
    int postId;

    BaseDialog mentionedPost;
    FrameLayout postBackground;
    TextView postContent;
    TextView text_distance_post;
    TextView dateTime_post;
    TextView number_of_like_post;
    TextView number_of_mention_post;
    ImageView btn_like;
    ImageView btn_mention;
    String good;
    String mention_count;
    boolean btn_like_selected;
    MentionBaseDialog mentionDialog;
    Button mention_send;
    EditText mention_write;
    ListView mention_listView;
    RelativeLayout mention_relativeLayout;
    Button btn_myStoryPostClose;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mactivity = getActivity();
        this.mContext = context;
    }

    public static ReplyFragment newInstacne(){
        ReplyFragment replyFragment=new ReplyFragment();
        return replyFragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_reply, container, false);

        userId = mactivity.getIntent().getIntExtra("userId", 0);

        centerStoryMessage = (TextView) rootView.findViewById(R.id.centerStoryMessage);
        subStoryMessage = (TextView) rootView.findViewById(R.id.subStoryMessage);
        myMention_listView = (ListView) rootView.findViewById(R.id.myMention_listView);

        setMentionById(userId, true); // 마이프래그먼트의 내 댓글 목록을 보여줌

        myMention_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MentionItem nowItem = (MentionItem) mentionsAdapter.getItem(i);
                //Toast.makeText(mContext, "클릭됨" + nowItem.getPosted(), Toast.LENGTH_SHORT).show();
                //내 댓글을 클릭하면 dialog로 띄워주게 구성하면 될 것 같다. 위에서 받은 postId로 해당 게시글 내용을 받아와서
                postId = nowItem.getPosted();
                setMentionById(postId, false);
            }
        }); // 댓글을 클릭하면 해당 댓글이 달린 게시글로 이동

        return rootView;
    }

    //댓글 목록 어댑터
    class MentionsAdapter extends BaseAdapter {
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

    //댓글 목록을 보여줌
    protected void ShowMention(String mJson, boolean choose, boolean main){
        if(choose){ // 내 댓글 목록을 보여줌
            try {
                mentionList = new ArrayList<MentionItem>();
                JSONObject jsonObj = new JSONObject(mJson);
                JSONArray mentions = jsonObj.getJSONArray("result");

                if(mentions.length() != 0){
                    centerStoryMessage.setVisibility(View.GONE);
                    subStoryMessage.setVisibility(View.GONE);
                }

                for(int i=0; i<mentions.length(); i++){
                    JSONObject mention = mentions.getJSONObject(i);
                    String content = mention.getString("mention");
                    String date = mention.getString("mdate");
                    int posted = Integer.parseInt(mention.getString("postId"));
                    //int mId = Integer.parseInt(mention.getString("mId"));

                    MentionItem mentionItem = new MentionItem(content, date, R.drawable.ic_12, posted);
                    mentionList.add(mentionItem);
                }

                mentionsAdapter = new MentionsAdapter();
                mentionsAdapter.addItems(mentionList);
                if(main){ // 내 댓글 목록일 때
                    myMention_listView.setAdapter(mentionsAdapter); // 내 댓글 목록을 보여줌
                } else{ // 댓글을 클릭하고 들어간 게시글에서 댓글 버튼을 누를때 해당 게시글의 댓글 목록
                    mention_listView.setAdapter(mentionsAdapter); // 해당 댓글이 달린 게시글의 댓글 목록을 보여줌
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        } else{ // 내댓글에서 댓글을 클릭하고 보여지는 게시글을 세팅해줌
            try{
                JSONObject jsonObj = new JSONObject(mJson);
                String content = jsonObj.getString("content");
                String location = jsonObj.getString("location");
                String pdate = jsonObj.getString("pdate");
                //String good = String.valueOf(post.getInt("good"));
                //String mention = String.valueOf(jsonObj.getInt("mention"));
                String background = jsonObj.getString("background");
                byte[] bytePlainOrg = Base64.decode(background, Base64.DEFAULT);
                int postId = Integer.parseInt(jsonObj.getString("postId"));

                String[] dateTime;
                String time;

                dateTime = pdate.split(" ");
                time = dateTime[1];
                dateTime = time.split(":");
                time = dateTime[0] + ":" + dateTime[1];

                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytePlainOrg);
                Bitmap img = BitmapFactory.decodeStream(inputStream);

                Resources res = mContext.getResources();

                Drawable backImg = new BitmapDrawable(res, img);
                //postBackground.setBackground(backImg);

                mentionedPostWindow(content, location, time, backImg); // 내 댓글에서 댓글을 클릭하면 나타나는 게시글 세팅

            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    //내 댓글을 표시함
    public void setMentionById(int Id, final boolean choose){
        class SelectMentionById extends AsyncTask<Integer, Void, String> {
            @Override
            protected String doInBackground(Integer... params) {
                //String serverURL = "http://" + ipAddress + "/caround/selectMentionById.php";
                String serverURL = "http://" + towsomeAddress + "/caround/selectMentionById.php";

                String postParameters;
                if(choose){
                    int userId = params[0];
                    postParameters = "userId=" + userId;
                } else{
                    int postId = params[0];
                    postParameters = "postId=" + postId;
                }

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
                //centerStoryMessage.setText(s);
                String mJson = s;
                ShowMention(mJson, choose, true);

            }
        }

        SelectMentionById selectMentionById = new SelectMentionById();
        selectMentionById.execute(Id);

    }

    protected void setLikeImage(boolean btn_like_selected){
        if(btn_like_selected == true){
            btn_like.setImageResource(R.drawable.ic_baseline_favorite_24_selected);
        } else{
            btn_like.setImageResource(R.drawable.ic_baseline_favorite_24);
        }
    } // 좋아요 상태에 따라서 좋아요 버튼 표시가 변함

    //내댓글에서 댓글 클릭하면 나타나는 게시글의 좋아요 상태, 좋아요 갯수, 댓글 갯수를 파악함
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

                    btn_like_selected = jsonObj.getBoolean("state");
                    good = String.valueOf(jsonObj.getInt("goodCount"));
                    mention_count = String.valueOf(jsonObj.getInt("mentionCount"));
                    //postContent.setText(String.valueOf(btn_like_selected));
                    //postContent.setText(s);
                    setLikeImage(btn_like_selected);
                    number_of_like_post.setText(good);
                    number_of_mention_post.setText(mention_count);
                    //centerStoryMessage.setText(s);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        SelectGood selectGood = new SelectGood();
        selectGood.execute(userId, postId);
    }

    //내 댓글에서 클릭하면 나타나는 게시글에서 좋아요 버튼 누를때 작동
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

    //내 댓글에서 댓글 클릭후 나타나는 게시글에서 댓글 목록을 누를때 댓글 목록을 받아옴
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
                ShowMention(mJson, true, false);

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
                    setMentionById(userId, true);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        InsertMention insertMention = new InsertMention();
        insertMention.execute(mention, String.valueOf(userId), String.valueOf(postId));
    }

    private void mentionWindow(){
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

    //내 댓글에서 댓글을 클릭하면 해당 댓글이 달린 게시글을 보여주는 다이얼로그 함수
    private void mentionedPostWindow(String content, String location, String time, Drawable background){
        mentionedPost = new BaseDialog(mContext, R.layout.dialog_post);
        postBackground = (FrameLayout) mentionedPost.findViewById(R.id.myStoryPostBackground);
        postContent = (TextView) mentionedPost.findViewById(R.id.myPostContent);
        text_distance_post = (TextView) mentionedPost.findViewById(R.id.myText_distance_post);
        dateTime_post = (TextView) mentionedPost.findViewById(R.id.myDateTime_post);
        number_of_like_post = (TextView) mentionedPost.findViewById(R.id.myNumber_of_like_post);
        number_of_mention_post = (TextView) mentionedPost.findViewById(R.id.myNumber_of_mention_post);
        btn_like = (ImageView) mentionedPost.findViewById(R.id.myBtn_like);
        btn_mention = (ImageView) mentionedPost.findViewById(R.id.myBtn_mention);
        btn_myStoryPostClose = (Button) mentionedPost.findViewById(R.id.btn_myStoryPostClose);

        if(location == "null"){
            location = "SomeWhere";
        }

        postContent.setText(content);
        text_distance_post.setText(location);
        dateTime_post.setText(time);
        postBackground.setBackground(background);
        setGood(userId, postId);

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
        });

        btn_mention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mentionWindow();
            }
        });

        btn_myStoryPostClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mentionedPost.cancel();
            }
        });

        mentionedPost.show();
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
}
