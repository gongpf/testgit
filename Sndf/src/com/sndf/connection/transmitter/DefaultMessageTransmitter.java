package com.sndf.connection.transmitter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.BytesBuffer;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class DefaultMessageTransmitter extends AbstractMessageTransmitter
{
    private BytesBuffer mTempBuffer;

    public DefaultMessageTransmitter(SocketChannel socketChannel)
    {
        super(socketChannel);
        mTempBuffer = new BytesBuffer();
    }

    @Override
    public void sendMessage(IMessage msg)
    {
        byte[] result = SerializableMessageUtil.wirteMessage(msg);
        
        if (null == result)
        {
            return;
        }
        
        BytesBuffer bytesBuffer = mTempBuffer;
        bytesBuffer.clear();

        bytesBuffer.appendInt(result.length);
        bytesBuffer.appendBytes(result);

        ByteBuffer writeBuffer = mSocketBuffer;
        writeBuffer.clear();
        
        writeBuffer.put(bytesBuffer.getBytes(), 0, bytesBuffer.getLength());
        writeBuffer.flip();

        try
        {
            mSocketChannel.write(writeBuffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
