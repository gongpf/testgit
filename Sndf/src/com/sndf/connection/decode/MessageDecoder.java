package com.sndf.connection.decode;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class MessageDecoder implements IStreamDecoder<IMessage>
{
    @Override
    public IMessage decode(byte[] buffer, int offset, int length)
    {
        return SerializableMessageUtil.readMessage(buffer, offset, length);
    }
}
