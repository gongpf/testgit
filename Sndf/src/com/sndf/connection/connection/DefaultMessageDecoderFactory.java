package com.sndf.connection.connection;

import java.nio.channels.SocketChannel;

public class DefaultMessageDecoderFactory implements IMessageDecoderFactory
{
    @Override
    public AbstractMessageDecoder createMessageDecoder(SocketChannel socketChannel) 
    {
        return new DefaultMessageDecoder(socketChannel);
    };
}
