package com.sndf.connection.connection;

import java.io.IOException;

import com.sndf.connection.message.IMessage;

public interface IMessageDecoder
{
    public IMessage readMessage() throws IOException;
}
