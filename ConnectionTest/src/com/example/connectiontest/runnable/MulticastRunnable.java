package com.example.connectiontest.runnable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Message;

import com.example.connectiontest.base.Debug;
import com.example.connectiontest.base.NetUtil;
import com.example.connectiontest.discovery.DiscoverMsg;
import com.example.connectiontest.message.IMessage;
import com.example.connectiontest.serializable.SerializableMessageUtil;

public class MulticastRunnable implements Runnable
{
    private static final String TAG = "MulticastRunnable";

    private InetAddress mGroupAddress;
    private int mRemotePort;
    private int mTimeout = 0;
    private IMessage mMessage;
    private Handler mHandler;
    private boolean mDiscoverAll = false;
    
    public MulticastRunnable(InetAddress address, int port, int timeout, IMessage msg, boolean discoverAll, Handler handler)
    {
        mGroupAddress = address;
        mRemotePort = port;
        mTimeout = timeout;
        mMessage = msg;
        mDiscoverAll = discoverAll;
        mHandler = handler;
    }

    @Override
    public void run()
    {
        MulticastSocket socket = null;
        try
        {
            socket = new MulticastSocket();
            socket.joinGroup(mGroupAddress);
            multicast(socket, mMessage);
            socket.setSoTimeout(mTimeout);
            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

            while (true)
            {
                try
                {
                    socket.receive(packet);
                    IMessage msg = SerializableMessageUtil.readMessage(packet.getData(), packet.getSocketAddress());

                    InetSocketAddress socketAddress = (InetSocketAddress)msg.getRemoteAddress();
                    if (null != msg && null != socketAddress 
                            && !NetUtil.isHostAddress(socketAddress.getAddress()))
                    {
                        Message.obtain(mHandler, DiscoverMsg.MSG_DISCOVER_HOST, msg).sendToTarget();
                    }

                    if (!mDiscoverAll)
                    {
                        break;
                    }
                }
                catch (SocketTimeoutException e)
                {
                    Debug.e(TAG, "Host discover timed out.");
                    break;
                }
            }
        }
        catch (IOException e)
        {
            Debug.e(TAG, "Host discover failed.");
        }
        finally
        {
            if (null != socket)
            {
                try
                {
                    socket.leaveGroup(mGroupAddress);
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            Message.obtain(mHandler, DiscoverMsg.MSG_DISCOVER_END).sendToTarget();
        }
    }

    /**
     * Multicast a UDP message on the Lan to discover one or any running
     * servers.
     * 
     * @param udpPort
     *            the udp port of the server.
     * @param msg 
     *            the multicast request.
     */
    private void multicast(MulticastSocket socket, IMessage msg) throws IOException
    {
        byte[] data = SerializableMessageUtil.wirteMessage(msg);

        if (null == data)
        {
            throw new RuntimeException("Broadcast message serialization faied.");
        }
        
        try
        {
            socket.send(new DatagramPacket(data, data.length, mGroupAddress, mRemotePort));
        }
        catch (Exception ignored)
        {
        }
    }
}
