package com.projes.caround;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogActivity extends AppCompatActivity {

    EditText edit_email;
    EditText edit_year;
    RadioButton btn_man;
    RadioButton btn_woman;
    RadioGroup btn_sex;
    TextView testText;
    String email;
    String year;
    int userId;

    String ipAddress = "192.168.0.18";
    String cozyIpAddress = "172.30.1.8";
    String towsomeAddress = "192.168.0.12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_year = (EditText) findViewById(R.id.edit_year);
        btn_man = (RadioButton) findViewById(R.id.btn_man);
        btn_woman = (RadioButton) findViewById(R.id.btn_woman);
        testText = (TextView) findViewById(R.id.testText);

        Button btn_createUser = (Button) findViewById(R.id.btn_createUser);
        btn_createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edit_email.getText().toString();
                year = edit_year.getText().toString();
                String sex;
                if(btn_man.isChecked()){
                    sex = "0";
                    //testText.setText(sex);
                } else if(btn_woman.isChecked()){
                    sex = "1";
                } else{
                    sex = "";
                }

                if(email != null && year != null){
                    InsertAccount insertAccount = new InsertAccount();
                    //insertAccount.execute("http://" + ipAddress + "/caround/insertUser.php", email, year, sex);
                    insertAccount.execute("http://" + towsomeAddress + "/caround/insertUser.php", email, year, sex);
                }
                else{
                    Toast.makeText(getApplicationContext(), "필수정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
            }
        }); //회원가입

        Button btn_logIn = (Button) findViewById(R.id.btn_logIn);
        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = edit_email.getText().toString();
                year = edit_year.getText().toString();

                if(email != null && year != null){
                    LoginAccount loginAccount = new LoginAccount();
                    //loginAccount.execute("http://" + ipAddress + "/caround/Login.php", email, year);
                    loginAccount.execute("http://" + towsomeAddress + "/caround/Login.php", email, year);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("userId", userId);
                            startActivity(intent);
                            finish();
                        }
                    }, 500);
                }
                else{
                    Toast.makeText(getApplicationContext(), "필수정보를 입력하세요.", Toast.LENGTH_SHORT).show();
                }

//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//                finish();
            }
        }); //로그인
    }

    class InsertAccount extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String)params[0];

            String email = (String)params[1];
            int year = Integer.parseInt(params[2]);
            int sex = Integer.parseInt(params[3]);

            String postParameters = "email=" + email + "&year=" + year + "&sex=" + sex;

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
            Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
            edit_email.setText("");
            edit_year.setText("");
            //testText.setText(s);
            //testText.setText("회원가입 완료");
//
//            Intent backIntent = new Intent();
//            //backIntent.putExtra("writer", Integer.parseInt(set_writer));
//            backIntent.putExtra("title", set_title);
//            backIntent.putExtra("content", set_content);
//
//            setResult(Activity.RESULT_OK, backIntent);
//
//            finish();

        }
    }

    class LoginAccount extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];

            String email = (String)params[1];
            int year = Integer.parseInt(params[2]);

            String postParameters = "email=" + email + "&year=" + year;

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
                userId = Integer.parseInt(jsonObj.getString("Id"));
                Toast.makeText(getApplicationContext(), "UserId:" + userId, Toast.LENGTH_SHORT).show();
            } catch(Exception e){
                e.printStackTrace();
            }

        }
    }

}