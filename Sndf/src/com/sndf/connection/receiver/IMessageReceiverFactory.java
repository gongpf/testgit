package com.sndf.connection.receiver;

import java.nio.channels.SocketChannel;

public interface IMessageReceiverFactory
{
    public AbstractMessageReceiver createMessageReceiver(SocketChannel socketChannel);
}
