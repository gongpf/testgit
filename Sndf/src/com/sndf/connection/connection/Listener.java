package com.sndf.connection.connection;

import com.sndf.connection.message.IMessage;

public interface Listener
{
    /**
     * Called when the remote end has been connected. This will be invoked
     * before any objects are received by {@link #received(ConnectionId, IMessage)}.
     * */
    public void onConnected(int connectionId);
    
    /**
     * Called when can't connect to the remote end. This will be invoked
     * after connect to the remote end timeout. 
     * */
    public void onConnectFailed(int connectionId);

    /**
     * Called when the remote end is no longer connected. There is no guarantee
     * as to what thread will invoke this method.
     * */
    public void disconnected(int connectionId);

    /**
     * Called when an object has been received from the remote end of the
     * connection. 
     * */
    public void onReceived(int connectionId, IMessage msg);
}
