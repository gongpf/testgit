package com.sndf.connection.transmitter;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public abstract class AbstractMessageTransmitter implements IMessageTransmitter
{
    protected final SocketChannel mSocketChannel;

    protected final ByteBuffer mSocketBuffer;

    public AbstractMessageTransmitter(SocketChannel socketChannel)
    {
        mSocketChannel = socketChannel;
        mSocketBuffer = ByteBuffer.allocate(1024);
    }

}
