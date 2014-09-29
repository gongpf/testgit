package com.sndf.connection.transmitter;

import java.nio.channels.SocketChannel;

public interface IMessageTransmitterFactory
{
    public AbstractMessageTransmitter createMessageTransmitter(SocketChannel socketChannel);
}
