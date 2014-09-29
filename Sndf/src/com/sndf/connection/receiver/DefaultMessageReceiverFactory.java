package com.sndf.connection.receiver;

import java.nio.channels.SocketChannel;

public class DefaultMessageReceiverFactory implements IMessageReceiverFactory
{
    @Override
    public AbstractMessageReceiver createMessageReceiver(SocketChannel socketChannel) 
    {
        return new DefaultMessageReceiver(socketChannel);
    };
}
