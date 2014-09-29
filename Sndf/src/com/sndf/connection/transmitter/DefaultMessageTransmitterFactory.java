package com.sndf.connection.transmitter;

import java.nio.channels.SocketChannel;

public class DefaultMessageTransmitterFactory implements IMessageTransmitterFactory
{
    @Override
    public AbstractMessageTransmitter createMessageTransmitter(SocketChannel socketChannel)
    {
        return new DefaultMessageTransmitter(socketChannel);
    }
}
