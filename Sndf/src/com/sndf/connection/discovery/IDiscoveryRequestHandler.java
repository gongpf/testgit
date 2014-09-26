package com.sndf.connection.discovery;

import java.net.SocketAddress;

import com.sndf.connection.message.IMessage;

public interface IDiscoveryRequestHandler
{
    /**
     * Called when the {@link HostDiscovererServer} receives a discovery request 
     * from {@link HostDiscovererClient}.
     * @param msg the request message from {@link HostDicovererClient}
     * @param addr the remote server address.
     */
    public void onDiscoveryRequest(IMessage msg, SocketAddress addr);
}
