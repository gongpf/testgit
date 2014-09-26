package com.example.connectiontest.message;

import java.io.Serializable;
import java.net.SocketAddress;

import com.example.connectiontest.base.SystemInfo;

public abstract class IMessage implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final int mVersionCode = SystemInfo.VERSION_CODE;

    private SocketAddress mRemoteAddress = null;
    
    /**
     * Get the version code of te remote message. 
     */
    public int getVersionCode()
    {
        return mVersionCode;
    }
    
    /**
     * Judge whether the version of this remote message is valid.
     * @return if the version code of remote message equal to current version
     * return 0, more than current verison code return 1, or return -1.
     */
    public int judgeVersionCode()
    {
        int result = 0;

        if (this.mVersionCode < SystemInfo.VERSION_CODE)
        {
            result = -1;
        }
        else if (this.mVersionCode > SystemInfo.VERSION_CODE) 
        {
            result = 1;
        }
        
        return result;
    }
    
    /**
     * Gets the host address and the port which this IMessage is from 
     * as a {@code SocketAddress} object.
     * @return the SocketAddress of the original host.
     */
    public SocketAddress getRemoteAddress()
    {
        return mRemoteAddress;
    }
    
    /**
     * Set the host address and the port which this IMessage is from. 
     * @param addr the host address and the port.
     */
    public void setRemoteAddress(SocketAddress addr)
    {
        this.mRemoteAddress = addr;
    }
}
