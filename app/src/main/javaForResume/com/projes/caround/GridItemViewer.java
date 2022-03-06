package com.projes.caround;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class GridItemViewer extends LinearLayout {

    ImageView imageView;
    public GridItemViewer(Context context) {
        super(context);
        init(context);
    }

    public GridItemViewer(Context context, @Nullable AttributeSet attrs){
        super(context);
        init(context);
    }
    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.griditem,this,true);
        imageView = (ImageView) findViewById(R.id.imageView);
    }
    public void setItem(GridItem gridItem){
        imageView.setImageResource(gridItem.getImage());
    }
}
