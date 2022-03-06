package com.projes.caround;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class MentionBaseDialog extends Dialog {

    private Context mContext;
    private Window mWindow;
    private WindowManager.LayoutParams mParams;

    public MentionBaseDialog(@NonNull Context context, int layoutId) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);
        this.mContext = context;

        setCancelable(true);
        setCanceledOnTouchOutside(true);

//        Window window = getWindow();
//        if(window != null){
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            // 높이,너비,애니메이션 설정을 위한 파라미터
//            WindowManager.LayoutParams params = window.getAttributes();
//
//            //높이,너비 최대로
//            params.width = WindowManager.LayoutParams.MATCH_PARENT;
//            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            //params.height = 400;
//
//            //열고 닫히는 애니메이션 지정
//            params.windowAnimations = R.style.AnimationPopupStyle;
//
//            // 파라미터 반영
//            window.setAttributes(params);
//
//            window.setGravity(Gravity.BOTTOM);
//        }

        this.mWindow = getWindow();
        if(mWindow != null){
            mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            this.mParams = mWindow.getAttributes();

            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            //mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.height = 400;

            mParams.windowAnimations = R.style.AnimationPopupStyle;

            mWindow.setAttributes(mParams);
            mWindow.setGravity(Gravity.BOTTOM);
        }
    }

    public Window getmWindow() {
        return mWindow;
    }

    public WindowManager.LayoutParams getmParams() {
        return mParams;
    }

    public void setParamsHeight(int height){
        int curHeight = mParams.height;
        mParams.height = curHeight - height;
        mWindow.setAttributes(mParams);
    }
}
