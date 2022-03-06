package com.projes.caround;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NotifyService extends Service {

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.34";
    ArrayList<String> notifications;
    int userId;
    int postId;
    //int flag;

    public NotifyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifications.add("누군가가 당신의 글을 좋아합니다.");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null){
            return Service.START_STICKY;
        } else{
            processCommand(intent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void processCommand(Intent intent){
        userId = intent.getIntExtra("userId", 0);
        postId = intent.getIntExtra("postId", 0);
        String draw = notifications.get(intent.getIntExtra("nofityFlag", 0));

        Nofity nofity = new Nofity();
        nofity.execute(String.valueOf(userId), String.valueOf(postId), draw);
    }

    class Nofity extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            String serverURL = "http://" + ipAddress + "/caround/notify.php";

            int userId = Integer.parseInt(params[0]);
            int postId = Integer.parseInt(params[1]);
            String draw = params[2];

            String postParameters = "userId=" + userId + "&postId=" + postId + "&draw=" + draw;

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
}
