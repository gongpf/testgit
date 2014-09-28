package com.sndf.connection.sample;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sndf.connection.discovery.HostDiscovererClient;
import com.sndf.connection.discovery.IDiscoveryHandler;
import com.sndf.connection.message.IMessage;

public class BroadcastPage extends LinearLayout
{
    private Context mContext;
    private TextView mTitle;
    private ListView mListView;
    private Button mRefresh;

    private MyAdapter mAdapter;
    private HostDiscovererClient mDiscovererClient;
    
    private boolean mIsScan = false;

    public BroadcastPage(Context context)
    {
        super(context);
        mContext = context;

        init();
        
        initDiscoverClient();
        
        setOnClickListener();
    }
    
    private void init()
    {
        setOrientation(VERTICAL);

        mTitle = new TextView(mContext);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setText("Online List");
        mTitle.setBackgroundColor(Color.GREEN);
        mTitle.setPadding(0, 10, 0, 10);
        mTitle.setTextSize(25);
        addView(mTitle, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mRefresh = new Button(mContext);
        mRefresh.setGravity(Gravity.CENTER);
        mRefresh.setText("Refresh");
        mRefresh.setPadding(0, 10, 0, 10);
        mRefresh.setTextSize(25);
        addView(mRefresh, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        mListView = new ListView(mContext);
        mAdapter = new MyAdapter(mContext);
        mListView.setAdapter(mAdapter);
        addView(mListView);

    }
    
    private void initDiscoverClient()
    {
        mDiscovererClient = new HostDiscovererClient();
        mDiscovererClient.setDiscoverHandler(new IDiscoveryHandler()
        {
            @Override
            public void onFinally()
            {
                Toast.makeText(mContext, "Broadcast over!!", Toast.LENGTH_SHORT).show();
                mIsScan = false;
            }
            @Override
            public void onDiscoveredHost(IMessage response)
            {
                if (response instanceof DeviceInfo)
                {
                    mAdapter.addInfo((DeviceInfo)response);
                }
            }
        });
    }
    
    public void setOnClickListener()
    {
        mRefresh.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (mIsScan)
                {
                    Toast.makeText(mContext, "Broadcast is Running!!", Toast.LENGTH_SHORT).show();
                }
                else 
                {
                    mAdapter.clear();
                    mIsScan = true;
                    mDiscovererClient.discoverHostByBroadcast(8820, 3000, true, new BroadRequest());
                }
            }
        });
    }
}
