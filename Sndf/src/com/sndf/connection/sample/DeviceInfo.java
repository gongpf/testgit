package com.sndf.connection.sample;

import java.net.SocketAddress;

import com.sndf.connection.message.IMessage;

public class DeviceInfo extends IMessage
{
    /**
     * 
     */
    private static final long serialVersionUID = 5695811163955359677L;

    public String mName = "";
    
    public SocketAddress mAddress = null;
    
    public DeviceInfo(String name, SocketAddress address)
    {
        mName = name;
        mAddress = address;
    }

}
