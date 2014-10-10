package com.sndf.connection.decode;

import com.sndf.connection.message.IMessage;


public class DefaultMessageDecoderFactory implements IStreamDecoderExFactory<IMessage>
{
    @Override
    public IStreamDecoderEx<IMessage> createStreamDecoder()
    {
        return new DefaultMessageDecoder(new MessageDecoder());
    }
}
