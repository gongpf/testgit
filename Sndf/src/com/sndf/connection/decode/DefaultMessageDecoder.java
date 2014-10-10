package com.sndf.connection.decode;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.sndf.connection.message.IMessage;

public class DefaultMessageDecoder implements IStreamDecoderEx<IMessage>
{
    private final ByteBuffer mTempBuffer;
    private IStreamDecoder<IMessage> mMessageDecoder;
    
    public DefaultMessageDecoder(IStreamDecoder<IMessage> messageDecoder)
    {
        mTempBuffer = ByteBuffer.wrap(new byte[1024 * 1024]);
        mMessageDecoder = messageDecoder;
    }

    @Override
    public List<IMessage> decode(byte[] buffer, int offset, int length)
    {
        mTempBuffer.put(buffer, 0, length);
        mTempBuffer.flip();
        List<IMessage> result = new ArrayList<IMessage>();
        
        while (true)
        {
            if (mTempBuffer.remaining() < 4)
            {
                break;
            }

            mTempBuffer.mark();
            int messageLength = mTempBuffer.getInt();

            if (mTempBuffer.remaining() < messageLength)
            {
                mTempBuffer.reset();
                break;
            }

            IMessage msg = mMessageDecoder.decode(mTempBuffer.array(), mTempBuffer.position(), messageLength);
            mTempBuffer.position(mTempBuffer.position() + messageLength);
            
            if (null != msg)
            {
                result.add(msg);
            }
        }

        mTempBuffer.compact();
        return result;
    }
}
