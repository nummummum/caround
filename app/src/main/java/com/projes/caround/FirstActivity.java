package com.projes.caround;

import androidx.annotation.Nullable;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class FirstActivity extends AppCompatActivity {
    private static String IP_ADDRESS = "192.168.0.26/";
    private EditText Edit_age;
    private RadioButton radio_man;
    private RadioButton radio_woman;
    private RadioGroup radioGroup;
    private Button btn_submit;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Edit_age = findViewById(R.id.edit_age);
        radio_man = findViewById(R.id.btn_man);
        radio_woman = findViewById(R.id.btn_wowan);
        btn_submit = findViewById(R.id.btn_submit);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radio_man.isChecked())
                {
                    if(Edit_age==null)
                    {
                        Toast.makeText(FirstActivity.this,"나이를 입력해주세요",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String sex;
                        sex="0";
                        String age;
                        age=Edit_age.getText().toString();
                        insertData task = new insertData();
                        Toast.makeText(FirstActivity.this,"성별 : "+ sex + " 나이 : " + age,Toast.LENGTH_SHORT).show();
                        task.execute("http://" + IP_ADDRESS + "/caround_account_insert.php",sex,age);
                        Edit_age.setText("");
                    }
                }
                else if(radio_woman.isChecked())
                {
                    if(Edit_age==null)
                    {
                        Toast.makeText(FirstActivity.this,"나이를 입력해주세요",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String sex;
                        sex="1";
                        String age;
                        age=Edit_age.getText().toString();
                        insertData task = new insertData();
                        Toast.makeText(FirstActivity.this,"성별 : "+ sex + " 나이 : " + age,Toast.LENGTH_SHORT).show();
                        task.execute("http://" + IP_ADDRESS + "/caround_account_insert.php",sex,age);
                        Edit_age.setText("");
                    }
                }
                else{
                    Toast.makeText(FirstActivity.this,"성별을 입력해주세요",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            if(i==R.id.btn_man)
            {
                Toast.makeText(FirstActivity.this,"남자를 선택하셨습니다",Toast.LENGTH_SHORT).show();
            }
            else if(i==R.id.btn_wowan){
                Toast.makeText(FirstActivity.this,"여자를 선택하셨습니다",Toast.LENGTH_SHORT).show();
            }
        }
    };

    class insertData extends AsyncTask<String, Void, String>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = ProgressDialog.show(FirstActivity.this,"기다려주세요",null,true,true);
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

        }
        @Override
        protected String doInBackground(String... params) {
            String serverURL = (String)params[0];
            String SEX = (String)params[1];
            String AGE = (String)params[2];
            String postParameters = "SEX="+SEX+"&AGE="+AGE;

            try{
                URL url =new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8")); //ID=아이디값&PW=비밀번호값 적음)
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("Find Error - ", "POST response code - " + responseStatusCode);
                Log.d("URL SEX AGE - ",serverURL + "///" + SEX + "///" + AGE);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            } catch (ProtocolException e) {
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                return new String("Error: " + e.getMessage());
            }
        }
    }

}
