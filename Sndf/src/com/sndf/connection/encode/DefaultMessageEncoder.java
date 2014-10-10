package com.sndf.connection.encode;

import java.nio.ByteBuffer;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class DefaultMessageEncoder implements IStreamEncoder<IMessage>
{
    @Override
    public boolean encode(IMessage msg, ByteBuffer toBuffer)
    {
        byte[] result = SerializableMessageUtil.wirteMessage(msg);
        
        if (null == result)
        {
            return false;
        }

        toBuffer.clear();
        toBuffer.putInt(result.length);
        toBuffer.put(result);
        toBuffer.flip();
        
        return true;
    }
}
