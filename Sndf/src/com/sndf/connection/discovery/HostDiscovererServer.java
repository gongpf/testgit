package com.sndf.connection.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Message;

import com.sndf.connection.base.AbstractCycleRunnable;
import com.sndf.connection.base.Debug;
import com.sndf.connection.base.DefaultExecutor;
import com.sndf.connection.message.IHandler;
import com.sndf.connection.message.IMessage;
import com.sndf.connection.runnable.SendRunnable;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class HostDiscovererServer extends AbstractCycleRunnable
{
    private static final String TAG = "HostDiscovererServer";

    private IHandler mRequestHandler;

    private DatagramPacket mData = new DatagramPacket(new byte[1024], 1024);
    private DatagramSocket mSocket = null;

    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (null == mRequestHandler)
            {
                throw new RuntimeException("the discovery request handler is null");
            }

            mRequestHandler.handleMessage((IMessage)msg.obj);
        }
    };

    /**
     * Construct a discover server which can receive broadcast request. 
     * @param port the port to bind.
     */
    public HostDiscovererServer(int port)
    {
        try
        {
            bindBroadcastSocket(port);
        }
        catch (IOException e)
        {
            Debug.e(TAG, "bind socket failed.");
            e.printStackTrace();
            close();
        }
    }
    
    /**
     * Construct a discover server which can receive multicast request. 
     * @param port the port to bind.
     * @param groupAddress the multicast address.
     */
    public HostDiscovererServer(int port, InetAddress groupAddress)
    {
        try
        {
            bindMulticastSocket(port, groupAddress);
        }
        catch (IOException e)
        {
            Debug.e(TAG, "bind socket failed.");
            e.printStackTrace();
            close();
        }
    }
    
    /**
     * Bind a broadcast socket, receive the broadcast request.
     * @throws IOException 
     */
    private void bindBroadcastSocket(int port) throws IOException
    {
        if (-1 == port)
        {
            throw new RuntimeException("the port is invalid");
        }

        try
        {
            mSocket = new DatagramSocket(port);
        }
        catch (SocketException e)
        {
            Debug.e(TAG, "Init sockt failed");

            if (null != mSocket)
            {
                mSocket.close();
            }
            throw e;
        }
    }

    /**
     * Bind a multicast socket, receive the multicast request.
     * @throws IOException 
     */
    private void bindMulticastSocket(int port, InetAddress groupAddress) throws IOException
    {
        if (-1 == port)
        {
            throw new RuntimeException("the port is invalid");
        }
        
        MulticastSocket socket = null;

        try
        {
            socket = new MulticastSocket(port);
            socket.joinGroup(groupAddress);
            mSocket = socket;
        }
        catch (SocketException e)
        {
            Debug.e(TAG, "Init sockt failed");

            if (null != socket)
            {
                socket.leaveGroup(groupAddress);
                socket.close();
            }
            throw e;
        }
    }

    @Override
    public void start()
    {
        super.start();
    }

    @Override
    public void doAction(int timeout) 
    {
        try
        {
            mSocket.setSoTimeout(timeout);
            mSocket.receive(mData);
        }
        catch (Exception e)
        {
            if (!(e instanceof SocketTimeoutException))
            {
                stop();
            }
            
            return ;
        }

        Debug.e(TAG, mData.getSocketAddress().toString());
        
        IMessage msg = SerializableMessageUtil.readMessage(mData.getData(), mData.getSocketAddress());

        if (null != msg)
        {
            Message.obtain(mHandler, 0, msg).sendToTarget();
        }
    }
    
    public void sendMessage(IMessage message, SocketAddress addr)
    {
        DefaultExecutor.executeInNonMainThread(new SendRunnable(mSocket, message, addr));
    }
    
    public void setRequestHandler(IHandler handler)
    {
        mRequestHandler = handler;
    }
}
