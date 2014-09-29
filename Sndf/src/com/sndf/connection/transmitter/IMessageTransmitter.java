package com.sndf.connection.transmitter;

import com.sndf.connection.message.IMessage;

public interface IMessageTransmitter
{
    public void sendMessage(IMessage msg);

}
