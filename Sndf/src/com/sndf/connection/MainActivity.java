package com.sndf.connection;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.sndf.connection.sample.ChatWidget;
import com.sndf.connection.sample.Controller;

public class MainActivity extends Activity 
{
    private ChatWidget mChatShow;
    private Controller mController;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mChatShow = new ChatWidget(this);
        mController = new Controller(mChatShow, this);
        setContentView(mChatShow);
    }
}
