package com.sndf.connection.runnable;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class SendRunnable implements Runnable
{
    private IMessage mMessage;
    private DatagramSocket mSocket;
    private SocketAddress mAddress;
    
    public SendRunnable(DatagramSocket socket, IMessage msg, SocketAddress addr)
    {
        if (null == msg || null == addr || null == socket)
        {
            throw new RuntimeException("Message is null or address or socket is null");
        }

        mSocket = socket;
        mMessage = msg;
        mAddress = addr;
    }

    @Override
    public void run()
    {
        byte[] byteArray = SerializableMessageUtil.wirteMessage(mMessage);

        if (null == byteArray)
        {
            return ;
        }

        try
        {
            mSocket.send(new DatagramPacket(byteArray, byteArray.length, mAddress));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
