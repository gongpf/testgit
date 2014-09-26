package com.sndf.connection.base;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetUtil
{
    /**
     * Get all local addresses. 
     * @return a list of all local addresses.
     */
    public static List<InetAddress> getHostAddressList()
    {
        Enumeration<NetworkInterface> allNetInterfaces = null;
        try
        {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return null;
        }

        List<InetAddress> list = new ArrayList<InetAddress>();
        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();

            while (addresses.hasMoreElements())
            {
                InetAddress address = addresses.nextElement();
                if (address != null && address instanceof Inet4Address)
                {
                    list.add(address);
                }
            }
        }
        
        return list;
    }
    
    /**
     * Get a appropriate local address and not a loopback address.
     */
    public static InetAddress getHostAddress()
    {
        Enumeration<NetworkInterface> allNetInterfaces = null;
        try
        {
            allNetInterfaces = NetworkInterface.getNetworkInterfaces();
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            return null;
        }

        while (allNetInterfaces.hasMoreElements())
        {
            NetworkInterface netInterface = allNetInterfaces.nextElement();
            Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
            while (addresses.hasMoreElements())
            {
                InetAddress address = addresses.nextElement();
                if (address != null && address instanceof Inet4Address && !address.isLoopbackAddress())
                {
                    return address;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Judge whether the address is local address.
     */
    public static boolean isHostAddress(InetAddress address)
    {
        List<InetAddress> list = getHostAddressList();
        
        if (null == list)
        {
            return false;
        }
        
        for (InetAddress inetAddress : list)
        {
            if (inetAddress.equals(address))
            {
                return true;
            }
        }
        
        return false;
    }
}
