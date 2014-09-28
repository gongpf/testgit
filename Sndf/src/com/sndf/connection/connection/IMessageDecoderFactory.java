package com.sndf.connection.connection;

import java.nio.channels.SocketChannel;

public interface IMessageDecoderFactory
{
    public AbstractMessageDecoder createMessageDecoder(SocketChannel socketChannel);
}
