package com.projes.caround;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

public class RecDialog extends Dialog {
    MediaRecorder recorder;
    String filename;
    MediaPlayer player;
    ImageView btn_rec;
    ImageView btn_re;
    ImageView btn_account;
    ImageView btn_cancle;
    protected Activity mactivity;
    protected Context mContext;
    int count = 1;

    public RecDialog(@NonNull Activity _activity, final Context _context) {
        super(_context);
        final Dialog dlg=new Dialog(_context);
        this.mactivity=_activity;
        this.mContext=_context;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.record_dialog2);
        btn_rec=findViewById(R.id.btn_rec);
        btn_re=findViewById(R.id.btn_re);
        btn_re.setEnabled(false);
        btn_account=findViewById(R.id.btn_account);
        btn_account.setEnabled(false);
        btn_cancle=findViewById(R.id.cancle_button);

        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard,"recorded.mp4");
        filename=file.getAbsolutePath();
        Log.d("MainActivity", "저장할 파일 명 : " + filename);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        Window window = getWindow();
        if(window != null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // 높이,너비,애니메이션 설정을 위한 파라미터
            WindowManager.LayoutParams params = window.getAttributes();

            //높이,너비 최대로
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

            //열고 닫히는 애니메이션 지정
            params.windowAnimations = R.style.AnimationPopupStyle;

            // 파라미터 반영
            window.setAttributes(params);

            window.setGravity(Gravity.BOTTOM);
        }
        findViewById(R.id.btn_rec).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(count==1)
                {
                    btn_rec.setImageResource(R.drawable.circle_stop);
                    recordAudio();
                    count+=1;
                }
                else if(count==2)
                {
                    btn_rec.setImageResource(R.drawable.circle_play);
                    stopRecording();
                    count+=1;
                    btn_re.setEnabled(true);
                    btn_account.setEnabled(true);
                }
                else if(count==3){
                    btn_rec.setImageResource(R.drawable.circle_stop);
                    playAudio();
                    count+=1;
                }
                else if(count==4){
                    btn_rec.setImageResource(R.drawable.circle_play);
                    stopAudio();
                    count=3;
                }
            }
        });
        findViewById(R.id.btn_re).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {   btn_re.setEnabled(false);
                btn_account.setEnabled(false);
                count=1;
                btn_rec.setImageResource(R.drawable.circle_stop);
                recordAudio();
                count+=1;
            }
        });
        findViewById(R.id.cancle_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                RecDialog.this.dismiss(); //종료
            }
        });
    }

    private void recordAudio(){
        recorder = new MediaRecorder(); //MediaPlay와 함께사용하여 마이크에서 오디오를 캡쳐하는 API
        /* 그대로 저장하면 용량이 크다.
         * 프레임: 한 순간의 음성이 들어오면, 음성을 바이트 단위로 전부 저장하는 것
         * 초당 15프레임이면 보통 8K(8000바이트) 정도가 한순간에 저장됨
         * 따라서 용량이 크므로, 압축할 필요가 있음. */
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //어디에서 음성 데이터를 받을건지
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); //압축 형식 설정
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //오디오 인코더 설정
        recorder.setOutputFile(filename); //출력파일 이름 설정
        try{
            recorder.prepare();
            recorder.start();
            Toast.makeText(mContext,"녹음 시작됨.",Toast.LENGTH_SHORT).show();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void closePlayer(){
        if(player!=null){
            player.release();
            player=null;
        }
    }

    private void playAudio(){
        try{
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(filename);
            player.prepare();
            player.start();

            Toast.makeText(mContext,"재생 시작됨",Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    private void stopRecording(){
        if(recorder != null){
            recorder.stop();
            recorder.release();
            recorder=null;
            Toast.makeText(mContext,"녹음 중지됨.",Toast.LENGTH_SHORT).show();
        }
    }
    private void stopAudio(){
        if(player!=null && player.isPlaying()){
            player.stop();

            Toast.makeText(mContext,"중지됨",Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onClick(View view) {
//        if(view == btn_rec){
//            //btn_rec.setImageResource(R.drawable.circle_stop);
//            recordAudio();
//        }
//    }

}
