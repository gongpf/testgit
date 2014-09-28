package com.sndf.connection.sample;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChatShow extends ScrollView
{
    private Context mContext;
    private LinearLayout mLinearLayout;
    
    public ChatShow(Context context)
    {
        super(context);
        mContext = context;
        init();
    }
    
    private void init()
    {
        mLinearLayout = new LinearLayout(mContext);
        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
        addView(mLinearLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    
    public void showText(String string, boolean isLeft)
    {
        TextView tx = new TextView(mContext);
        tx.setText(string);
        tx.setTextSize(25);
        tx.setPadding(10, 10, 10, 10);
        
        LinearLayout.LayoutParams tLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                LayoutParams.WRAP_CONTENT);
        tLayoutParams.topMargin = 100;
        if (isLeft)
        {
            tLayoutParams.rightMargin = 100;
            tLayoutParams.gravity = Gravity.LEFT;
            tx.setBackgroundColor(Color.CYAN);
        }
        else 
        {
            tLayoutParams.leftMargin = 100;
            tLayoutParams.gravity = Gravity.RIGHT;
            tx.setBackgroundColor(Color.GREEN);
        }
        
        mLinearLayout.addView(tx, tLayoutParams);
    }

}
