package com.sndf.connection.connection;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class AbstractMessageDecoder implements IMessageDecoder
{
    protected final SocketChannel mSocketChannel;
    
    protected final ByteBuffer mSocketBuffer;

    public AbstractMessageDecoder(SocketChannel socketChannel)
    {
        mSocketChannel = socketChannel;
        mSocketBuffer = ByteBuffer.allocate(1024);
    }
}
