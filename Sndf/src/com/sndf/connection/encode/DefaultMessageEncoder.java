package com.sndf.connection.encode;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.BytesConverteUtil;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class DefaultMessageEncoder implements IStreamEncoder<IMessage>
{
    @Override
    public byte[] encode(IMessage msg)
    {
        byte[] bytes = SerializableMessageUtil.wirteMessage(msg);
        
        if (null == bytes || 0 == bytes.length)
        {
            return null;
        }
        
        byte[] length = BytesConverteUtil.int2bytesOrderBy(bytes.length);
        byte[] result = new byte[length.length + bytes.length];
        
        System.arraycopy(length, 0, result, 0, length.length);
        System.arraycopy(bytes, 0, result, length.length, bytes.length);
        
        return result;
    }
}
