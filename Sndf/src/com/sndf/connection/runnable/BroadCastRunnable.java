package com.sndf.connection.runnable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import android.os.Handler;
import android.os.Message;

import com.sndf.connection.base.Debug;
import com.sndf.connection.base.NetUtil;
import com.sndf.connection.discovery.DiscoverMsg;
import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class BroadCastRunnable implements Runnable
{
    private static final String TAG = "BroadCastRunnable";

    private static final String DEFAULT_BROADCAST_IP = "255.255.255.255";

    private int mUdpPort = -1;
    private int mTimeout = 0;
    private IMessage mMessage;
    private Handler mHandler;
    private boolean mDiscoverAll = false;
    
    public BroadCastRunnable(int udpPort, int timeout, IMessage msg, boolean discoverAll, Handler handler)
    {
        mUdpPort = udpPort;
        mTimeout = timeout;
        mMessage = msg;
        mDiscoverAll = discoverAll;
        mHandler = handler;
    }

    @Override
    public void run()
    {
        DatagramSocket socket = null;
        try
        {
            socket = new DatagramSocket();
            socket.setBroadcast(true);
            broadcast(mUdpPort, socket, mMessage);
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
                socket.close();
            }
            Message.obtain(mHandler, DiscoverMsg.MSG_DISCOVER_END).sendToTarget();
        }
    }

    /**
     * Broadcast a UDP message on the Lan to discover one or any running
     * servers.
     * 
     * @param udpPort
     *            the udp port of the server.
     * @param msg 
     *            the broadcast request.
     */
    private void broadcast(int udpPort, DatagramSocket socket, IMessage msg) throws IOException
    {
        byte[] data = SerializableMessageUtil.wirteMessage(msg);

        if (null == data)
        {
            throw new RuntimeException("Broadcast message serialization faied.");
        }
        
        try
        {
            socket.send(new DatagramPacket(data, data.length, InetAddress.getByName(DEFAULT_BROADCAST_IP), udpPort));
        }
        catch (Exception ignored)
        {
        }
    }
}
