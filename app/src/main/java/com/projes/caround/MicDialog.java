package com.projes.caround;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MicDialog {
    private Context context;
    private Activity mactivity;
    public MicDialog(Activity activity,Context context){
        this.context = context;
        this.mactivity = activity;
    }
    RecDialog recDialog;
    public void callFunction(){
        final Dialog dlg=new Dialog(context);
        //커스텀 다이얼로그 만들기 위한 Dialog 생성
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //액티비티의 타이틀바를 숨긴다.
        dlg.setContentView(R.layout.record_dialog);
        //레이아웃 설정
        final TextView record_now = dlg.findViewById(R.id.record_now);
        final TextView upload_record = dlg.findViewById(R.id.upload_record);
        final ImageView btn_cancle = dlg.findViewById(R.id.cancle_button);
        dlg.show();

        record_now.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(context,"지금 녹음을 누르셨습니다",Toast.LENGTH_SHORT).show();
                recDialog = new RecDialog(mactivity,context);
                recDialog.show();
                dlg.dismiss();
            }
        });
        upload_record.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(context,"녹음 올리기",Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
        btn_cancle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(context,"취소하셨습니다",Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
