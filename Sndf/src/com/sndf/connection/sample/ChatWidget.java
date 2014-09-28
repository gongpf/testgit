package com.sndf.connection.sample;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.sndf.connection.connection.ConnectionManager;

public class ChatWidget extends LinearLayout
{
    private ConnectionManager mDirector;

    private Context mContext;

    private Button mRefresh;

    private ChatShow mChatShow;
    
    private LinearLayout mSendLinearLayout;
    private EditText mEditText;
    private Button mSendButton;

    public ChatWidget(Context context)
    {
        super(context);
        mContext = context;
        setOrientation(VERTICAL);
        
        initRefesh();
        initChatShow();
        initSend();
        setSendListenr();
    }
    
    private void initRefesh()
    {
        mRefresh = new Button(mContext);
        mRefresh.setText("Rresh");
        addView(mRefresh, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }
    
    public void setOnClickListener(OnClickListener l)
    {
        mRefresh.setOnClickListener(l);
    }
    
    private void initChatShow()
    {
        mChatShow = new ChatShow(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        addView(mChatShow, params);
    }
    
    private void initSend()
    {
        mSendLinearLayout = new LinearLayout(mContext);
        mSendLinearLayout.setOrientation(HORIZONTAL);
        addView(mSendLinearLayout);

        mEditText = new EditText(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 150, 1);
        mSendLinearLayout.addView(mEditText, params);
        
        mSendButton = new Button(mContext);
        mSendButton.setText("Send");
        mSendLinearLayout.addView(mSendButton);
    }
    
    private void setSendListenr() 
    {
        mSendButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String string = mEditText.getEditableText().toString();

                showText(string, false);

                if (null != mDirector)
                {
                    mDirector.sendMessageToAll(new ChatMessage(string));
                }
            }
        });
    }

    public void showText(String string, boolean isLeft)
    {
        mChatShow.showText(string, isLeft);
        mChatShow.post(new Runnable()
        {
            @Override
            public void run()
            {
                mChatShow.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void setDirector(ConnectionManager mDirector2)
    {
        mDirector = mDirector2;
    }
}
