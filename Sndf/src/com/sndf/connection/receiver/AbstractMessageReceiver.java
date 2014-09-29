package com.sndf.connection.receiver;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class AbstractMessageReceiver implements IMessageReceiver
{
    protected final SocketChannel mSocketChannel;
    
    protected final ByteBuffer mSocketBuffer;

    public AbstractMessageReceiver(SocketChannel socketChannel)
    {
        mSocketChannel = socketChannel;
        mSocketBuffer = ByteBuffer.allocate(1024);
    }
}
