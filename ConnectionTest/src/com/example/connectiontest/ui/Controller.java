package com.example.connectiontest.ui;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.connectiontest.base.NetUtil;
import com.example.connectiontest.connection.ConnectionDirector;
import com.example.connectiontest.connection.Listener;
import com.example.connectiontest.discovery.HostDiscovererClient;
import com.example.connectiontest.discovery.HostDiscovererServer;
import com.example.connectiontest.discovery.IDiscoveryHandler;
import com.example.connectiontest.message.IHandler;
import com.example.connectiontest.message.IMessage;

public class Controller
{
    private HostDiscovererClient mHostDiscovererClient;
    
    private HostDiscovererServer mHostDiscovererServer;

    private ConnectionDirector mDirector;
    
    private ChatWidget mChatWidget;
    
    private Context mContext ;
    
    public Controller(ChatWidget widget, Context context)
    {
        mContext = context;

        mChatWidget = widget;

        setListener();
        
        initAndStartDiscoverServer();
        
        initAndDiscoverClient();
        
        mChatWidget.setDirector(mDirector);
        
        mChatWidget.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                broadcast();
            }
        });
    }
    
    public void broadcast()
    {
        try
        {
            mHostDiscovererClient.discoverHostByMulticast(InetAddress.getByName("230.0.0.1"), 8820, 3000, true, new BroadRequest());
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void initAndDiscoverClient()
    {
        mHostDiscovererClient = new HostDiscovererClient();
        mHostDiscovererClient.setDiscoverHandler(new IDiscoveryHandler()
        {
            @Override
            public void onFinally()
            {
            }
            
            @Override
            public void onDiscoveredHost(IMessage response)
            {
                Toast.makeText(mContext, "Descover Host", Toast.LENGTH_SHORT).show();

                if (response instanceof DeviceInfo)
                {
                    DeviceInfo info = (DeviceInfo)response;
                    mDirector.connect(info.mAddress, 2000);
                }
            }
        });
    }
    
    public void initAndStartDiscoverServer()
    {
        try
        {
            mHostDiscovererServer = new HostDiscovererServer(8820, InetAddress.getByName("230.0.0.1"));
        }
        catch (UnknownHostException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mHostDiscovererServer.setRequestHandler(new IHandler()
        {
            @Override
            public void handleMessage(IMessage msg)
            {
                Toast.makeText(mContext, "Response Descover Host", Toast.LENGTH_SHORT).show();
                mHostDiscovererServer.sendMessage(new DeviceInfo(Build.MODEL, mDirector.getServerAddress()), msg.getRemoteAddress());
            }
        });

        mHostDiscovererServer.start();
    }
    
    private void setListener()
    {
        mDirector = new ConnectionDirector();
        mDirector.bind(new InetSocketAddress(NetUtil.getHostAddress(), 1234));
        mDirector.setListener(new Listener()
        {
            @Override
            public void onReceived(int connectionId, IMessage msg)
            {
                if (msg instanceof ChatMessage)
                {
                    ChatMessage message = (ChatMessage)msg;
                    mChatWidget.showText(message.getString(), true);
                }
            }
            
            @Override
            public void onConnected(int connectionId)
            {
                Toast.makeText(mContext, "Connected" + connectionId, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onConnectFailed(int connectionId)
            {
                Toast.makeText(mContext, "Connected fainled" + connectionId, Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void disconnected(int connectionId)
            {
                Toast.makeText(mContext, "Dis Connected" + connectionId, Toast.LENGTH_SHORT).show();
            }
        });
        
        mDirector.start();
    }

}
