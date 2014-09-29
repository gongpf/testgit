package com.sndf.connection.receiver;

import java.io.IOException;

import com.sndf.connection.message.IMessage;

public interface IMessageReceiver
{
    public IMessage receiveMessage() throws IOException;
}
