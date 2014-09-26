package com.sndf.connection.discovery;

import com.sndf.connection.message.IMessage;


public interface IDiscoveryHandler
{
    /**
     * Called when the {@link HostDiscovererClient} receives a discovery response.
     * @param response the response from {@link HostDicovererServer}
     */
    public void onDiscoveredHost(IMessage response);

    /**
     * This method will be called when discovered over.
     */
    public void onFinally();
}
