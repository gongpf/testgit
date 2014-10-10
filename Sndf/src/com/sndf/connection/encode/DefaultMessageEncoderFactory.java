package com.sndf.connection.encode;

import com.sndf.connection.message.IMessage;

public class DefaultMessageEncoderFactory implements IStreamEncoderFactory<IMessage>
{
    @Override
    public IStreamEncoder<IMessage> createSteamEncoder()
    {
        return new DefaultMessageEncoder();
    }
}
