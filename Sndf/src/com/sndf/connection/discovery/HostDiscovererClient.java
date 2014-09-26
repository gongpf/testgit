package com.sndf.connection.discovery;

import java.net.InetAddress;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.sndf.connection.base.DefaultExecutor;
import com.sndf.connection.message.IMessage;
import com.sndf.connection.runnable.BroadCastRunnable;
import com.sndf.connection.runnable.MulticastRunnable;

public class HostDiscovererClient
{
    private IDiscoveryHandler mDiscoveryHandler = null;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (null == mDiscoveryHandler)
            {
                throw new RuntimeException("the discovery handler is null");
            }

            switch (msg.what)
            {
                case DiscoverMsg.MSG_DISCOVER_HOST:
                    mDiscoveryHandler.onDiscoveredHost((IMessage)msg.obj);
                    break;

                case DiscoverMsg.MSG_DISCOVER_END:
                    mDiscoveryHandler.onFinally();
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * Set the dicovery handler. 
     * @param handler
     */
    public void setDiscoverHandler(IDiscoveryHandler handler)
    {
        mDiscoveryHandler = handler;
    }

    /**
     * Broadcast a UDP message on the Lan to discover one or any running
     * servers.
     * 
     * @param udpPort
     *            the udp port of the server.
     * @param timeoutMillis
     *            the number of milliseconds to wait for a response
     * @param discoverAll
     *            whether discover all running servers.
     */
    public void discoverHostByBroadcast(int udpPort, int timeout, boolean discoverAll, IMessage msg)
    {
        DefaultExecutor.executeInNonMainThread(new BroadCastRunnable(udpPort, timeout, msg, discoverAll, mHandler));
    }

    /**
     * Multicast a UDP message on the Lan to discover one or any running
     * servers.
     * 
     * @param udpPort
     *            the udp port of the server.
     * @param timeoutMillis
     *            the number of milliseconds to wait for a response
     * @param discoverAll
     *            whether discover all running servers.
     */
    public void discoverHostByMulticast(InetAddress address, int udpPort, int timeout, boolean discoverAll, IMessage msg)
    {
        DefaultExecutor.executeInNonMainThread(new MulticastRunnable(address, udpPort, timeout, msg, discoverAll, mHandler));
    }
}
